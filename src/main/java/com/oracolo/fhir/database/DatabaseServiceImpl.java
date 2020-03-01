package com.oracolo.fhir.database;

import com.oracolo.fhir.model.aggregations.AggregationEncounter;
import com.oracolo.fhir.model.aggregations.AggregationType;
import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.backboneelements.BundleResponse;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.datatypes.Metadata;
import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResourceType;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseServiceImpl implements DatabaseService {

  private MongoClient mongoClient;

  public DatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public DatabaseService createDeletedResource(String collection, JsonObject query,
                                               Handler<AsyncResult<JsonObject>> handler) {
    fetchDomainResourceWithQuery(collection, query, null, res -> {
      // if it is successfull, the resource is not deleted
      if (res.succeeded() && res.result() != null) {
        JsonObject resultFromFetch = res.result();
        Metadata metadata = Json.decodeValue(resultFromFetch.getJsonObject("meta").encode(), Metadata.class);
        metadata.addNewTag(new Coding()
          .setCode(FhirUtils.DELETED))
          .setLastUpdated(Instant.now());

        resultFromFetch.put("meta", JsonObject.mapFrom(metadata));
        this.mongoClient.insert(collection, resultFromFetch, insertRes -> {
          if (insertRes.succeeded()) {

            // vertx mongo client insert _id, when saving, need to get it out
            resultFromFetch.remove("_id");
            handler.handle(Future.succeededFuture(resultFromFetch));
          } else {
            handler.handle(ServiceException.fail(FhirUtils.MONGODB_CONNECTION_FAIL, insertRes.cause().getMessage()));
          }
        });
        // client is trying to delete a resource that does not exist but it is ok
      } else {
        ServiceException exception = (ServiceException) res.cause();

        handler.handle(ServiceException.fail(exception.failureCode(), res.cause().getMessage()));

      }
    });
    return this;
  }

  @Override
  public DatabaseService createResource(String collection, JsonObject body,
                                        Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.insert(collection, body, insertRes -> {
      if (insertRes.succeeded()) {
        // mongodb insert _id, when saving, need to get it out
        body.remove("_id");
        handler.handle(Future.succeededFuture(body));
      } else {
        handler.handle(
          ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
      }
    });
    return this;
  }

  @Override
  public DatabaseService updateDomainResource(String collection,
                                              JsonObject body,
                                              JsonObject matchQuery,
                                              Handler<AsyncResult<JsonObject>> handler) {
    fetchDomainResourceWithQuery(collection, matchQuery, null, asyncRes -> {
      if (asyncRes.succeeded() && asyncRes.result() != null) {
        this.mongoClient.insert(collection, body, insertRes -> {
          if (insertRes.succeeded()) {
            // vertx mongo client insert _id, when saving, need to get it out
            body.remove("_id");
            handler.handle(Future.succeededFuture(JsonObject.mapFrom(new UpdateResult()
              .setBody(body.encode())
              .setStatus(HttpResponseStatus.OK.code()))));
            updateAggregationEncounter(collection, body.getString("id"), body);
          } else {
            handler.handle(
              ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
          }
        });
      } else {
        this.mongoClient.insert(collection, body, insertRes -> {
          if (insertRes.succeeded()) {
            // vertx mongo client insert _id, when saving, need to get it out
            body.remove("_id");
            handler.handle(Future.succeededFuture(JsonObject.mapFrom(new UpdateResult()
              .setBody(body.encode())
              .setStatus(HttpResponseStatus.CREATED.code()))));
            updateAggregationEncounter(collection, body.getString("id"), body);

          } else {
            handler.handle(
              ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), insertRes.cause().getMessage()));
          }
        });
      }
    });


    return this;
  }


  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public DatabaseService fetchDomainResourceWithQuery(String collection, JsonObject query, JsonObject fields,
                                                      Handler<AsyncResult<JsonObject>> handler) {
    this.mongoClient.find(collection, query, result -> {
      if (result.succeeded() && result.result() != null && result.result().size() > 0) {
        JsonObject jsonObjectResult = result.result().stream().peek(json -> json.remove("_id"))
          .max(Comparator.comparing(jsonObject -> Json
            .decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class).getLastUpdated()))
          .get();

        Metadata metadata = Json.decodeValue(jsonObjectResult.getJsonObject("meta").encode(), Metadata.class);
        if (metadata.getTag() != null && metadata.getTag().stream().noneMatch(coding -> coding.getCode().equals(FhirUtils.DELETED))) {
          handler.handle(ServiceException.fail(HttpResponseStatus.GONE.code(), FhirUtils.DELETE_MESSAGE));
        } else {
          handler.handle(Future.succeededFuture(jsonObjectResult));

        }
      } else if (result.succeeded() && result.result() != null && result.result().size() == 0) {
        handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));

      } else {
        handler.handle(
          ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), result.cause().getMessage()));
      }
    });
    return this;
  }

  @Override
  public DatabaseService executeAggregationCommand(String collection,
                                                   JsonObject command,
                                                   Handler<AsyncResult<JsonObject>> handler) {
    command.put("aggregate", collection);
    JsonArray aggregationOutputFields = (JsonArray) command.remove("aggregationOutputFields");

    mongoClient.runCommand("aggregate", command, asyncRes -> {
      if (asyncRes.succeeded() && asyncRes.result() != null && asyncRes.result()
        .getJsonObject("cursor")
        .getJsonArray("firstBatch")
        .size() > 0) {
        JsonObject mongoDbBatch = asyncRes.result();
        Map<String, JsonObject> results = new HashMap<>();
        mongoDbBatch.getJsonObject("cursor")
          .getJsonArray("firstBatch")
          .stream()
          .map(JsonObject::mapFrom)
          .filter(json -> {
            boolean emptyResults = true;
            for (Object obj : aggregationOutputFields) {
              if (json.getJsonArray((String) obj).size() == 0) {
                emptyResults = false;
              }
            }
            return emptyResults;
          })
          .peek(json -> {
            json.remove("_id");
            aggregationOutputFields.stream().map(obj -> (String) obj).forEach(json::remove);
          })
          .collect(Collectors.groupingBy(jsonObject -> jsonObject.getString("id")))
          .forEach((id, list) ->
            results.put(id, list.stream().max(Comparator.comparing(jsonObject -> Json
              .decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class).getLastUpdated())).get())
          );
        if (results.size() == 0) {
          handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));

        } else {
          Bundle bundle = new Bundle().setTimestamp(Instant.now()).setTotal(results.size());
          results.forEach((id,json) -> {

            Metadata metadata = Json.decodeValue(json.getJsonObject("meta").encode(), Metadata.class);
            bundle.addNewEntry(new BundleEntry()
              .setResponse(
                new BundleResponse()
                  .setLastModified(metadata.getLastUpdated().toString())
                  .setEtag(metadata.getVersionId()))
              .setResource(json));
          });
          handler.handle(Future.succeededFuture(JsonObject.mapFrom(bundle)));

        }


      } else if (asyncRes.succeeded() && asyncRes.result() != null) {
        handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));
      } else {
        handler.handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), asyncRes.cause().getMessage()));
      }

    });
    return this;
  }


  @Override
  public DatabaseService executeWriteBulkOperations(String collection, List<JsonObject> resources,
                                                    Handler<AsyncResult<JsonObject>> handler) {
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

  @Override
  public DatabaseService createAggregationResource(AggregationType aggregationType, JsonObject mainResource,
                                                   List<JsonObject> resources, Handler<AsyncResult<JsonObject>> handler) {

    if (aggregationType == AggregationType.ENCOUNTER) {
      List<Observation> observations = resources.stream()
        .filter(jsonObject -> jsonObject.getString("resourceType").equals(ResourceType.OBSERVATION.typeName()))
        .map(json -> Json.decodeValue(json.encode(), Observation.class)).collect(Collectors.toList());
      List<Condition> conditions = resources.stream()
        .filter(jsonObject -> jsonObject.getString("resourceType").equals(ResourceType.CONDITION.typeName()))
        .map(json -> Json.decodeValue(json.encode(), Condition.class)).collect(Collectors.toList());
      List<Procedure> procedures = resources.stream()
        .filter(jsonObject -> jsonObject.getString("resourceType").equals(ResourceType.PROCEDURE.typeName()))
        .map(json -> Json.decodeValue(json.encode(), Procedure.class)).collect(Collectors.toList());
      List<Encounter> encounters = resources.stream()
        .filter(jsonObject -> jsonObject.getString("resourceType").equals(ResourceType.ENCOUNTER.typeName()))
        .map(json -> Json.decodeValue(json.encode(), Encounter.class)).collect(Collectors.toList());
      List<Practitioner> practitioners = resources.stream()
        .filter(jsonObject -> jsonObject.getString("resourceType").equals(ResourceType.PRACTITIONER.typeName()))
        .map(json -> Json.decodeValue(json.encode(), Practitioner.class)).collect(Collectors.toList());

      AggregationEncounter aggregationEncounter = new AggregationEncounter()
        .setIds(encounters.stream()
          .map(Encounter::getId)
          .collect(Collectors.toList()))
        .setMainEncounter(Json.decodeValue(mainResource.encode(), Encounter.class))
        .setSubEncounters(encounters)
        .setObservations(observations)
        .setProcedures(procedures).setPractitioners(practitioners)
        .setConditions(conditions);
      JsonObject aggregationJson = JsonObject.mapFrom(aggregationEncounter);

      mongoClient.insert("aggregations", aggregationJson, res -> {
        if (res.succeeded()) {
          aggregationJson.remove("_id");
          handler.handle(Future.succeededFuture(aggregationJson));
        } else {
          handler
            .handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), res.cause().getMessage()));
        }
      });
    } else {
      handler.handle(ServiceException.fail(HttpResponseStatus.BAD_REQUEST.code(), "Unknown Aggregation Resource"));
    }
    return this;
  }

  @Override
  public DatabaseService findAggregationResource(AggregationType aggregationType, JsonObject query,
                                                 Handler<AsyncResult<JsonObject>> handler) {
    mongoClient.findOne("aggregations", query, null, res -> {
      if (res.succeeded() && res.result() != null) {
        JsonObject aggregationJsonObject = res.result();
        aggregationJsonObject.remove("_id");
        Bundle bundle = new Bundle().setType("searchset");
        List<Object> allResource = new ArrayList<>();
        JsonArray subEncounters = aggregationJsonObject.getJsonArray("subEncounters");
        JsonArray observations = aggregationJsonObject.getJsonArray("observations");
        JsonArray procedures = aggregationJsonObject.getJsonArray("procedures");
        JsonArray conditions = aggregationJsonObject.getJsonArray("conditions");
        if (subEncounters != null) {
          allResource.addAll(subEncounters.getList());
        }
        if (observations != null) {
          allResource.addAll(observations.getList());
        }
        if (procedures != null) {
          allResource.addAll(procedures.getList());
        }
        if (conditions != null) {
          allResource.addAll(conditions.getList());
        }
        allResource.stream().map(JsonObject::mapFrom).forEach(json -> {
          String resourceType = json.getString("resourceType");
          String id = json.getString("id");
          Metadata meta = Json.decodeValue(json.getJsonObject("meta").encode(), Metadata.class);
          bundle
            .addNewEntry(
              new BundleEntry()
                .setResponse(new BundleResponse().setLastModified(meta.getLastUpdated().toString())
                  .setEtag(meta.getVersionId()).setLocation("/" + resourceType + "/" + id))
                .setResource(json));
        });
        Metadata meta = Json.decodeValue(
          aggregationJsonObject.getJsonObject("mainEncounter").getJsonObject("meta").encode(), Metadata.class);
        bundle.addNewEntry(new BundleEntry().setResource(aggregationJsonObject.getJsonObject("mainEncounter"))
          .setResponse(new BundleResponse().setEtag(meta.getVersionId())
            .setLastModified(meta.getLastUpdated().toString()).setLocation("/" + ResourceType.ENCOUNTER.typeName()
              + "/" + aggregationJsonObject.getJsonObject("mainEncounter").getString("id"))));
        bundle.setTotal(bundle.getEntry().size());
        handler.handle(Future.succeededFuture(JsonObject.mapFrom(bundle)));
      } else if (res.succeeded()) {
        handler.handle(ServiceException.fail(HttpResponseStatus.NOT_FOUND.code(), "No resource found"));
      } else {
        handler
          .handle(ServiceException.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), res.cause().getMessage()));

      }
    });

    return this;
  }


  private void updateAggregationEncounter(String collection, String id, JsonObject body) {
    this.mongoClient.find("aggregations", new JsonObject()
      .put(collection + ".id", id), aggregationAsyncRes -> {
      if (aggregationAsyncRes.succeeded() && aggregationAsyncRes.result() != null && aggregationAsyncRes.result().size() > 0) {
        aggregationAsyncRes.result()
          .forEach(jsonObject -> {
            JsonArray resources = jsonObject.getJsonArray(collection);
            Optional<JsonObject> toRemove = resources.
              stream()
              .map(obj -> (JsonObject) obj)
              .filter(j -> j.getString("id").equals(id))
              .findAny();
            toRemove.ifPresent(resources::remove);
            resources.add(body);
            JsonObject query = new JsonObject()
              .put("_id", jsonObject.getString("_id"));
            mongoClient.findOneAndReplace("aggregations", query, jsonObject, resultHandler -> {
              if (resultHandler.succeeded()) {
                JsonObject r = resultHandler.result();

              }
            });
          });

      }
    });
  }


}
