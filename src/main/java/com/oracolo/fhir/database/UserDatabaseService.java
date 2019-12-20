package com.oracolo.fhir.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

@ProxyGen
@VertxGen
public interface UserDatabaseService {
  static UserDatabaseService create(MongoClient mongoClient) {
    return new UserDatabaseServiceImpl(mongoClient);
  }

  static UserDatabaseService createProxy(Vertx vertx, String address) {
    return new UserDatabaseServiceVertxEBProxy(vertx, address);
  }

  @Fluent
  UserDatabaseService createNewPatientResource(JsonObject requestBody, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  UserDatabaseService fetchPatient(String id, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler);
  @Fluent
  UserDatabaseService fetchPatientVersion(String id, String vId, List<String> queryParams, Handler<AsyncResult<JsonObject>> handler);
}
