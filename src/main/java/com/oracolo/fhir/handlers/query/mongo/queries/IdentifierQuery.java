package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IdentifierQuery extends BaseMongoDbQuery {

  @Override
  public String name() {
    return "identifier";
  }

  @Override
  public JsonObject mongoDbQuery() {
    return new JsonObject()
      .put("identifier.value", value);
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {

    return new JsonObject()
      .put("$in", new JsonArray()
        .add(value)
        .add("$identifier.value"));
  }


}
