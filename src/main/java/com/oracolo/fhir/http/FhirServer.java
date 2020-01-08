package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.operation.OperationHandler;
import com.oracolo.fhir.handlers.query.QueryHandler;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.backboneelements.BundleRequest;
import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.OperationOutcomeIssue;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.model.resources.Bundle;
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

    restApi.post("/" + FhirUtils.BASE)
      .consumes(FhirHttpHeader.APPLICATION_JSON.value())
      .consumes(FhirHttpHeader.APPLICATION_JSON_VERSION.value())
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeader.APPLICATION_JSON.value())
      .produces(FhirHttpHeader.APPLICATION_JSON.value())
      .produces("*/json")
      .produces("*/xml")
      .handler(this::handleBatchOperations)
      .failureHandler(this::errorHandler);

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
      restApi.delete("/" + FhirUtils.BASE + "/" + type.typeName() + "/:" + FhirUtils.ID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .produces(HttpHeaderValues.TEXT_PLAIN.toString())
        .handler(routingContext -> handleResourceDelete(routingContext, type))
        .failureHandler(this::errorHandler);
      //create
      restApi.post("/" + FhirUtils.BASE + "/" + type.typeName())
        .consumes(FhirHttpHeader.APPLICATION_JSON.value())
        .consumes(FhirHttpHeader.APPLICATION_JSON_VERSION.value())
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceCreate(routingContext, type))
        .failureHandler(this::errorHandler);
      //search
      restApi.get("/" + FhirUtils.BASE + "/" + type.typeName())
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceSearch(routingContext, type))
        .failureHandler(this::errorHandler);
      //read
      restApi.get("/" + FhirUtils.BASE + "/" + type.typeName() + "/:" + FhirUtils.ID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceRead(routingContext, type))
        .failureHandler(this::errorHandler);
      //vread
      restApi.get("/" + FhirUtils.BASE + "/" + type.typeName() + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces(FhirHttpHeader.APPLICATION_JSON.value())
        .produces("*/json")
        .produces("*/xml")
        .handler(routingContext -> handleResourceVersionRead(routingContext, type))
        .failureHandler(this::errorHandler);
      //update as create
      restApi.put("/" + FhirUtils.BASE + "/" + type.typeName() + "/:" + FhirUtils.ID)
        .consumes(FhirHttpHeader.APPLICATION_JSON.value())
        .consumes(FhirHttpHeader.APPLICATION_JSON_VERSION.value())
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
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .createResponseAsync(serverResponse, (service, promise) ->
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
      .put("id", id)
      .put("meta.versionId", vId);
    OperationHandler
      .createReadOperationHandler()
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .createResponseAsync(serverResponse, (service, promise) ->
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

    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    JsonObject resourceJson = routingContext.getBodyAsJson();

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    resourceJson.put("id", newId);
    Metadata meta = new Metadata()
      .setVersionId(newVersionId)
      .setLastUpdated(Instant.now());
    resourceJson.put("meta", JsonObject.mapFrom(meta));
    String collection = type.getCollection();
    String acceptableType = routingContext.getAcceptableContentType();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    //validation
    ValidationHandler validationHandler = ValidationHandler.createValidator();
    boolean isValidFhirResource = validationHandler.validateAgainstJsonSchema(resourceJson);
    boolean isValidFhirClass = validationHandler.validateAgainstClass(resourceJson, type.getResourceClass());
    //if is not valid
    HttpServerResponse serverResponse = routingContext.response();
    if (!isValidFhirClass || !isValidFhirResource) {
      routingContext.put("error", "Not valid resource");
      routingContext.put("code", "invariant");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    } else {
      FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
      String preferHeader = routingContext.request().headers().get(FhirHttpHeader.PREFER);
      FhirHttpHeader prefer = FhirHttpHeader.fromPreferString(preferHeader);
      OperationHandler
        .createUpdateCreateOperationHandler()
        .setService(databaseService)
        .withResponseFormat(new ResponseFormat()
          .withAcceptHeader(accept)
          .withPreferHeader(prefer))
        .createResponseAsync(serverResponse, (service, promise) -> service.createUpdateResource(collection, resourceJson, promise))
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
            routingContext.put("code", "exception");
            routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
          }
        });
    }


  }

  private void handleResourceUpdate(RoutingContext routingContext, ResourceType type) {
    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject resourceJson = routingContext.getBodyAsJson();
    String id = resourceJson.getString(FhirUtils.ID);
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
      resourceJson.put("meta", JsonObject.mapFrom(meta));
      //Prefer header, response object depends on its value
      //Db operation using service proxy
      String acceptableType = routingContext.getAcceptableContentType();
      if (acceptableType == null) {
        acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
      }
      ValidationHandler validationHandler = ValidationHandler.createValidator();
      boolean isValidFhirResource = validationHandler.validateAgainstJsonSchema(resourceJson);
      boolean isValidFhirClass = validationHandler.validateAgainstClass(resourceJson, type.getResourceClass());
      HttpServerResponse serverResponse = routingContext.response();
      //if is not valid
      if (!isValidFhirClass || !isValidFhirResource) {
        routingContext.put("error", "Not valid resource");
        routingContext.put("code", "invariant");
        routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
      } else {
        FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
        String preferHeader = routingContext.request().headers().get(FhirHttpHeader.PREFER);
        FhirHttpHeader prefer = FhirHttpHeader.fromPreferString(preferHeader);
        OperationHandler
          .createUpdateCreateOperationHandler()
          .setService(databaseService)
          .withResponseFormat(new ResponseFormat()
            .withAcceptHeader(accept)
            .withPreferHeader(prefer))
          .createResponseAsync(serverResponse, (service, promise)
            -> service.createUpdateResource(collection, resourceJson, promise))
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

    HttpServerResponse serverResponse = routingContext.response();
    OperationHandler
      .createDeleteOperationHandler()
      .setService(databaseService)
      .createResponseAsync(serverResponse, (service, promise) ->
        service.createDeletedResource(collection, query, promise))
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

//    fetchResource
//      .future()
//      //if the last updated patient has tag DELETED in meta, it still succedes
//      .onSuccess(jsonObject -> {
//        String newVersionId = UUID.randomUUID().toString();
//        Metadata meta = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class)
//          .setLastUpdated(Instant.now())
//          .setVersionId(newVersionId)
//          .addNewTag(FhirUtils.DELETED);
//        jsonObject.put("meta", JsonObject.mapFrom(meta));
//
//      }).onFailure(throwable -> {
//      if (throwable instanceof ServiceException) {
//
//        int code = ((ServiceException) throwable).failureCode();
//        String message = throwable.getMessage();
//        ErrorFormat errorFormat = ErrorFormat.createFormat(code);
//        routingContext.put("error", message);
//        routingContext.put("code", errorFormat.getFhirErrorCode());
//        routingContext.fail(code);
//      } else {
//        routingContext.put("error", throwable.getMessage());
//        routingContext.put("code", "invariant");
//        routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
//      }
//
//    });

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
      .fromResourceType(type)
      .query(queryParams)
      .createMongoDbQuery();
    HttpServerResponse serverResponse = routingContext.response();
    OperationHandler
      .createSearchOperationHandler()
      .setService(databaseService)
      .withResponseFormat(new ResponseFormat()
        .withAcceptHeader(accept))
      .createResponseAsync(serverResponse, (service, promise)
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

  private void handleBatchOperations(RoutingContext routingContext) {

    JsonObject bundleJsonObject = routingContext.getBodyAsJson();
    ValidationHandler validationHandler = ValidationHandler.createValidator();
    boolean isValidFhirResource = validationHandler.validateAgainstJsonSchema(bundleJsonObject);
    boolean isValidFhirClass = validationHandler.validateAgainstClass(bundleJsonObject, Bundle.class);
    if (!isValidFhirClass || !isValidFhirResource) {
      routingContext.put("error", "Not valid resource");
      routingContext.put("code", "invariant");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }
    String acceptableType = routingContext.getAcceptableContentType();
    if (acceptableType == null) {
      acceptableType = FhirHttpHeader.APPLICATION_JSON.value();
    }
    FhirHttpHeader accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, acceptableType);
    String preferHeader = routingContext.request().headers().get(FhirHttpHeader.PREFER);
    FhirHttpHeader prefer = FhirHttpHeader.fromPreferString(preferHeader);

    Bundle bundle = Json.decodeValue(bundleJsonObject.encodePrettily(), Bundle.class);

    bundle.getEntry()
      .forEach(bundleEntry -> {
        BundleRequest bundleRequest = bundleEntry.getRequest();
        String method = bundleRequest.getMethod();
        switch (method) {
          case "GET":
            String url = bundleRequest.getUrl();
            String resourceType = url.split("/")[1];

        }
      });

  }
}
