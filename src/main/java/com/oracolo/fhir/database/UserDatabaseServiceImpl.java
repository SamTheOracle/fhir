package com.oracolo.fhir.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class UserDatabaseServiceImpl implements UserDatabaseService {
  private final MongoClient mongoClient;
  private final static String PATIENT_COLLECTION = "patients";

  public UserDatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public UserDatabaseService createNewPatientResource(JsonObject requestBody, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.insert(PATIENT_COLLECTION, requestBody, res -> {
      if (res.succeeded()) {
        handler.handle(Future.succeededFuture(requestBody));
      } else {
        handler.handle(Future.failedFuture(res.cause().getMessage()));
      }
    });
    return this;
  }

  @Override
  public UserDatabaseService fetchPatient(String id, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(PATIENT_COLLECTION, new JsonObject().put("id", id), null,
      mongoHandler -> {
        if (mongoHandler.succeeded()) {
          JsonObject patientJson = mongoHandler.result();
          patientJson.remove("_id");
          handler.handle(Future.succeededFuture(patientJson));
        } else {
          handler.handle(Future.failedFuture(mongoHandler.cause().getMessage()));
        }
      });
    return this;
  }

  @Override
  public UserDatabaseService fetchPatientVersion(String id, String vId, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(PATIENT_COLLECTION, new JsonObject()
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

}
