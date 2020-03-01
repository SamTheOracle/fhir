package com.oracolo.fhir.handlers.query.mongo.parser.chain;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.MongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.Prefix;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.DiagnosisReferenceQuery;
import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChainParserHandler {


  public static JsonObject createLookupPipelineStage(String paramName, String parsedValue, Prefix prefix, JsonObject matchQuery, String field) {
    for (ResourceType type : ResourceType.values()) {
      //subject:Patient.name becomes split["subject","name"]
      if (paramName.contains(type.typeName())) {
        String[] split = paramName.split(":(.*)\\.");

        String queryName = split[1];
        //lookup pipeline stage query
        FhirQuery fhirQuery = MongoDbQuery
          .valueOf(queryName.replace("-", "_"))
          .getFhirQuery();
        //value might be a or conditions, e.g. code=1234,678
        JsonArray orConditions = new JsonArray();
        for (String orElement : parsedValue.split(",")) {
          orConditions.add(fhirQuery
            .setValue(orElement)
            .setPrefix(prefix)
            .mongoDbPipelineStageQuery());

        }
        return new JsonObject()
          .put("$lookup", new JsonObject()
            .put("from", type.getCollection())
            .put("let", new JsonObject()
              .put("searchParam", "$" + field))
            .put("pipeline", new JsonArray()
              .add(new JsonObject()
                .put("$match", new JsonObject()
                  .put("$expr", new JsonObject()
                    .put("$and", new JsonArray()
                      .add(new JsonObject()
                        .put("$or", orConditions))
                      .add(matchQuery)
                    ))
                )))
            .put("as", type.getCollection()));
      }


    }


    return null;
  }

}


