package com.oracolo.fhir.http;

import com.oracolo.fhir.database.UserDatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.domain.Patient;
import model.elements.Metadata;
import model.exceptions.NotValideFhirResourceException;
import utils.FhirHttpHeaderNames;
import utils.FhirHttpHeaderValues;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class Gateway extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(Gateway.class.getName());
  private UserDatabaseService userService;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create());
    restApi.get("/welcome")
      .handler(routingContext -> routingContext.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .setStatusCode(HttpResponseStatus.OK.code())
        .end("Welcome"));
    //Patient API
    restApi.route().handler(BodyHandler.create());
    restApi.get("/Patient/:" + Utils.PATH_ID).handler(this::handleGetPatient);
    restApi.get("/Patient/:" + Utils.PATH_ID + "/" + Utils.HISTORY + "/:" + Utils.PATH_VERSIONID).handler(this::handleConditionalPatientRead);
    restApi.get("/Patient").handler(this::handleGetAllPatient);
    restApi.post("/Patient")
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .handler(this::handleCreatePatient)
      .failureHandler(this::invalidResourceFailure);

    vertx.createHttpServer().requestHandler(restApi).listen(8000, http -> {
      if (http.succeeded()) {
        this.userService = UserDatabaseService.createProxy(vertx, Utils.SERVICE_ADDRESS);
        LOGGER.info(deploymentID() + ".server listening at port " + http.result().actualPort());
        startPromise.complete();
      } else {
        startPromise.fail(http.cause());
      }
    });

  }


  private void invalidResourceFailure(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    String errorMessage = routingContext.get("error");
    String code = routingContext.get("code");
    OperationOutcome operationOutcome = new OperationOutcome()
      .setIssue(new OperationOutcomeIssue()
        .setSeverity("error")
        .setCode(code)
        .setDiagnostics(errorMessage));
    response
      .putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON)
      .setStatusCode(routingContext.statusCode())
      .end(JsonObject.mapFrom(operationOutcome).encodePrettily());
  }

  private void handleCreatePatient(RoutingContext routingContext) {

//    if (routingContext.request().params().isEmpty()) {

    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(Utils.FORMAT);
    String pretty = queryParams.get(Utils.PRETTY);
    String summary = queryParams.get(Utils.SUMMARY);
    String element = queryParams.get(Utils.ELEMENTS);

    //still to be supported
    JsonObject patientJson = routingContext.getBodyAsJson();


    try {

      String newId = UUID.randomUUID().toString();
      String newVersionId = UUID.randomUUID().toString();
      //validation
      Utils.validateJsonAgainstSchema(patientJson);

      Patient clientPatient = Json.decodeValue(patientJson.encode(), Patient.class);
      clientPatient.setId(newId);
      Metadata meta = new Metadata().setVersionId(newVersionId).setLastUpdated(Instant.now());
      clientPatient.setMeta(meta);
      //Prefer header, response object depends on its value
      String preferHeader = routingContext.request().headers().get("Prefer");
      //Db operation using service proxy
      Promise<JsonObject> jsonObjectPromise = Promise.promise();

      this.userService.createNewPatientResource(JsonObject.mapFrom(clientPatient), jsonObjectPromise);
      jsonObjectPromise.future().setHandler(asyncResult -> {
        if (asyncResult.succeeded()) {

          JsonObject dbResult = asyncResult.result();
          //_id is a mongo db index set the same as resource id. The actual resource does not have it, so
          //it is necessary to remove it at runtime

          String lastModified = clientPatient.getMeta().getLastUpdated().toString();
          String versionId = clientPatient.getMeta().getVersionId();

          //creates response based on prefer header;
          HttpServerResponse response = routingContext.response();
          response.setStatusCode(HttpResponseStatus.CREATED.code());
          response.putHeader(HttpHeaderNames.ETAG, versionId);
          response.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
          response.putHeader(HttpHeaderNames.LOCATION, "/Patient/" + dbResult.getString("id") + "/_history/" + versionId);
          response.putHeader(HttpHeaderNames.CONTENT_TYPE,FhirHttpHeaderValues.APPLICATION_JSON);
          Utils.createPostResponseBasedOnPreferHeader(preferHeader, dbResult, response);
        } else {
          Utils.createPostRequestErrorResponse(routingContext.response(), asyncResult.cause().getMessage());
        }

      });
    } catch (DecodeException | IllegalArgumentException e) {
      e.printStackTrace();
      routingContext.put("error", e.getMessage());
      routingContext.put("code", "invariant");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    } catch (NotValideFhirResourceException e) {
      e.printStackTrace();
      routingContext.put("error", "Not a valid Fhir Resource");
      routingContext.put("code", "invariant");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }


  }
