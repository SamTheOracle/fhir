package com.oracolo.fhir.database.delete;

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
public interface DeleteDatabaseService {
  static DeleteDatabaseService create(MongoClient mongoClient) {
    return new DeleteDatabaseServiceImpl(mongoClient);
  }

  static DeleteDatabaseService createProxy(Vertx vertx, String address) {
    return new DeleteDatabaseServiceVertxEBProxy(vertx, address);
  }

  @Fluent
  DeleteDatabaseService findDeleteDocument(String id, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DeleteDatabaseService insertDocuments(JsonArray deletedFhirResources, Handler<AsyncResult<Void>> handler);

  @Fluent
  DeleteDatabaseService deleteAllResourceById(String collectionName, String resourceId, Handler<AsyncResult<Void>> handler);

}
