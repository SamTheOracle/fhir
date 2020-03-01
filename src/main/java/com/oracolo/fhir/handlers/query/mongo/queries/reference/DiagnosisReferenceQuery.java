package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserHandler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DiagnosisReferenceQuery extends BaseMongoDbQuery {

  @Override
  public String name() {
    return "_content";
  }

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
  public JsonObject mongoDbPipelineStageQuery() {
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
        .put("regex", value)
        .put("options", "i"));
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName) {
    return ChainParserHandler.createLookupPipelineStage(paramName, value, prefix, new JsonObject()
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
