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
  public DatabaseService createDeletedResource(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
    fetchDomainResourceWithQuery(collection, query, null, res -> {
      //if it is successfull, the resource is not deleted
      if (res.succeeded() && res.result() != null) {
        JsonObject resultFromFetch = res.result();

        resultFromFetch.getJsonObject("meta")
          .put("tag", new JsonArray().add(FhirUtils.DELETED))
          .put("lastUpdated", Instant.now());
        this.mongoClient.insert(collection, resultFromFetch, insertRes -> {
          if (insertRes.succeeded()) {

            //vertx mongo client insert _id, when saving, need to get it out
            resultFromFetch.remove("_id");
            handler.handle(Future.succeededFuture(resultFromFetch));
          } else {
            handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
          }
        });
      } else {
        handler.handle(ServiceException.fail(HttpResponseStatus.GONE.code(), "Resource already deleted"));
      }
    });
    return this;
  }

  @Override
  public DatabaseService createUpdateResource(String collection, JsonObject body, Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.insert(collection, body, insertRes -> {
      if (insertRes.succeeded()) {
        //vertx mongo client insert _id, when saving, need to get it out
        body.remove("_id");
        handler.handle(Future.succeededFuture(body));
      } else {
        handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
      }
    });
    return this;
  }


  @Override
  public DatabaseService conditionalCreateUpdate(String collection, JsonObject body, JsonObject query, Handler<AsyncResult<JsonObject>> handler) {
//    JsonObject copy = body.copy();
//    copy.remove("id");
//    copy.remove("meta");
//    JsonObject query = new JsonObject()
//      .put("$or", new JsonArray()
//        .add(new JsonObject()
//          .put("id", body.getString("id")))
//        .add(copy));
    fetchDomainResourceWithQuery(collection, query, null, checkIfDeletedHandler -> {
      if (checkIfDeletedHandler.succeeded() && checkJsonObjects(checkIfDeletedHandler.result(), body.copy())) {
        handler.handle(ServiceException.fail(HttpResponseStatus.BAD_REQUEST.code(), "Resource already exists"));

      } else {
        this.mongoClient.insert(collection, body, insertRes -> {
          if (insertRes.succeeded()) {
            //vertx mongo client insert _id, when saving, need to get it out
            body.remove("_id");
            handler.handle(Future.succeededFuture(body));
          } else {
            handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
          }
        });
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
        handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), result.cause().getMessage()));
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

  @Override
  public DatabaseService executeWriteBulkOperations(String collection, List<JsonObject> resources, Handler<AsyncResult<JsonObject>> handler) {
    List<BulkOperation> operations = new ArrayList<>();
    resources.forEach(domainResource -> operations.add(BulkOperation.createInsert(domainResource)));
    mongoClient.bulkWrite(collection, operations, res -> {
      if (res.succeeded() && res.result() != null) {
        handler.handle(Future.succeededFuture(new JsonObject()));
      } else {
        handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "Error"));
      }
    });
    return this;
  }

  private boolean checkJsonObjects(JsonObject j1, JsonObject j2) {
    j1.remove("meta");
    j1.remove("id");
    j2.remove("id");
    j2.remove("meta");
    return j1.encode().equals(j2.encode());
  }
}
