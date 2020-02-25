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
      for (MongoDbQuery mongoDbQuery : MongoDbQuery.values()) {
        if (mongoDbQuery.getQueryName().equals(queryName)) {
          new QueryPrefixHandler();
          QueryPrefixResult queryPrefixResult = QueryPrefixHandler
            .parsePrefix(params.get(queryName));
          JsonObject fhirQuery = mongoDbQuery.getFhirQuery()
            .setPrefix(queryPrefixResult.prefix())
            .setValue(queryPrefixResult.parsedValue())
            .mongoDbQuery();
          andOperations.add(fhirQuery);
        }
      }
    });
    andOperations.add(new JsonObject());
    pipeline.add(new JsonObject()
      .put("$match", new JsonObject()
        .put("$and", andOperations)))
      .addAll(lookUpStages);
    JsonObject command = new JsonObject()
      .put("aggregationOutputFields", aggregationOutputFields)
      .put("pipeline", pipeline)
      .put("cursor",
        new JsonObject());
    return command;

  }
}
