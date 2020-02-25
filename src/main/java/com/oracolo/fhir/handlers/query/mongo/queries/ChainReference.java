package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonObject;

public interface ChainReference extends FhirQuery {
  JsonObject mongoDbMatchQuery(String mongoDbStageVariable);
}
