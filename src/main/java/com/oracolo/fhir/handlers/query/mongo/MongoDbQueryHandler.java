package com.oracolo.fhir.handlers.query.mongo;

import com.oracolo.fhir.handlers.query.QueryHandler;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.ReferenceQuery;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * It creates a MongoDb query
 */
public class MongoDbQueryHandler implements QueryHandler {

  private MultiMap params;


  public MongoDbQueryHandler(MultiMap params) {
    this.params = params;
  }


  public JsonObject createMongoDbQuery() throws Exception {
    JsonArray andOperations = new JsonArray();
    JsonArray pipeline = new JsonArray();
    JsonArray lookUpStages = new JsonArray();
    JsonArray aggregationOutputFields = new JsonArray();
    params.forEach(entry -> {
      String queryName = entry.getKey();
      String queryValue = entry.getValue();
      if (queryName.equals(MongoDbQuery._content.getQueryName())) {
        pipeline.add(new JsonObject()
          .put("$match", MongoDbQuery._content
            .getFhirQuery()
            .mongoDbPipelineStageQuery(queryName, queryValue)));
      } else {
        for (MongoDbQuery mongoDbQuery : MongoDbQuery.values()) {
          if (queryName.contains(".") && mongoDbQuery.getType().equals("Reference") && queryName.split(":(.*)\\.")[0].equals(mongoDbQuery.getQueryName())) {

            JsonObject fhirQueryJson = Objects.requireNonNull(ReferenceQuery
              .createReferenceQuery(mongoDbQuery))
              .createMongoDbLookUpStage(queryName, queryValue);

            lookUpStages.add(fhirQueryJson);
            aggregationOutputFields.add(fhirQueryJson
              .getJsonObject("$lookup")
              .getString("as"));


          } else {
            if (mongoDbQuery.getQueryName().equals(queryName)) {
              //one can make or conditions with "," on param. For each param create normal queries but added in
              //or condition. If only one condition is present, still adds "$or" with second element as empty (true)

              String[] orCondition = queryValue.split(",");
              if(orCondition.length > 1){
                JsonArray orConditionsJsonArray = new JsonArray();
                for (String valueFromOrCondition : orCondition) {
                  JsonObject fhirQuery = mongoDbQuery.getFhirQuery()
                    .mongoDbPipelineStageQuery(queryName, valueFromOrCondition);
                  orConditionsJsonArray.add(fhirQuery);
                }
                andOperations.add(new JsonObject()
                  .put("$or", orConditionsJsonArray
                    .add(new JsonObject())));
              }
              else {
                andOperations.add(mongoDbQuery.getFhirQuery().mongoDbPipelineStageQuery(queryName,queryValue));
              }

            }
          }
        }
      }
    });

    andOperations
      .add(new JsonObject());
    pipeline
      .add(new JsonObject()
        .put("$match", new JsonObject()
          .put("$expr", new JsonObject()
            .put("$and", andOperations))
        ))
      .addAll(lookUpStages);
    JsonObject command = new JsonObject()
      .put("aggregationOutputFields", aggregationOutputFields)
      .put("pipeline", pipeline)
      .put("cursor",
        new JsonObject())
      .put("allowDiskUse", true);
    return command;

  }
}
