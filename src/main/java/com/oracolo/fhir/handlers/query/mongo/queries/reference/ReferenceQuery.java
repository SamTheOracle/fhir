package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonObject;

public interface ReferenceQuery extends FhirQuery {
  JsonObject mongoDbMatchQuery(String mongoDbStageVariable);
}
