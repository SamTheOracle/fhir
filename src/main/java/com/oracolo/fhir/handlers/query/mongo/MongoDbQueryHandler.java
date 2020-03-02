package com.oracolo.fhir.handlers.query.mongo;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.QueryHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserResult;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.ReferenceQuery;
import com.oracolo.fhir.model.datatypes.Reference;
import com.oracolo.fhir.utils.FhirUtils;
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
      String value = entry.getValue();
      String[] orCondition = value.split(",");
      if (queryName.equals(MongoDbQuery._content.getQueryName())) {
        pipeline.add(new JsonObject()
          .put("$match", MongoDbQuery._content
            .getFhirQuery()
            .setValue(params.get(queryName))
            .mongoDbPipelineStageQuery()));
      } else {
        for (MongoDbQuery mongoDbQuery : MongoDbQuery.values()) {
          if (queryName.contains(".") && mongoDbQuery.getType().equals("Reference") && queryName.split(":(.*)\\.")[0].equals(mongoDbQuery.getQueryName())) {
            QueryPrefixResult result = QueryPrefixHandler.parsePrefix(value);

            JsonObject fhirQueryJson = Objects.requireNonNull(ReferenceQuery
              .createReferenceQuery(mongoDbQuery))
              .createMongoDbLookUpStage(queryName,result);

            lookUpStages.add(fhirQueryJson);
            aggregationOutputFields.add(fhirQueryJson.getJsonObject("$lookup").getString("as"));
          } else {
            if (mongoDbQuery.getQueryName().equals(queryName)) {
              //one can make or conditions with "," on param. For each param create normal queries but added in
              //or condition

              if (orCondition.length > 1) {
                JsonArray orConditionsJsonArray = new JsonArray();

                for (String valueFromOrCondition : orCondition) {
                  QueryPrefixResult queryPrefixResult = QueryPrefixHandler
                    .parsePrefix(valueFromOrCondition);
                  JsonObject fhirQuery = mongoDbQuery.getFhirQuery()
                    .setPrefix(queryPrefixResult.prefix())
                    .setValue(queryPrefixResult.parsedValue())
                    .mongoDbPipelineStageQuery();
                  orConditionsJsonArray.add(fhirQuery);
                }
                andOperations.add(new JsonObject()
                  .put("$or", orConditionsJsonArray));
              } else {
                //normal
                QueryPrefixResult queryPrefixResult = QueryPrefixHandler
                  .parsePrefix(value);
                JsonObject fhirQuery = mongoDbQuery.getFhirQuery()
                  .setPrefix(queryPrefixResult.prefix())
                  .setValue(queryPrefixResult.parsedValue())
                  .mongoDbPipelineStageQuery();
                andOperations.add(fhirQuery);
              }
            }
          }
        }
      }
    });

    andOperations
      .add(new JsonObject());
    pipeline
//      .add(new JsonObject()
//        .put("$match", new JsonObject()
//          .put("$expr", new JsonObject()
//            .put("$eq", new JsonArray()
//              .add(false)
//              .add(new JsonObject()
//                .put("$in", new JsonArray()
//                  .add(FhirUtils.DELETED)
//                  .add(new JsonObject()
//                    .put("$ifNull", new JsonArray()
//                      .add("$meta.tag.code")
//                      .add(new JsonArray()))))))
//          )))
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
