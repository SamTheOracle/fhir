package com.oracolo.fhir.handlers.query;

import io.vertx.core.json.JsonObject;

/**
 * Fhir Query Interface. Useful if SQL, RDF queries need to be added
 */
public interface FhirQuery {


  JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue);




}
