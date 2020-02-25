package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ValueIntegerQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "valueInteger";
  }

  @Override
  public JsonObject mongoDbQuery() {
    return new JsonObject()
      .put("valueInteger", new JsonObject().put(prefix.operator(), Integer.parseInt(value)));
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject()
      .put(prefix.operator(), new JsonArray()
        .add("$valueInteger")
        .add(Integer.valueOf(value)));
  }

}
