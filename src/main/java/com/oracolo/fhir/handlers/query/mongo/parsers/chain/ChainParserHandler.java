package com.oracolo.fhir.handlers.query.mongo.parsers.chain;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.MongoDbQuery;
import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChainParserHandler {


  public static JsonObject createLookupPipelineStage(String paramName, String value, JsonObject matchQuery, String field) {
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
        for (String orElement : value.split(",")) {
          orConditions.add(fhirQuery
            .mongoDbPipelineStageQuery(paramName, orElement));

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


