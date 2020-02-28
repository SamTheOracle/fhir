package com.oracolo.fhir.handlers.query.mongo.parser.chain;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.MongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.ChainReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.ReferenceQuery;
import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChainParserHandler {


  public static ChainParserResult createLookupPipelineStage(String paramName, String paramValue) {
    for (ResourceType type : ResourceType.values()) {
      if (paramName.contains(type.typeName())) {
        //subject:Patient.name becomes split["subject","name"]
        String[] split = paramName.split(":(.*)\\.");
        String fieldToCompare = split[0];
        String queryName = split[1];
        QueryPrefixResult result = QueryPrefixHandler.parsePrefix(paramValue);
        FhirQuery fhirQuery = MongoDbQuery
          .valueOf(queryName.replace("-", "_"))
          .getFhirQuery();
        ChainReferenceQuery chainReferenceQuery = ChainReferenceQuery
          .valueOf(fieldToCompare.replace("-", "_"));
        ReferenceQuery reference = chainReferenceQuery
          .getChainReference();
        return new ChainParserResult(type.getCollection(),
          new JsonObject()
            .put("$lookup", new JsonObject()
              .put("from", type.getCollection())
              .put("let", new JsonObject()
                .put("searchParam", "$" + chainReferenceQuery
                  .getFhirResourceField()))
              .put("pipeline", new JsonArray()
                .add(new JsonObject()
                  .put("$match", new JsonObject()
                    .put("$expr", new JsonObject()
                      .put("$and", new JsonArray()
                        .add(fhirQuery
                          .setPrefix(result.prefix())
                          .setValue(result.parsedValue())
                          .mongoDbPipelineStageQuery())
                        .add(reference.mongoDbMatchQuery("searchParam"))
                      ))
                  )))
              .put("as", type.getCollection())));

      }
    }


    return null;
  }
}


