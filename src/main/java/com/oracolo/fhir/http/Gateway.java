package com.oracolo.fhir.http;

import com.oracolo.fhir.database.delete.DeleteDatabaseService;
import com.oracolo.fhir.database.user.UserDatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
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
import utils.FhirUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

public class Gateway extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(Gateway.class.getName());
  private UserDatabaseService userService;
  private DeleteDatabaseService deleteService;


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
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientRead);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientVersionRead);
    restApi.post("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientCreate)
      .failureHandler(this::errorHandler);
    restApi.put("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .handler(this::handlePatientUpdate)
      .failureHandler(this::errorHandler);
    //Note that irrespective of this rule, servers are free to completely delete the resource and
    // it's history if policy or business rules make this the appropriate action to take.
    restApi.delete("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID)
      .handler(this::handlePatientDelete);
    vertx.createHttpServer().requestHandler(restApi).listen(8000, http -> {
      if (http.succeeded()) {
        this.userService = UserDatabaseService.createProxy(vertx, FhirUtils.USER_SERVICE_ADDRESS);
        this.deleteService = DeleteDatabaseService.createProxy(vertx, FhirUtils.DELETE_SERVICE_ADDRESS);
        LOGGER.info(deploymentID() + ".server listening at port " + http.result().actualPort());
        startPromise.complete();
      } else {
        startPromise.fail(http.cause());
      }
    });


  }

  private void handlePatientDelete(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    //Step 1: find all resource with id
    Promise<JsonArray> fetchAllPatientsToDeletePromise = Promise.promise();
    //Step 2: insert all patients in delete collection
    Promise<Void> insertAllPatientsInDeleteCollectionPromise = Promise.promise();
    //Step 3: remove all patients from their collection
    Promise<Void> deletePromise = Promise.promise();
    this.userService.fetchAllPatient(id, fetchAllPatientsToDeletePromise);
    fetchAllPatientsToDeletePromise.future().compose(patients -> {

      this.deleteService.insertDocuments(patients, insertAllPatientsInDeleteCollectionPromise);
      return insertAllPatientsInDeleteCollectionPromise.future();

    }).compose(safeDelete -> {

      this.deleteService.deleteAllResourceById(FhirUtils.PATIENTS_COLLECTION, id, deletePromise);
      return deletePromise.future();

    }).setHandler(deleteResult -> {
      //always successful even in the case the resource does not exist
      HttpServerResponse response = routingContext.response();
      response
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end();

    });
  }

  private void handlePatientUpdate(RoutingContext routingContext) {

    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject patientJson = routingContext.getBodyAsJson();
    String id = patientJson.getString(FhirUtils.ID);
    String pathId = routingContext.pathParam(FhirUtils.ID);
    if (id != null && id.equals(pathId)) {
      try {

        FhirUtils.validateJsonAgainstSchema(patientJson);

      } catch (NotValideFhirResourceException e) {
        e.printStackTrace();
        routingContext.put("error", "Not a valid Fhir Resource");
        routingContext.put("code", "invariant");
        routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
      }
      //Prefer header, response object depends on its value
      String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);
      //If the request body includes a meta, the server SHALL ignore the provided versionId and lastUpdated values.
      // If the server supports versions, it SHALL populate the meta.versionId and meta.lastUpdated with the new correct values.
      String newVersionId = UUID.randomUUID().toString();
      Instant lastUpdated = Instant.now();
      Metadata metadata = new Metadata()
        .setLastUpdated(lastUpdated)
        .setVersionId(newVersionId);
      patientJson.put("meta", JsonObject.mapFrom(metadata));

      Promise<JsonObject> updatePromise = Promise.promise();
      this.userService.createOrUpdatePatientResource(patientJson, updatePromise);
      updatePromise.future().setHandler(asyncResult -> {
        if (asyncResult.succeeded()) {

          JsonObject dbResult = asyncResult.result();
          Patient clientPatient = Json.decodeValue(dbResult.encode(), Patient.class);
          String lastModified = clientPatient.getMeta().getLastUpdated().toString();
          String versionId = clientPatient.getMeta().getVersionId();

          //creates response based on prefer header;
          HttpServerResponse response = routingContext.response();
          response.setStatusCode(HttpResponseStatus.CREATED.code());
          response.putHeader(HttpHeaderNames.ETAG, versionId);
          response.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
          response.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + versionId);
          response.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
          FhirUtils.createPostResponseBasedOnPreferHeader(preferHeader, dbResult, response);
        } else {
          FhirUtils.createPostRequestErrorResponse(routingContext.response(), asyncResult.cause().getMessage());
        }
      });


    } else {
      routingContext.put("error", "Incorrect resource: resource's id does not match with path id");
      routingContext.put("code", "business-rule");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }

  }


  private void errorHandler(RoutingContext routingContext) {
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

  private void handlePatientCreate(RoutingContext routingContext) {


    //still to be supported

    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);

    JsonObject patientJson = routingContext.getBodyAsJson();

    //validation

    try {

      FhirUtils.validateJsonAgainstSchema(patientJson);

    } catch (NotValideFhirResourceException e) {
      e.printStackTrace();
      routingContext.put("error", "Not a valid Fhir Resource");
      routingContext.put("code", "invariant");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    patientJson.put("id", newId);
    Metadata meta = new Metadata().setVersionId(newVersionId).setLastUpdated(Instant.now());
    patientJson.put("meta", JsonObject.mapFrom(meta));
    //Prefer header, response object depends on its value
    String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);
    //Db operation using service proxy
    Promise<JsonObject> jsonObjectPromise = Promise.promise();

    this.userService.createOrUpdatePatientResource(patientJson, jsonObjectPromise);
    jsonObjectPromise.future().setHandler(asyncResult -> {
      if (asyncResult.succeeded()) {
        Patient clientPatient = Json.decodeValue(asyncResult.result().encode(), Patient.class);
        String lastModified = clientPatient.getMeta().getLastUpdated().toString();
        String versionId = clientPatient.getMeta().getVersionId();
        String id = clientPatient.getId();
        //creates response based on prefer header;
        HttpServerResponse response = routingContext.response();
        response.setStatusCode(HttpResponseStatus.CREATED.code());
        response.putHeader(HttpHeaderNames.ETAG, versionId);
        response.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
        response.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + versionId);
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
        FhirUtils.createPostResponseBasedOnPreferHeader(preferHeader, asyncResult.result(), response);
      } else {
        FhirUtils.createPostRequestErrorResponse(routingContext.response(), asyncResult.cause().getMessage());
      }

    });


  }


  private void handleGetAllPatient(RoutingContext routingContext) {
    MultiMap queryParams = routingContext.queryParams();
    queryParams.forEach(entry -> {
      String key = entry.getKey();//validate key if is valid
      String value = entry.getValue();
      String[] values = value.split(",");
    });
  }


  private void handlePatientVersionRead(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    String vId = routingContext.pathParam(FhirUtils.PATH_VERSIONID);
    if (vId == null) {
      routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .end(FhirUtils.GENERAL_PATH_PARAMETER_ERROR);
    }
    //still to be supported

    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);

    Promise<JsonObject> versionedPatientJsonObjectPromise = Promise.promise();
    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();

    deleteService.findDeleteDocument(id, checkIfDeletedPromise);

    checkIfDeletedPromise.future()
      .onFailure(noDocumentsInDeleteCollection -> {
        userService.fetchPatientVersion(id, vId, new ArrayList<>(), versionedPatientJsonObjectPromise);
      })
      .onSuccess(patientWasDeleted -> {
        routingContext.response().setStatusCode(HttpResponseStatus.GONE.code())
          .end("Patient was deleted");
      });

    versionedPatientJsonObjectPromise.future().setHandler(asyncResult -> {
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
    });
  }


  //Does not support conditional read
  private void handlePatientRead(RoutingContext routingContext) {

    String id = routingContext.pathParam(FhirUtils.ID);
    if (id == null) {
      routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .end(FhirUtils.GENERAL_PATH_PARAMETER_ERROR);
    } else {
      MultiMap headers = routingContext.request().headers();
      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(FhirUtils.FORMAT);
      String pretty = queryParams.get(FhirUtils.PRETTY);
      String summary = queryParams.get(FhirUtils.SUMMARY);
      String element = queryParams.get(FhirUtils.ELEMENTS);

      //still to be supported

      Promise<JsonArray> allPatientsPromise = Promise.promise();
      //If resource has been deleted, need to respond with http status 410
      Promise<JsonObject> checkIfDeletedPromise = Promise.promise();

      deleteService.findDeleteDocument(id, checkIfDeletedPromise);
      HttpServerResponse serverResponse = routingContext.response();

      checkIfDeletedPromise.future()
        //If no resource has been found in delete collections, it is possible in patients. If not in patients, it means it has never been
        //created in the first place
        .onFailure(noResourceInDeleteCollection -> {
          userService.fetchAllPatient(id, allPatientsPromise);
        })
        //if it has been found in delete, respond with GONE status
        .onSuccess(resourceInCollection -> {
          serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
            .end();
        });

      allPatientsPromise.future().setHandler(asyncResult -> {

        try {
          if (asyncResult.succeeded()) {
            JsonArray dbResultArray = asyncResult.result();
            Patient lastUpdated = dbResultArray.stream().map(patientObject -> {
              JsonObject patientJson = JsonObject.mapFrom(patientObject);
              return Json.decodeValue(patientJson.encode(), Patient.class);
            }).min((o1, o2) -> o2.getMeta().getLastUpdated().compareTo(o1.getMeta().getLastUpdated()))
              .get();


            String lastModified = lastUpdated.getMeta().getLastUpdated().toString();
            String versionId = lastUpdated.getMeta().getVersionId();

            serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
            serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
            serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);

            serverResponse.setStatusCode(HttpResponseStatus.OK.code()).
              end(JsonObject.mapFrom(lastUpdated).encodePrettily());
          } else {
            serverResponse.setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
              .end(asyncResult.cause().getMessage());
          }
        } catch (NullPointerException | NoSuchElementException e) {
          serverResponse.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("No Resource Found");
        }

      });

    }
  }
}
