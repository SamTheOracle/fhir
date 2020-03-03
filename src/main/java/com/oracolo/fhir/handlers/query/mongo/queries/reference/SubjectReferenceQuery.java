package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.chain.ChainParserHandler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SubjectReferenceQuery implements FhirQuery, ReferenceQuery {


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
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    return new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("$regexMatch", new JsonObject()
            .put("input", "$subject.reference")
            .put("regex", paramValue)
            .put("options", "i")))
        .add(new JsonObject()
          .put("$regexMatch", new JsonObject()
            .put("input", "$subject.display")
            .put("regex", paramValue)
            .put("options", "i")))
      );
  }

  @Override
  public JsonObject createMongoDbLookUpStage(String paramName, String paramValue) {
    return ChainParserHandler.createLookupPipelineStage(paramName, paramValue, new JsonObject()
        .put("$regexMatch", new JsonObject()
          .put("input", "$$searchParam.reference")
          .put("regex", "$id")
          .put("options", "i")),
      "subject");
  }


}
