package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

public class FamilyQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_content";
  }

  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("name.family", new JsonObject()
        .put("$regex", value)
        .put("$options", "i"));
    return this;
  }
}
