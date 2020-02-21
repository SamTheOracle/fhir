package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class ContentQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_content";
  }

  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("$text", new JsonObject()
        .put("$search", value));
    return this;
  }
}
