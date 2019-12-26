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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.domain.Patient;
import model.elements.Metadata;
import model.exceptions.ResourceNotFound;
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
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handlePatientSearch)
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
      .handler(this::handleObservationRead);
    restApi.get("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID + "/" + FhirUtils.HISTORY + "/:" + FhirUtils.PATH_VERSIONID)
      .produces(HttpHeaderValues.APPLICATION_JSON.toString())
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .handler(this::handleObservationVersionRead);
    restApi.put("/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/:" + FhirUtils.ID)
      .consumes(FhirHttpHeaderValues.APPLICATION_JSON)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
      .produces(FhirHttpHeaderValues.APPLICATION_JSON)
      .handler(this::handleObservationUpdate)
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

  private void handleObservationUpdate(RoutingContext routingContext) {
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
          (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.OBSERVATIONS_COLLECTION, patientJson, promise))
        .setResponse(routingContext.response())
        .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.APPLICATION_JSON))
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

    Promise<JsonArray> allPatientsPromise = Promise.promise();
    //If resource has been deleted, need to respond with http status 410
    Promise<JsonObject> checkIfDeletedPromise = Promise.promise();
    Promise<JsonObject> patientPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id)
      .put("meta.versionId", vId);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, patientPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.OBSERVATIONS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        //Check if the documents have been deleted
        if (throwable.getCause() instanceof ResourceNotFound) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              deleteThrowable.printStackTrace();
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

  private void handleObservationRead(RoutingContext routingContext) {
    String id = routingContext.pathParam(FhirUtils.ID);
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
    Promise<JsonObject> patientPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id);

    OperationHandler
      .createReadOperationHandler()
      .executeDatabaseOperation(databaseService, patientPromise, (jsonObjectPromise, service) -> service.fetchDomainResourceWithQuery(FhirUtils.OBSERVATIONS_COLLECTION,
        query, null, jsonObjectPromise))
      .setResponse(serverResponse)
      .withHeader(FhirHttpHeader.of(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON))
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        //Check if the documents have been deleted
        if (throwable.getCause() instanceof ResourceNotFound) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              deleteThrowable.printStackTrace();
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
      .executeDatabaseOperation(databaseService, jsonObjectPromise, (promise, service) -> service.createOrUpdateDomainResource(FhirUtils.OBSERVATIONS_COLLECTION, observationJson, promise))
      .setResponse(routingContext.response())
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


  //Diagnostics CRUD


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
        if (throwable.getCause() instanceof ResourceNotFound) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              deleteThrowable.printStackTrace();
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
    //Step 1: find all resource with id
    Promise<JsonArray> fetchAllPatientsToDeletePromise = Promise.promise();
    //Step 2: insert all patients in delete collection
    Promise<Void> insertAllPatientsInDeleteCollectionPromise = Promise.promise();
    //Step 3: remove all patients from their collection
    Promise<JsonObject> deletePromise = Promise.promise();

    JsonObject query = new JsonObject()
      .put("id", id);
    OperationHandler
      .createDeleteOperationHandler()
      .executeDatabaseOperation(databaseService, deletePromise,
        (jsonObjectPromise, service) -> service.deleteResourcesFromCollection(FhirUtils.PATIENTS_COLLECTION, query, jsonObjectPromise))
      .setResponse(routingContext.response())
      .writeResponseBody()
      .releaseAsync()
      .future()
      .onSuccess(HttpServerResponse::end)
      .onFailure(throwable -> {
        routingContext.put("error", throwable.getMessage());
        routingContext.put("code", "invariant");
        routingContext.fail(HttpResponseStatus.BAD_REQUEST.code());
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
        .withHeader(FhirHttpHeader.of(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.APPLICATION_JSON))
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

    Promise<JsonArray> allPatientsPromise = Promise.promise();
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
        //Check if the documents have been deleted
        if (throwable.getCause() instanceof ResourceNotFound) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              deleteThrowable.printStackTrace();
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
  //Does not support conditional read
  private void handlePatientRead(RoutingContext routingContext) {

    String id = routingContext.pathParam(FhirUtils.ID);
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
    Promise<JsonObject> patientPromise = Promise.promise();
    HttpServerResponse serverResponse = routingContext.response();
    JsonObject query = new JsonObject()
      .put("id", id);

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
        if (throwable.getCause() instanceof ResourceNotFound) {
          databaseService
            .findDeletedDocument(query, checkIfDeletedPromise);
          checkIfDeletedPromise.future()
            .onSuccess(jsonObject -> serverResponse.setStatusCode(HttpResponseStatus.GONE.code())
              .end())
            .onFailure(deleteThrowable -> {
              deleteThrowable.printStackTrace();
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

}
