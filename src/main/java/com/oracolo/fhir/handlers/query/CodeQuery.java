package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CodeQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_id";
  }

  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("code.text", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("code.coding.display", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("code.coding.code", new JsonObject()
            .put("$regex", value)
            .put("$options", "i"))));
    return this;
  }
}
