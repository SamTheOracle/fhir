package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.mongo.parser.prefix.Prefix;
import io.vertx.core.json.JsonObject;

public interface FhirQuery {

  String name();

  JsonObject mongoDbPipelineStageQuery();

  /**
   * Creates a pipelineStageQuery by automatically parsing
   * a complex query parameter.
   * @param complexParamName complex query parameter
   * @return the query
   */
  JsonObject mongoDbPipelineStageQuery(String complexParamName);

  FhirQuery setPrefix(Prefix prefix);

  FhirQuery setValue(String value);



}
