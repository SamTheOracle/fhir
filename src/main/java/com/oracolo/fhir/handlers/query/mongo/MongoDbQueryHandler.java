package com.oracolo.fhir.handlers.query.mongo;

import com.oracolo.fhir.handlers.query.QueryHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserResult;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
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


  public JsonObject createMongoDbQuery() {
    JsonArray andOperations = new JsonArray();
    JsonArray pipeline = new JsonArray();
    JsonArray lookUpStages = new JsonArray();
    JsonArray aggregationOutputFields = new JsonArray();
    params.forEach(entry -> {
      String queryName = entry.getKey();
      if (queryName.contains(".")) {
        ChainParserResult chainParserResult = ChainParserHandler.createLookupPipelineStage(queryName, entry.getValue());
        JsonObject chainQuery = Objects.requireNonNull(chainParserResult).getQuery();
        lookUpStages.add(chainQuery);
        aggregationOutputFields.add(chainParserResult.getCollection());
      }
      //text base search must be in first stage of the overall pipeline
      if (queryName.equals(MongoDbQuery._content.getQueryName())) {
        pipeline.add(new JsonObject()
          .put("$match", MongoDbQuery._content
            .getFhirQuery()
            .setValue(params.get(queryName))
            .mongoDbPipelineStageQuery()));
      } else {
        for (MongoDbQuery mongoDbQuery : MongoDbQuery.values()) {
          if (mongoDbQuery.getQueryName().equals(queryName)) {
            QueryPrefixResult queryPrefixResult = QueryPrefixHandler
              .parsePrefix(params.get(queryName.replace("-", "_")));
            JsonObject fhirQuery = mongoDbQuery.getFhirQuery()
              .setPrefix(queryPrefixResult.prefix())
              .setValue(queryPrefixResult.parsedValue())
              .mongoDbPipelineStageQuery();
            andOperations.add(fhirQuery);
          }
        }
      }

    });
    andOperations.add(new JsonObject());
    pipeline.add(new JsonObject()
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
