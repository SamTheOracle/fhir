package com.oracolo.fhir.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

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
  DatabaseService createOrUpdateDomainResource(String collection, JsonObject requestBody, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Find a document in delete collections
   *
   * @param query
   * @param handler
   * @return
   */
  @Fluent
  DatabaseService findDeletedDocument(JsonObject query, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService insertDeletedDomainResources(String collection, JsonArray deletedFhirResources, Handler<AsyncResult<Void>> handler);

  @Fluent
  DatabaseService deleteResourceFromCollection(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler);

}
