package com.oracolo.fhir.database.user;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import utils.FhirUtils;

import java.util.List;

public class UserDatabaseServiceImpl implements UserDatabaseService {
  private final MongoClient mongoClient;

  UserDatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public UserDatabaseService createOrUpdatePatientResource(JsonObject requestBody, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.insert(FhirUtils.PATIENTS_COLLECTION, requestBody, res -> {
      if (res.succeeded()) {
        //vertx mongo client insert _id, when saving, need to get it out
        requestBody.remove("_id");
        handler.handle(Future.succeededFuture(requestBody));
      } else {
        handler.handle(Future.failedFuture(res.cause().getMessage()));
      }
    });
    return this;
  }

  @Override
  public UserDatabaseService fetchPatient(String id, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(FhirUtils.PATIENTS_COLLECTION, new JsonObject().put("id", id), null,
      mongoHandler -> {
        if (mongoHandler.succeeded() && mongoHandler.result() != null) {
          JsonObject patientJson = mongoHandler.result();
          patientJson.remove("_id");
          handler.handle(Future.succeededFuture(patientJson));
        }
        if (mongoHandler.succeeded()) {
          handler.handle(Future.failedFuture("No patient found"));

        } else {
          handler.handle(Future.failedFuture(mongoHandler.cause().getMessage()));
        }
      });
    return this;
  }

  @Override
  public UserDatabaseService fetchPatientVersion(String id, String vId, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(FhirUtils.PATIENTS_COLLECTION, new JsonObject()
        .put("meta.versionId",vId)
        .put("id", id), null,
      mongoHandler -> {
        if (mongoHandler.succeeded() && mongoHandler.result() != null) {
          JsonObject patientJson = mongoHandler.result();
          patientJson.remove("_id");
          handler.handle(Future.succeededFuture(patientJson));
        }
        if (mongoHandler.succeeded()) {
          handler.handle(Future.failedFuture("No patient found"));

        } else {
          handler.handle(Future.failedFuture(mongoHandler.cause().getMessage()));
        }
      });
    return this;
  }


  @Override
  public UserDatabaseService fetchAllPatient(String id, Handler<AsyncResult<JsonArray>> handler) {
    this.mongoClient.find(FhirUtils.PATIENTS_COLLECTION, new JsonObject()
        .put("id", id),
      mongoHandler -> {
        if (mongoHandler.succeeded() && mongoHandler.result() != null) {
          JsonArray patients = new JsonArray();
          List<JsonObject> fetchedPatients = mongoHandler.result();
          fetchedPatients.forEach(fetchedPatient -> {
            fetchedPatient.remove("_id");
            patients.add(fetchedPatient);
          });

          handler.handle(Future.succeededFuture(patients));
        }
        if (mongoHandler.succeeded()) {
          handler.handle(Future.failedFuture("No patient found"));

        } else {
          handler.handle(Future.failedFuture(mongoHandler.cause().getMessage()));
        }
      });
    return this;
  }

}
