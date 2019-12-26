package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.OperationHandler;
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
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.domain.Patient;
import model.elements.Metadata;
import utils.FhirHttpHeader;
import utils.FhirHttpHeaderNames;
import utils.FhirHttpHeaderValues;
import utils.FhirUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class FhirServer extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(FhirServer.class.getName());

  private DatabaseService databaseService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create());
    //Patient API
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientRead)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientVersionRead)
      .failureHandler(this::errorHandler);
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
      .handler(this::handlePatientDelete)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientSearch)
      .failureHandler(this::errorHandler);

    //OBSERVATION
    //Note that irrespective of this rule, servers are free to completely delete the resource and
    // it's history if policy or business rules make this the appropriate action to take.
    //For servers that maintain a version history, the delete interaction does not remove a resource's version history.
    // From a version history respect, deleting a resource is the equivalent of creating a special kind of history entry that has no content
    // and is marked as deleted. Note that there is no support for deleting past versions - see notes on the history interaction.
    // Since deleted resources may be brought back to life, servers MAY include an ETag on the delete response to allow version
    // contention management when a resource is brought back to life.
    restApi.delete("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID)
      .handler(this::handleObservationDelete)
      .failureHandler(this::errorHandler);
    restApi.post("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleObservationCreate)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleObservationSearch)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleObservationRead)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleObservationVersionRead)
      .failureHandler(this::errorHandler);
    restApi.put("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .handler(this::handleObservationUpdate)
      .failureHandler(this::errorHandler);

    //CONDITION
    //Note that irrespective of this rule, servers are free to completely delete the resource and
    // it's history if policy or business rules make this the appropriate action to take.
    restApi.delete("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/:" + FhirUtils.ID)
      .handler(this::handleConditionDelete)
      .failureHandler(this::errorHandler);
    restApi.post("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleConditionCreate)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleConditionSearch)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/:" + FhirUtils.ID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleConditionRead)
      .failureHandler(this::errorHandler);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleConditionVersionId)
      .failureHandler(this::errorHandler);
    restApi.put("/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/:" + FhirUtils.ID)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .handler(this::handleConditionUpdate)
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


  //Condition Crud
  private void handleConditionDelete(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    JsonObject query = new JsonObject()
      .put("id", id);
    //Step 1: find last modified resource
    Promise<JsonObject> fetchCondition = Promise.promise();
    databaseService.fetchDomainResourceWithQuery(FhirUtils.CONDITIONS_COLLECTION, query, null, fetchCondition);

    Promise<JsonObject> insertWithTagDeletedPromise = Promise.promise();

    fetchCondition
      .future()
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
          .executeDatabaseOperation(databaseService, insertWithTagDeletedPromise, (promise, service) ->
            service.createOrUpdateDomainResource(FhirUtils.CONDITIONS_COLLECTION, jsonObject, promise))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.ETAG.toString(), newVersionId))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/" + id + "/_history/" + newVersionId))
          .writeResponseBody()
          .releaseAsync()
          .future()
          .onSuccess(HttpServerResponse::end)
          .onFailure(throwable -> {
            routingContext.put("error", throwable.getMessage());
            routingContext.put("code", "invariant");
            routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
          });
      });
  }

  private void handleConditionUpdate(RoutingContext routingContext) {
    LOGGER.info("Trying update");
    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject conditionJson = routingContext.getBodyAsJson();
    String id = conditionJson.getString(FhirUtils.ID);
    String pathId = routingContext.pathParam(FhirUtils.ID);
    if (id != null && id.equals(pathId)) {

      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(FhirUtils.FORMAT);
      String pretty = queryParams.get(FhirUtils.PRETTY);
      String summary = queryParams.get(FhirUtils.SUMMARY);
      String element = queryParams.get(FhirUtils.ELEMENTS);
      String acceptableType = routingContext.getAcceptableContentType();
      String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

      String newVersionId = UUID.randomUUID().toString();


      Metadata meta = new Metadata()
        .setVersionId(newVersionId)
        .setLastUpdated(Instant.now());
      conditionJson.put("meta", JsonObject.mapFrom(meta));
      //Prefer header, response object depends on its value
      //Db operation using service proxy
      Promise<JsonObject> jsonObjectPromise = Promise.promise();
      OperationHandler
        .createUpdateCreateOperationHandler()
        .validate(conditionJson)
        .executeDatabaseOperation(databaseService, jsonObjectPromise,
          (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.PATIENTS_COLLECTION, conditionJson, promise))
        .setResponse(routingContext.response())
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.CONDITIONS_COLLECTION + "/" + id + "/_history/" + newVersionId))
        .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
        .writeResponseBody()
        .releaseAsync()
        .future()
        .onSuccess(HttpServerResponse::end)
        .onFailure(throwable -> {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        });


    } else {
      routingContext.put("error", "Incorrect resource: resource's id does not match with path id");
      routingContext.put("code", "business-rule");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  private void handleConditionVersionId(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    String vId = routingContext.pathParam(FhirUtils.PATH_VERSIONID);
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    //still to be supported

    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> conditionPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("meta.versionId", vId)
      .put("resourceType", FhirUtils.CONDITION_TYPE);


    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, conditionPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.CONDITION_TYPE,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });
  }

  private void handleConditionRead(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    //still to be supported

    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> conditionPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("resourceType", FhirUtils.CONDITION_TYPE);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, conditionPromise, (jsonObjectPromise, service) ->
        service.fetchDomainResourceWithQuery(FhirUtils.CONDITIONS_COLLECTION, query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });

  }

  private void handleConditionSearch(RoutingContext routingContext) {
    //still to be supported


  }

  private void handleConditionCreate(RoutingContext routingContext) {

    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    String acceptableType = routingContext.getAcceptableContentType();
    String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

    JsonObject conditionJson = routingContext.getBodyAsJson();

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    conditionJson.put("id", newId);
    Metadata meta = new Metadata()
      .setVersionId(newVersionId)
      .setLastUpdated(Instant.now());
    conditionJson.put("meta", JsonObject.mapFrom(meta));
    //Prefer header, response object depends on its value
    //Db operation using service proxy
    Promise<JsonObject> jsonObjectPromise = Promise.promise();

    OperationHandler
      .createUpdateCreateOperationHandler()
      .validate(conditionJson)
      .executeDatabaseOperation(databaseService, jsonObjectPromise, (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.CONDITIONS_COLLECTION, conditionJson, promise))
      .setResponse(routingContext.response())
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/" + newId + "/_history/" + newVersionId))
      .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .setHandler(asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpServerResponse serverResponse = asyncResult.result();
          serverResponse.end();
        } else {
          routingContext.put("error", asyncResult.cause().getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        }
      });
  }


  //Observation CRUD
  private void handleObservationDelete(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    JsonObject query = new JsonObject()
      .put("id", id);
    //Step 1: find last modified resource
    Promise<JsonObject> fetchObservation = Promise.promise();
    databaseService.fetchDomainResourceWithQuery(FhirUtils.OBSERVATIONS_COLLECTION, query, null, fetchObservation);
    //Step 2: insert all patients in delete collection
    //Step 3: remove all patients from their collection
    Promise<JsonObject> insertWithTagDeletedPromise = Promise.promise();

    fetchObservation
      .future()
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
          .executeDatabaseOperation(databaseService, insertWithTagDeletedPromise, (promise, service) ->
            service.createOrUpdateDomainResource(FhirUtils.OBSERVATIONS_COLLECTION, jsonObject, promise))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.ETAG.toString(), newVersionId))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/" + id + "/_history/" + newVersionId))
          .writeResponseBody()
          .releaseAsync()
          .future()
          .onSuccess(HttpServerResponse::end)
          .onFailure(throwable -> {
            routingContext.put("error", throwable.getMessage());
            routingContext.put("code", "invariant");
            routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
          });
      });
  }

  private void handleObservationUpdate(RoutingContext routingContext) {
    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject observationJson = routingContext.getBodyAsJson();
    String id = observationJson.getString(FhirUtils.ID);
    String pathId = routingContext.pathParam(FhirUtils.ID);
    if (id != null && id.equals(pathId)) {

      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(FhirUtils.FORMAT);
      String pretty = queryParams.get(FhirUtils.PRETTY);
      String summary = queryParams.get(FhirUtils.SUMMARY);
      String element = queryParams.get(FhirUtils.ELEMENTS);
      String acceptableType = routingContext.getAcceptableContentType();
      String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

      String newVersionId = UUID.randomUUID().toString();


      Metadata meta = new Metadata()
        .setVersionId(newVersionId)
        .setLastUpdated(Instant.now());
      observationJson.put("meta", JsonObject.mapFrom(meta));
      //Prefer header, response object depends on its value
      //Db operation using service proxy
      Promise<JsonObject> jsonObjectPromise = Promise.promise();
      OperationHandler
        .createUpdateCreateOperationHandler()
        .validate(observationJson)
        .executeDatabaseOperation(databaseService, jsonObjectPromise,
          (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.OBSERVATIONS_COLLECTION, observationJson, promise))
        .setResponse(routingContext.response())
        .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/" + id + "/_history/" + newVersionId))
        .writeResponseBody()
        .releaseAsync()
        .future()
        .onSuccess(HttpServerResponse::end)
        .onFailure(throwable -> {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        });
    } else {
      routingContext.put("error", "Incorrect resource: resource's id does not match with path id");
      routingContext.put("code", "business-rule");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  private void handleObservationVersionRead(RoutingContext routingContext) {
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
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> observationPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("meta.versionId", vId)
      .put("resourceType", FhirUtils.OBSERVATION_TYPE);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, observationPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.OBSERVATIONS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });
  }

  private void handleObservationRead(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    //still to be supported

    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> observationPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("resourceType", FhirUtils.OBSERVATION_TYPE);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, observationPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.OBSERVATIONS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        //Check if the documents have been deleted


        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }

      });
  }

  private void handleObservationSearch(RoutingContext routingContext) {
    //
  }

  private void handleObservationCreate(RoutingContext routingContext) {
    //still to be supported

    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    String acceptableType = routingContext.getAcceptableContentType();
    String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

    JsonObject observationJson = routingContext.getBodyAsJson();

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    observationJson.put("id", newId);
    Metadata meta = new Metadata()
      .setVersionId(newVersionId)
      .setLastUpdated(Instant.now());
    observationJson.put("meta", JsonObject.mapFrom(meta));
    //Prefer header, response object depends on its value
    //Db operation using service proxy
    Promise<JsonObject> jsonObjectPromise = Promise.promise();

    OperationHandler
      .createUpdateCreateOperationHandler()
      .validate(observationJson)
      .executeDatabaseOperation(databaseService, jsonObjectPromise, (promise, service) ->
        service.createOrUpdateDomainResource(FhirUtils.OBSERVATIONS_COLLECTION, observationJson, promise))
      .setResponse(routingContext.response())
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/" + newId + "/_history/" + newVersionId))
      .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .setHandler(asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpServerResponse serverResponse = asyncResult.result();
          serverResponse.end();
        } else {
          routingContext.put("error", asyncResult.cause().getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        }
      });
  }


  //Patient CRUD
  private void handlePatientSearch(RoutingContext routingContext) {
    //still to be supported
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    String acceptableType = routingContext.getAcceptableContentType();
    Optional<String> _given = Optional.ofNullable(queryParams.get(Patient.SEARCH_PARAM_GIVEN));

    Optional<String> _family = Optional.ofNullable(queryParams.get(Patient.SEARCH_PARAM_FAMILY));


    Promise<JsonObject> patientPromise = Promise.promise();
    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("name.family", new JsonObject()
        .put("$regex", _family.orElse(".*")))
      .put("name.given", new JsonObject()
        .put("$regex", _given.orElse(".*")));

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, patientPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.PATIENTS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        //Check if the documents have been deleted
        if (throwable.getCause() == null) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              routingContext.put("error", throwable.getMessage());
              routingContext.put("code", "invariant");
              routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
            });
        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        }

      });


  }

  private void handlePatientDelete(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
    JsonObject query = new JsonObject()
      .put("id", id);
    //Step 1: find last modified resource
    Promise<JsonObject> fetchPatient = Promise.promise();
    databaseService.fetchDomainResourceWithQuery(FhirUtils.PATIENTS_COLLECTION, query, null, fetchPatient);
    //Step 2: insert all patients in delete collection
    //Step 3: remove all patients from their collection
    Promise<JsonObject> insertWithTagDeletedPromise = Promise.promise();

    fetchPatient
      .future()
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
          .executeDatabaseOperation(databaseService, insertWithTagDeletedPromise, (promise, service) ->
            service.createOrUpdateDomainResource(FhirUtils.PATIENTS_COLLECTION, jsonObject, promise))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.ETAG.toString(), newVersionId))
          .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + newVersionId))
          .writeResponseBody()
          .releaseAsync()
          .future()
          .onSuccess(HttpServerResponse::end)
          .onFailure(throwable -> {
            routingContext.put("error", throwable.getMessage());
            routingContext.put("code", "invariant");
            routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
          });
      }).onFailure(throwable -> {
      routingContext.put("error", "Unknown Resource");
      routingContext.put("code", "processing");
      routingContext.fail(HttpResponseStatus.NOT_FOUND.code());
    });


  }

  private void handlePatientUpdate(RoutingContext routingContext) {
    LOGGER.info("Trying update");
    //The request body SHALL be a Resource with an id element that has an identical value to the [id] in the URL
    //If no id element is provided, or the id disagrees with the id in the URL, the server SHALL respond with an HTTP 400 error code,
    // and SHOULD provide an OperationOutcome identifying the issue.
    JsonObject patientJson = routingContext.getBodyAsJson();
    String id = patientJson.getString(FhirUtils.ID);
    String pathId = routingContext.pathParam(FhirUtils.ID);
    if (id != null && id.equals(pathId)) {

      MultiMap queryParams = routingContext.queryParams();
      String format = queryParams.get(FhirUtils.FORMAT);
      String pretty = queryParams.get(FhirUtils.PRETTY);
      String summary = queryParams.get(FhirUtils.SUMMARY);
      String element = queryParams.get(FhirUtils.ELEMENTS);
      String acceptableType = routingContext.getAcceptableContentType();
      String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

      String newVersionId = UUID.randomUUID().toString();


      Metadata meta = new Metadata()
        .setVersionId(newVersionId)
        .setLastUpdated(Instant.now());
      patientJson.put("meta", JsonObject.mapFrom(meta));
      //Prefer header, response object depends on its value
      //Db operation using service proxy
      Promise<JsonObject> jsonObjectPromise = Promise.promise();
      OperationHandler
        .createUpdateCreateOperationHandler()
        .validate(patientJson)
        .executeDatabaseOperation(databaseService, jsonObjectPromise,
          (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.PATIENTS_COLLECTION, patientJson, promise))
        .setResponse(routingContext.response())
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + newVersionId))
        .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
        .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
        .writeResponseBody()
        .releaseAsync()
        .future()
        .onSuccess(HttpServerResponse::end)
        .onFailure(throwable -> {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        });


    } else {
      routingContext.put("error", "Incorrect resource: resource's id does not match with path id");
      routingContext.put("code", "business-rule");
      routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
    }

  }

  private void handlePatientCreate(RoutingContext routingContext) {


    //still to be supported

    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    String acceptableType = routingContext.getAcceptableContentType();
    String preferHeader = routingContext.request().headers().get(FhirHttpHeaderNames.PREFER);

    JsonObject patientJson = routingContext.getBodyAsJson();

    String newId = UUID.randomUUID().toString();
    String newVersionId = UUID.randomUUID().toString();

    patientJson.put("id", newId);
    Metadata meta = new Metadata()
      .setVersionId(newVersionId)
      .setLastUpdated(Instant.now());
    patientJson.put("meta", JsonObject.mapFrom(meta));
    //Prefer header, response object depends on its value
    //Db operation using service proxy
    Promise<JsonObject> jsonObjectPromise = Promise.promise();

    OperationHandler
      .createUpdateCreateOperationHandler()
      .validate(patientJson)
      .executeDatabaseOperation(databaseService, jsonObjectPromise, (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.PATIENTS_COLLECTION, patientJson, promise))
      .setResponse(routingContext.response())
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.LOCATION.toString(), FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + newId + "/_history/" + newVersionId))
      .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, preferHeader))
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.ACCEPT.toString(), acceptableType))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .setHandler(asyncResult -> {
        if (asyncResult.succeeded()) {
          HttpServerResponse serverResponse = asyncResult.result();
          serverResponse.end();
        } else {
          routingContext.put("error", asyncResult.cause().getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
        }
      });


  }

  private void handlePatientVersionRead(RoutingContext routingContext) {
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
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> patientPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("meta.versionId", vId);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, patientPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.PATIENTS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
      });
  }

  private void handlePatientRead(RoutingContext routingContext) {

    String id = routingContext.pathParam(FhirUtils.ID);
    MultiMap headers = routingContext.request().headers();
    MultiMap queryParams = routingContext.queryParams();
    String format = queryParams.get(FhirUtils.FORMAT);
    String pretty = queryParams.get(FhirUtils.PRETTY);
    String summary = queryParams.get(FhirUtils.SUMMARY);
    String element = queryParams.get(FhirUtils.ELEMENTS);
    //still to be supported

    //If resource has been deleted, need to respond with http status 410

    Promise<JsonObject> patientPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("resourceType", FhirUtils.PATIENT_TYPE);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, patientPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.PATIENTS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {

        if (throwable instanceof ServiceException) {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(((ServiceException) throwable).failureCode());

        } else {
          routingContext.put("error", throwable.getMessage());
          routingContext.put("code", "invariant");
          routingContext.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }

        //Check if the documents have been deleted


      });


  }

}
