package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonObject;

public class FamilyQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_content";
  }

  @Override
  public JsonObject mongoDbQuery() {
    return new JsonObject()
      .put("name.family", new JsonObject()
        .put("$regex", value)
        .put("$options", "i"));
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject()
      .put("$name.family", new JsonObject()
        .put("$regex", value)
        .put("$options", "i"));
  }


}
