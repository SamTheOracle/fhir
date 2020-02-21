package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class LastUpdatedQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_lastUpdated";
  }


  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("meta.lastUpdated", new JsonObject().put(prefix.operator(), value));
    return this;
  }
}
