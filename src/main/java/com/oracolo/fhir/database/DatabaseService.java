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
public interface DatabaseService {
  static DatabaseService create(MongoClient mongoClient) {
    return new DatabaseServiceImpl(mongoClient);
  }

  static DatabaseService createProxy(Vertx vertx, String address) {
    return new DatabaseServiceVertxEBProxy(vertx, address);
  }


  @Fluent
  DatabaseService createDeletedResource(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService conditionalCreateUpdate(String collection, JsonObject body, JsonObject query, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService fetchDomainResourcesWithQuery(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService executeWriteBulkOperations(String collection, List<JsonObject> resources, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService createUpdateResource(String collection, JsonObject body, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService findEverythingAboutEncounter(String encounterId, Handler<AsyncResult<JsonObject>> handler);
}
