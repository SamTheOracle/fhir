package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GivenQuery extends BaseMongoDbQuery {

  @Override
  public String name() {
    return "given";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("name.given", new JsonObject()
//        .put("$regex", value)
//        .put("$options", "i"));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {

    //$in cannot be used in regex, so i need to reduce the array to a single string
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", new JsonObject()
          .put("$reduce", new JsonObject()
            .put("input", "$name.given")
            .put("initialValue", "")
            .put("in", new JsonObject()
              .put("$concat", new JsonArray()
                .add("$$value")
                .add("$$this")
                .add(" ")))
          ))
        .put("regex", value)
        .put("options", "i")
      );
  }


}
