package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonObject;

public class ContentQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_content";
  }

  @Override
  public JsonObject mongoDbQuery() {
    return new JsonObject()
      .put("$text", new JsonObject()
        .put("$search", value));
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject()
      .put("$text", new JsonObject()
        .put("$search", value));
  }


}
