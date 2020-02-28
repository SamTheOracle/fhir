package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.mongo.parser.prefix.Prefix;
import io.vertx.core.json.JsonObject;

public interface FhirQuery {

  String name();

  JsonObject mongoDbPipelineStageQuery();

  FhirQuery setPrefix(Prefix prefix);

  FhirQuery setValue(String value);


}
