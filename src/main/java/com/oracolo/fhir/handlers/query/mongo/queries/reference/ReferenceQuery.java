package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import io.vertx.core.json.JsonObject;

public interface ReferenceQuery {
  JsonObject createMongoDbLookUpStage(String paramName, String paramValue);
}