//    else{
//      routingContext.put("error", "Custom parameters or parameter _format,_pretty,_summary,_elements not supported for POST requests");
//      routingContext.put("code","business-rule");
//      routingContext.fail(HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
//    }
//}

  private void handleGetAllPatient(RoutingContext routingContext) {
    MultiMap queryParams = routingContext.queryParams();
    queryParams.forEach(entry -> {
      String key = entry.getKey();//validate key if is valid
      String value = entry.getValue();
      String[] values = value.split(",");
    });
  }


  private void handleConditionalPatientRead(RoutingContext routingContext) {
    String id = routingContext.pathParam(Utils.PATH_ID);
    String vId = routingContext.pathParam(Utils.PATH_VERSIONID);
    if (vId == null) {
      routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .end(Utils.GENERAL_PATH_PARAMETER_ERROR);
    }
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(Utils.FORMAT);
    String pretty = queryParams.get(Utils.PRETTY);
    String summary = queryParams.get(Utils.SUMMARY);
    String element = queryParams.get(Utils.ELEMENTS);

    //still to be supported
    Promise<JsonObject> jsonObjectPromise = Promise.promise();

    userService.fetchPatientVersion(id, vId, new ArrayList<>(), jsonObjectPromise);
    jsonObjectPromise.future().setHandler(asyncResult -> {
      if (asyncResult.succeeded()) {

        Patient patient = Json.decodeValue(asyncResult.result().encode(), Patient.class);
        String lastModified = patient.getMeta().getLastUpdated().toString();
        String versionId = patient.getMeta().getVersionId();
        HttpServerResponse serverResponse = routingContext.response();
        serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
        serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
        serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, versionId);

        routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).
          end(asyncResult.result().encodePrettily());
      } else {
        routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
          .end(asyncResult.cause().getMessage());
      }
    });
  }


  private void handleGetPatient(RoutingContext routingContext) {

    String id = routingContext.pathParam(Utils.PATH_ID);
    if (id == null) {
      routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .end(Utils.GENERAL_PATH_PARAMETER_ERROR);
    } else {
      MultiMap headers = routingContext.request().headers();
      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(Utils.FORMAT);
      String pretty = queryParams.get(Utils.PRETTY);
      String summary = queryParams.get(Utils.SUMMARY);
      String element = queryParams.get(Utils.ELEMENTS);

      //still to be supported

      Promise<JsonObject> jsonObjectPromise = Promise.promise();

      userService.fetchPatient(id, new ArrayList<>(), jsonObjectPromise);
      jsonObjectPromise.future().setHandler(asyncResult -> {

        try {
          if (asyncResult.succeeded()) {


            Patient patient = Json.decodeValue(asyncResult.result().encode(), Patient.class);
            String lastModified = patient.getMeta().getLastUpdated().toString();
            String versionId = patient.getMeta().getVersionId();
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
            serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
            serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);

            routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).
              end(asyncResult.result().encodePrettily());
          } else {
            routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
              .end(asyncResult.cause().getMessage());
          }
        } catch (NullPointerException e) {
          routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("No Resource Found");
        }

      });

    }
  }
}
