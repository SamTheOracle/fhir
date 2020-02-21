package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class IdQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_id";
  }


  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("id", value);
    return this;
  }
}
