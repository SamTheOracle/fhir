package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.ChainReference;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SubjectReferenceQuery extends BaseMongoDbQuery implements ChainReference {


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
          .put("subject.reference", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("subject.display", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
      );
  }

  @Override
  public JsonObject mongoDbMatchQuery(String mongoDbStageVariable) {
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", "$$" + mongoDbStageVariable + ".reference")
        .put("regex", "$id")
        .put("options", "i")
      );
  }
}
