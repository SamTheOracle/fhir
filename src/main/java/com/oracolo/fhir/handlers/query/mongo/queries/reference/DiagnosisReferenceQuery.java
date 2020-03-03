package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.chain.ChainParserHandler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DiagnosisReferenceQuery implements ReferenceQuery, FhirQuery {



//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$or", new JsonArray()
//        .add(new JsonObject()
//          .put("diagnosis.condition", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i")))
//      );
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", new JsonObject()
          .put("$reduce", new JsonObject()
            .put("input", "$diagnosis")
            .put("initialValue", "")
            .put("in", new JsonObject()
              .put("$concat", new JsonArray()
                .add("$$value")
                .add("$$this.condition.reference")
                .add(" ")))))
        .put("regex", paramValue)
        .put("options", "i"));
  }

  @Override
  public JsonObject createMongoDbLookUpStage(String paramName, String paramValue) {

    return ChainParserHandler.createLookupPipelineStage(paramName, paramValue, new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", new JsonObject()
          .put("$reduce", new JsonObject()
            .put("input", "$$searchParam")
            .put("initialValue", "")
            .put("in", new JsonObject()
              .put("$concat", new JsonArray()
                .add("$$value")
                .add("$$this.condition.reference")
                .add(" ")))
          ))
        .put("regex", "$id")
        .put("options", "i")
      ), "diagnosis");
  }


}
