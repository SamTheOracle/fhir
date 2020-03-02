package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserHandler;
import com.oracolo.fhir.handlers.query.mongo.parser.chain.ChainParserResult;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.QueryPrefixResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SubjectReferenceQuery extends BaseMongoDbQuery implements FhirQuery, ReferenceQuery {


  @Override
  public String name() {
    return "subject";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$or", new JsonArray()
//        .add(new JsonObject()
//          .put("subject.reference", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i")))
//        .add(new JsonObject()
//          .put("subject.display", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i"))));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("$regexMatch", new JsonObject()
            .put("input", "$subject.reference")
            .put("regex", value)
            .put("options", "i")))
        .add(new JsonObject()
          .put("$regexMatch", new JsonObject()
            .put("input", "$subject.display")
            .put("regex", value)
            .put("options", "i")))
      );
  }

  @Override
  public JsonObject createMongoDbLookUpStage(String paramName, QueryPrefixResult result) {
    super.prefix = result.prefix();
    super.value = result.parsedValue();
    return ChainParserHandler.createLookupPipelineStage(paramName, value, prefix, new JsonObject()
        .put("$regexMatch", new JsonObject()
          .put("input", "$$searchParam.reference")
          .put("regex", "$id")
          .put("options", "i")),
      "subject");
  }


}
