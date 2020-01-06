package com.oracolo.fhir.database;

import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.FhirUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceException;

import java.time.Instant;
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
        handler.handle(ServiceException.fail(FhirUtils.MONGODB_CONNECTION_FAIL, res.cause().getMessage()));
      }
    });
    return this;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.find(collection, query, result -> {
      if (result.succeeded() && result.result() != null && result.result().size() > 0) {
        JsonObject jsonObjectResult = result.result()
          .stream()
          .peek(json -> json.remove("_id"))
          .max(Comparator.comparing(jsonObject -> Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class)
            .getLastUpdated()))
          .get();
        Metadata metadata = Json.decodeValue(jsonObjectResult.getJsonObject("meta").encode(), Metadata.class);
        if (metadata.getTag() != null && metadata.getTag().contains(FhirUtils.DELETED)) {
          handler.handle(ServiceException.fail(HttpResponseStatus.GONE.code(), "Resource already deleted"));
        } else {
          handler.handle(Future.succeededFuture(jsonObjectResult));

        }
      } else if (result.succeeded() && result.result() != null && result.result().size() == 0) {
        handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));

      } else {
        handler.handle(ServiceException.fail(FhirUtils.MONGODB_CONNECTION_FAIL, result.cause().getMessage()));
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
    this.mongoClient.bulkWrite(FhirUtils.DELETE_COLLECTION, bulkOperations, result -> {
      if (result.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(result.cause()));
      }
    });

    return this;
  }

  @Override
  public DatabaseService deleteResourceFromCollection(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.removeDocuments(collection, query, result -> {
      if (result.succeeded()) {

        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(result.cause()));
      }
    });
    return this;
  }

  @Override
  public DatabaseService fetchDomainResourcesWithQuery(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
    mongoClient.find(collection, query, result -> {
      if (result.succeeded() && result.result() != null && result.result().size() > 0) {

        Bundle bundle = new Bundle()
          .setTimestamp(Instant.now())
          .setType("searchset")
          .setTotal(result.result().size());

        result.result()
          .stream()
          .peek(jsonObject -> jsonObject.remove("_id"))
          .map(jsonObject -> Json.decodeValue(jsonObject.encodePrettily()))
          .forEach(object -> bundle.addNewEntry(new BundleEntry()
            .setResource(object)));
        handler.handle(Future.succeededFuture(JsonObject.mapFrom(bundle)));

      } else if (result.succeeded() && result.result() != null && result.result().size() == 0) {
        handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));

      } else {
        handler.handle(ServiceException.fail(FhirUtils.MONGODB_CONNECTION_FAIL, result.cause().getMessage()));
      }

    });
    return this;
  }

}
