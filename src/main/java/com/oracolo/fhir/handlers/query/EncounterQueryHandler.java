package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class EncounterQueryHandler extends BaseQueryHandler implements QueryHandler {

  private final String subject = "subject";
  private final String partOf = "partOf";
  private final String participant = "participant";
  private final String identifier = "identifier";
  private final String participant_type = "participant-type";

  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject baseQuery = super.createMongoDbQuery();
    JsonArray baseQueryOperations = baseQuery.getJsonArray("$and");

    String subject = params.get(this.subject);
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
    params.getAll(this.partOf).forEach(partOfQuery -> baseQueryOperations.add(new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("partOf.reference", new JsonObject()
            .put("$regex", partOf)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("partOf.display", new JsonObject()
            .put("$regex", partOf)
            .put("$options", "i"))))));


    params.getAll(participant).forEach(participantQuery -> baseQueryOperations.add(new JsonObject()
      .put("$or", new JsonArray()
        .add(new JsonObject()
          .put("participant.individual.reference", new JsonObject()
            .put("$regex", participantQuery)
            .put("$options", "i")))
        .add(new JsonObject()
          .put("participant.individual.display", new JsonObject()
            .put("$regex", participantQuery)
            .put("$options", "i"))))));

    params.getAll(participant_type).forEach(participantTypeQuery -> {
      baseQueryOperations.add(new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("participant.type.coding.code", new JsonObject()
              .put("$regex", participantTypeQuery)
              .put("$options", "i")))
          .add(new JsonObject()
            .put("participant.type.text", new JsonObject()
              .put("$regex", participantTypeQuery)
              .put("$options", "i")))
        ));
    });
    //only one identifier
    String identifierQuery = params.get(identifier);
    if (identifierQuery != null) {
      baseQueryOperations.add(new JsonObject()
        .put("identifier.value", identifierQuery));
    }


    return baseQuery;
  }
}
