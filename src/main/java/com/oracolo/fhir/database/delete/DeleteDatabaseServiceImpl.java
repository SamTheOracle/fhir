package com.oracolo.fhir.database.delete;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

public class DeleteDatabaseServiceImpl implements DeleteDatabaseService {
  private final static String DELETE_COLLECTION = "deleted_resources";
  private final MongoClient mongoClient;

  public DeleteDatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }


  @Override
  public DeleteDatabaseService findDeleteDocument(String id, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(DELETE_COLLECTION, new JsonObject()
      .put("id", id), null, res -> {
      if (res.succeeded() && res.result() != null) {
        JsonObject resource = res.result();
        resource.remove("_id");
        handler.handle(Future.succeededFuture(resource));
      }
      if (res.succeeded()) {
        handler.handle(Future.failedFuture("Resource with id " + id + "not found"));
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }


  @Override
  public DeleteDatabaseService insertDocuments(JsonArray deletedFhirResources, Handler<AsyncResult<Void>> handler) {
    List<BulkOperation> bulkOperations = new ArrayList<>();
    deletedFhirResources.forEach(resourceToDelete -> {
      BulkOperation bulkOperation = BulkOperation.createInsert((JsonObject) resourceToDelete);
      bulkOperations.add(bulkOperation);
    });
    this.mongoClient.bulkWrite(DELETE_COLLECTION, bulkOperations, res -> {
      if (res.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });

    return this;
  }

  @Override
  public DeleteDatabaseService deleteAllResourceById(String collectionName, String resourceId, Handler<AsyncResult<Void>> handler) {
    this.mongoClient.removeDocuments(collectionName, new JsonObject()
      .put("id", resourceId), res -> {
      if (res.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }
}
