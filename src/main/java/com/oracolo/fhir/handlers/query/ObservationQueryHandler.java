package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.parser.Prefix;
import com.oracolo.fhir.handlers.query.parser.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.parser.QueryPrefixResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ObservationQueryHandler extends BaseQueryHandler {

  static final String encounter = "encounter";
  static final String code = "code";
  static final String subject = "subject";
  static final String valueInteger = "valueInteger";


  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject baseQuery = super.createMongoDbQuery();
    JsonArray baseQueryOperations = baseQuery.getJsonArray("$and");
    String encounterReference = params.get(encounter);
    if (encounterReference != null) {
      baseQueryOperations.add(new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("encounter.reference", new JsonObject()
              .put("$regex", encounterReference)
              .put("$options", "i")))
          .add(new JsonObject()
            .put("encounter.display", new JsonObject()
              .put("$regex", encounterReference)
              .put("$options", "i")))
        ));
    }
    String code = params.get(ObservationQueryHandler.code);
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
    String subject = params.get(ObservationQueryHandler.subject);
    if (subject != null) {
      baseQueryOperations.add(new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("subject.reference", new JsonObject()
              .put("$regex", subject)
              .put("$options", "i")))
          .add(new JsonObject()
            .put("subject.display", new JsonObject()
              .put("$regex", subject)
              .put("$options", "i")))));
    }
    String value = params.get(ObservationQueryHandler.valueInteger);
    if (value != null) {
      QueryPrefixResult queryPrefixResult = QueryPrefixHandler.parsePrefix(value);
      if (queryPrefixResult != null) {
        Prefix prefix = queryPrefixResult.prefix();
        String parsedValue = queryPrefixResult.parsedValue();
        Integer valueInteger = Integer.parseInt(parsedValue);
        baseQueryOperations
          .add(new JsonObject()
            .put("valueInteger", new JsonObject().put(prefix.operator(), valueInteger)));
      }
    }
    return baseQuery;
  }
}
