package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class IdentfierQuery extends BaseMongoDbQuery {
  @Override
  public String name() {
    return "identifier";
  }

  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("identifier.value", value);
    return this;
  }
}
