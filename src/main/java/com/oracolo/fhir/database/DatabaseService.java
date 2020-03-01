package com.oracolo.fhir.database;

import com.oracolo.fhir.model.aggregations.AggregationType;
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
  DatabaseService createDeletedResource(String collection, String id, Handler<AsyncResult<JsonObject>> handler);


  @Fluent
  DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields,
                                               Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService executeAggregationCommand(String collection,
                                            JsonObject command,
                                            Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService executeWriteBulkOperations(String collection, List<JsonObject> resources,
                                             Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService createResource(String collection, JsonObject body, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService updateDomainResource(String collection,
                                       JsonObject body,
                                       JsonObject matchQuery,
                                       Handler<AsyncResult<JsonObject>> handler);


  @Fluent
  DatabaseService createAggregationResource(AggregationType aggregationType, JsonObject mainResource,
                                            List<JsonObject> resources, Handler<AsyncResult<JsonObject>> handler);

  @Fluent
  DatabaseService findAggregationResource(AggregationType aggregationType, JsonObject query,
                                          Handler<AsyncResult<JsonObject>> handler);



}
