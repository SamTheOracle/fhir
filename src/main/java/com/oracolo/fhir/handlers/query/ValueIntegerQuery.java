package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class ValueIntegerQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "valueInteger";
  }


  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("valueInteger", new JsonObject().put(prefix.operator(), Integer.parseInt(value)));
    return this;
  }
}
