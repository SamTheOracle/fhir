package com.oracolo.fhir.database.diagnostics;

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
public interface DiagnosticsDatabaseService {
  static DiagnosticsDatabaseService create(MongoClient mongoClient) {
    return new DiagnosticsDatabaseServiceImpl(mongoClient);
  }

  static DiagnosticsDatabaseService createProxy(Vertx vertx, String address) {
    return new DiagnosticsDatabaseServiceVertxEBProxy(vertx, address);
  }

  @Fluent
  DiagnosticsDatabaseService findDeleteDocument(String id, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DiagnosticsDatabaseService insertDocuments(JsonArray deletedFhirResources, Handler<AsyncResult<Void>> handler);

  @Fluent
  DiagnosticsDatabaseService deleteAllResourceById(String collectionName, String resourceId, Handler<AsyncResult<Void>> handler);

}
