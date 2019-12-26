package com.oracolo.fhir.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;
import model.elements.Metadata;
import model.exceptions.ResourceNotFound;
import utils.FhirUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseServiceImpl implements DatabaseService {

  private MongoClient mongoClient;

  public DatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public DatabaseService createOrUpdateDomainResource(String collection, JsonObject requestBody, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.insert(collection, requestBody, res -> {
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
  public DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.find(collection, query, res -> {
      if (res.succeeded() && res.result() != null && res.result().size() > 0) {
        JsonObject jsonObjectResult = res.result()
          .stream()
          .peek(json -> json.remove("_id"))
          .max(Comparator.comparing(jsonObject -> Json.decodeValue(jsonObject.getString("meta"), Metadata.class)
            .getLastUpdated()))
          .orElse(null);
        handler.handle(Future.succeededFuture(jsonObjectResult));
      }
      if (res.succeeded()) {
        handler.handle(Future.failedFuture(new ResourceNotFound("Resource not Found")));

      } else {
        handler.handle(Future.failedFuture(res.cause().getMessage()));
      }
    });
    return this;
  }

  /**
   * Find a document in delete collections
   *
   * @param query
   * @param handler
   * @return
   */
  @Override
  public DatabaseService findDeletedDocument(JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.findOne(FhirUtils.DELETE_COLLECTION, query, null, res -> {
      if (res.succeeded() && res.result() != null) {
        JsonObject resource = res.result();
        resource.remove("_id");
        handler.handle(Future.succeededFuture(resource));
      }
      if (res.succeeded()) {
        handler.handle(Future.failedFuture("Resource not found"));
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public DatabaseService insertDeletedDomainResources(String collection, JsonArray deletedFhirResources, Handler<AsyncResult<Void>> handler) {
    List<BulkOperation> bulkOperations = new ArrayList<>();
    deletedFhirResources.forEach(resourceToDelete -> {
      BulkOperation bulkOperation = BulkOperation.createInsert((JsonObject) resourceToDelete);
      bulkOperations.add(bulkOperation);
    });
    this.mongoClient.bulkWrite(FhirUtils.DELETE_COLLECTION, bulkOperations, res -> {
      if (res.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });

    return this;
  }

  @Override
  public DatabaseService deleteResourcesFromCollection(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.removeDocuments(collection, query, res -> {
      if (res.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }


}
