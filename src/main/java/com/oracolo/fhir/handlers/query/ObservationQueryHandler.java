package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ObservationQueryHandler extends BaseQueryHandler {

  private final String encounter = "encounter";

  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject baseQuery = super.createMongoDbQuery();
    JsonArray baseQueryOperations = baseQuery.getJsonArray("$and");
    String encounterReference = params.get(encounter);
    if (encounterReference != null) {
      baseQueryOperations.add(new JsonObject().put("encounter.reference", encounterReference));

    }
    return baseQuery;
  }
}
