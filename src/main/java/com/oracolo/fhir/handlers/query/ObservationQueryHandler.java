package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ObservationQueryHandler extends BaseQueryHandler {

  private final String encounter = "encounter";
  private final String code = "code";

  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject baseQuery = super.createMongoDbQuery();
    JsonArray baseQueryOperations = baseQuery.getJsonArray("$and");
    String encounterReference = params.get(encounter);
    if (encounterReference != null) {
      baseQueryOperations.add(new JsonObject().put("encounter.reference", encounterReference));
    }
    String code = params.get(this.code);
    if (code != null) {
      baseQueryOperations.add(new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("code.text", new JsonObject()
              .put("$regex", code)
              .put("$options", "i")))
          .add(new JsonObject()
            .put("code.coding.display", new JsonObject()
              .put("$regex", code)
              .put("$options", "i")))
          .add(new JsonObject()
            .put("code.coding.code", new JsonObject()
              .put("$regex", code)
              .put("$options", "i")))));
    }
    return baseQuery;
  }
}
