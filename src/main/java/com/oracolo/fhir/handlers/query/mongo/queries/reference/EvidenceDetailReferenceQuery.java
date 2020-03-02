package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class EvidenceDetailReferenceQuery extends BaseMongoDbQuery implements FhirQuery,ReferenceQuery {


  @Override
  public String name() {
    return "evidence-detail";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("evidence.detail.reference", value);
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    JsonObject innerStepReduce = new JsonObject();
    innerStepReduce
      .put("$reduce", new JsonObject()
        .put("input", "$evidence")
        .put("initialValue", new JsonArray())
        .put("in", new JsonObject()
          .put("$concatArrays", new JsonArray()
            .add("$$value")
            .add("$$this.detail"))));

    JsonObject outerStepReduce = new JsonObject()
      .put("$reduce", new JsonObject()
        .put("input", innerStepReduce)
        .put("initialValue", "")
        .put("in", new JsonObject()
          .put("$concat", new JsonArray()
            .add("$$value")
            .add("$$this.reference")
            .add(" "))));
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", outerStepReduce)
        .put("regex", value)
        .put("options", "i"));
  }

  @Override
  public JsonObject createMongoDbLookUpStage(String paramName, QueryPrefixResult result) {
    super.prefix = result.prefix();
    super.value = result.parsedValue();
    JsonObject innerStepReduce = new JsonObject();
    innerStepReduce
      .put("$reduce", new JsonObject()
        .put("input", "$$searchParam")
        .put("initialValue", new JsonArray())
        .put("in", new JsonObject()
          .put("$concatArrays", new JsonArray()
            .add("$$value")
            .add("$$this.detail"))));
    JsonObject outerStepReduce = new JsonObject()
      .put("$reduce", new JsonObject()
        .put("input", innerStepReduce)
        .put("initialValue", "")
        .put("in", new JsonObject()
          .put("$concat", new JsonArray()
            .add("$$value")
            .add("$$this.reference")
            .add(" "))));
    return ChainParserHandler.createLookupPipelineStage(paramName, value,prefix,
      new JsonObject().put("$regexMatch", new JsonObject()
        .put("input", outerStepReduce)
        .put("regex", "$id")
        .put("options", "i")),
      "evidence");
  }


}
