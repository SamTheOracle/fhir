package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class EncounterReferenceQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_content";
  }

  @Override
  public FhirQuery setValue(String value) {
    query = new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("encounter.reference", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("encounter.display", new JsonObject()
            .put("$regex", value)
            .put("$options", "i")))
      );
    return this;
  }
}
