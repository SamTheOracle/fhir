package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.operation.OperationHandler;
import com.oracolo.fhir.handlers.query.QueryHandler;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.OperationOutcomeIssue;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.utils.ErrorFormat;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResponseFormat;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.serviceproxy.ServiceException;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

public class FhirServer extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(FhirServer.class.getName());

  private DatabaseService databaseService;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create());

    loadRoutes(restApi);

    createAPIServer(0, restApi)
      .compose(httpServer -> {
        int port = httpServer.actualPort();
        LOGGER.info(deploymentID() + ".server listening at port " + port);
        return publishHTTPEndPoint(port, FhirUtils.FHIR_SERVICE, FhirUtils.LOCALHOST, FhirUtils.BASE);
      }).setHandler(publishSuccessful -> {
      if (publishSuccessful.succeeded()) {
        this.databaseService = DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS);

        startPromise.complete();
      } else {
        startPromise.fail(publishSuccessful.cause());
      }
    });
  }

  private void loadRoutes(Router restApi) {
    for (ResourceType type : ResourceType.values()) {
      //delete
      restApi.delete("/" + FhirUtils.BASE + "/" + type.toString() + "/:" + FhirUtils.ID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .produces(HttpHeaderValues.TEXT_PLAIN.toString())
        .handler(routingContext -> handleResourceDelete(routingContext, type))
        .failureHandler(this::errorHandler);
      //create
      restApi.post("/" + FhirUtils.BASE + "/" + type.toString())
        .consumes(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceCreate(routingContext, type))
        .failureHandler(this::errorHandler);
      //search
      restApi.get("/" + FhirUtils.BASE + "/" + type.toString())
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceSearch(routingContext, type))
        .failureHandler(this::errorHandler);
      //read
      restApi.get("/" + FhirUtils.BASE + "/" + type.toString() + "/:" + FhirUtils.ID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceRead(routingContext, type))
        .failureHandler(this::errorHandler);
      //vread
      restApi.get("/" + FhirUtils.BASE + "/" + type.toString() + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceVersionRead(routingContext, type))
        .failureHandler(this::errorHandler);
      //update as create
      restApi.put("/" + FhirUtils.BASE + "/" + type.toString() + "/:" + FhirUtils.ID)
        .consumes(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceUpdate(routingContext, type))
        .failureHandler(this::errorHandler);
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
      .putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeader.APPLICATION_JSON.value())
      .setStatusCode(routingContext.statusCode())
      .end(JsonObject.mapFrom(operationOutcome).encodePrettily());
  }

  private void handleResourceRead(RoutingContext routingContext, ResourceType type) {
    String id = routingContext.pathParam(FhirUtils.ID);
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);

    //still to be supported

    //If resource has been deleted, need to respond with http status 410

    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id);
    String acceptableType = routingContext.getAcceptableContentType();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
    String collection = type.getCollection();
    OperationHandler
      .createReadOperationHandler()
      .setResponse(serverResponse)
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .writeResponseBodyAsync((service, promise) ->
        service.fetchDomainResourceWithQuery(collection, query, null, promise))
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          int code = ((ServiceException) throwable).failureCode();
          String message = throwable.getMessage();
          ErrorFormat errorFormat = ErrorFormat.createFormat(code);
          routingContext.put("error", message);
          routingContext.put("code", errorFormat.getFhirErrorCode());
          routingContext.fail(code);

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }

        //Check if the documents have been deleted


      });
  }

  private void handleResourceVersionRead(RoutingContext routingContext, ResourceType type) {
    String id = routingContext.pathParam(FhirUtils.ID);
    String vId = routingContext.pathParam(FhirUtils.PATH_VERSIONID);
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    //still to be supported

    //If resource has been deleted, need to respond with http status 410
    String collection = type.getCollection();
    String acceptableType = routingContext.getAcceptableContentType();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id);
    OperationHandler
      .createReadOperationHandler()
      .setResponse(serverResponse)
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .writeResponseBodyAsync((service, promise) ->
        service.fetchDomainResourceWithQuery(collection, query, null, promise))
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          int code = ((ServiceException) throwable).failureCode();
          String message = throwable.getMessage();
          ErrorFormat errorFormat = ErrorFormat.createFormat(code);
          routingContext.put("error", message);
          routingContext.put("code", errorFormat.getFhirErrorCode());
          routingContext.fail(code);

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });
  }

  private void handleResourceCreate(RoutingContext routingContext, ResourceType type) {


    //still to be supported

    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    JsonObject patientJson = routingContext.getBodyAsJson();

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    patientJson.put("id", newId);
    Metadata meta = new Metadata()
      .setVersionId(newVersionId)
      .setLastUpdated(Instant.now());
    patientJson.put("meta", JsonObject.mapFrom(meta));
    String collection = type.getCollection();
    String acceptableType = routingContext.getAcceptableContentType();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
    String preferHeader = routingContext.request().headers().get(FhirHttpHeader.PREFER);
    FhirHttpHeader prefer = FhirHttpHeader.fromPreferString(preferHeader);
    OperationHandler
      .createUpdateCreateOperationHandler(ValidationHandler.from(type))
      .validate(patientJson)
      .validateAgainstClass(patientJson)
      .setResponse(routingContext.response())
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept)
        .withPreferHeader(prefer))
      .writeResponseBodyAsync((service, promise) -> service.createOrUpdateDomainResource(collection, patientJson, promise))
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        if (throwable instanceof ServiceException) {
          int code = ((ServiceException) throwable).failureCode();
          String message = throwable.getMessage();
          ErrorFormat errorFormat = ErrorFormat.createFormat(code);
          routingContext.put("error", message);
          routingContext.put("code", errorFormat.getFhirErrorCode());
          routingContext.fail(code);

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });

  }

  private void handleResourceUpdate(RoutingContext routingContext, ResourceType type) {
    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject bodyAsJson = routingContext.getBodyAsJson();
    String id = bodyAsJson.getString(FhirUtils.ID);
    String pathId = routingContext.pathParam(FhirUtils.ID);
    if (id != null && id.equals(pathId)) {

      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(FhirUtils.FORMAT);
      String pretty = queryParams.get(FhirUtils.PRETTY);
      String summary = queryParams.get(FhirUtils.SUMMARY);
      String element = queryParams.get(FhirUtils.ELEMENTS);

      String newVersionId = UUID.randomUUID().toString();
      String collection = type.getCollection();
      Metadata meta = new Metadata()
        .setVersionId(newVersionId)
        .setLastUpdated(Instant.now());
      bodyAsJson.put("meta", JsonObject.mapFrom(meta));
      //Prefer header, response object depends on its value
      //Db operation using service proxy
      String acceptableType = routingContext.getAcceptableContentType();
      if (acceptableType == null) {
        acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
      }
      FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
      String preferHeader = routingContext.request().headers().get(FhirHttpHeader.PREFER);
      FhirHttpHeader prefer = FhirHttpHeader.fromPreferString(preferHeader);
      OperationHandler
        .createUpdateCreateOperationHandler(ValidationHandler.from(type))
        .validate(bodyAsJson)
        .validateAgainstClass(bodyAsJson)
        .setResponse(routingContext.response())
        .setService(databaseService)
        .withResponseFormat(new ResponseFormat()
          .withAcceptHeader(accept)
          .withPreferHeader(prefer))
        .writeResponseBodyAsync((service, promise)
          -> service.createOrUpdateDomainResource(collection, bodyAsJson, promise))
        .releaseAsync()
        .future()
        .onSuccess(HttpServerResponse::end)
        .onFailure(throwable -> {

          if (throwable instanceof ServiceException) {
            int code = ((ServiceException) throwable).failureCode();
            String message = throwable.getMessage();
            ErrorFormat errorFormat = ErrorFormat.createFormat(code);
            routingContext.put("error", message);
            routingContext.put("code", errorFormat.getFhirErrorCode());
            routingContext.fail(code);

          } else {
            routingContext.put("error", throwable.getMessage());
            routingContext.put("code", "invariant");
            routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
          }
        });


    } else {
      routingContext.put("error", "Incorrect resource: resource's id does not match with path id");
      routingContext.put("code", "business-rule");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  private void handleResourceDelete(RoutingContext routingContext, ResourceType type) {
    String id = routingContext.pathParam(FhirUtils.ID);
    String collection = type.getCollection();
    JsonObject query = new JsonObject()
      .put("id", id);
    //Step 1: find last modified resource
    Promise<JsonObject> fetchResource = Promise.promise();
    databaseService.fetchDomainResourceWithQuery(collection, query, null, fetchResource);
    //Step 2: insert all patients in delete collection
    //Step 3: remove all patients from their collection

    fetchResource
      .future()
      //if the last updated patient has tag DELETED in meta, it still succedes
      .onSuccess(jsonObject -> {
        String newVersionId = UUID.randomUUID().toString();
        Metadata meta = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class)
          .setLastUpdated(Instant.now())
          .setVersionId(newVersionId)
          .addNewTag(FhirUtils.DELETED);
        jsonObject.put("meta", JsonObject.mapFrom(meta));
        OperationHandler
          .createDeleteOperationHandler()
          .setResponse(routingContext.response())
          .setService(databaseService)
          .writeResponseBodyAsync((service, promise) ->
            service.createOrUpdateDomainResource(collection, jsonObject, promise))
          .releaseAsync()
          .future()
          .onSuccess(HttpServerResponse::end)
          .onFailure(throwable -> {
            if (throwable instanceof ServiceException) {
              int code = ((ServiceException) throwable).failureCode();
              String message = throwable.getMessage();
              ErrorFormat errorFormat = ErrorFormat.createFormat(code);
              routingContext.put("error", message);
              routingContext.put("code", errorFormat.getFhirErrorCode());
              routingContext.fail(code);

            } else {
              routingContext.put("error", throwable.getMessage());
              routingContext.put("code", "invariant");
              routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            }
          });
      }).onFailure(throwable -> {
      if (throwable instanceof ServiceException) {
        routingContext.response().end();

      } else {
        routingContext.put("error", throwable.getMessage());
        routingContext.put("code", "invariant");
        routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      }

    });

  }

  private void handleResourceSearch(RoutingContext routingContext, ResourceType type) {
    //need to support if-modified-since and if-none-match
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.request().params();
    String acceptableType = routingContext.getAcceptableContentType();
    String collection = type.getCollection();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);

    JsonObject query = QueryHandler
      .fromResourceType(type.toString())
      .query(queryParams)
      .createMongoDbQuery();

    OperationHandler
      .createSearchOperationHandler()
      .setService(databaseService)
      .setResponse(routingContext.response())
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .writeResponseBodyAsync((service, promise)
        -> service.fetchDomainResourcesWithQuery(collection, query, promise))
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          int code = ((ServiceException) throwable).failureCode();
          String message = throwable.getMessage();
          ErrorFormat errorFormat = ErrorFormat.createFormat(code);
          routingContext.put("error", message);
          routingContext.put("code", errorFormat.getFhirErrorCode());
          routingContext.fail(code);

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });
  }
}
