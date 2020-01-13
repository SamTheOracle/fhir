package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class PatientQueryHandler extends BaseQueryHandler {

  public static final String name = "name";
  public static final String family = "family";
  public static final String given = "given";

  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject baseQuery = super.createMongoDbQuery();
    JsonArray andOperations = baseQuery.getJsonArray("$and");
    List<String> nameValues = params.getAll(name);
    String familyValue = params.get(family);
    String givenValue = params.get(given);
    JsonObject textBasedQuery = baseQuery.getJsonObject("$text");
    if (textBasedQuery != null) {
      String textQuery = textBasedQuery.getString("$search");
      textBasedQuery.put("$search", textQuery.concat(" ").concat(String.join(" ", nameValues)));
      andOperations.add(new JsonObject().put("$text", textBasedQuery));
    }
    if (familyValue != null) {
      andOperations.add(new JsonObject()
        .put("name.family", new JsonObject()
          .put("$regex", familyValue)
          .put("$options", "i")));
    }
    if (givenValue != null) {
      andOperations.add(new JsonObject()
        .put("name.given", new JsonObject()
          .put("$regex", givenValue)
          .put("$options", "i")));
    }


    return baseQuery;
  }
}
