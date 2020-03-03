package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonObject;

public class ContentQuery implements FhirQuery{


//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$text", new JsonObject()
//        .put("$search", value));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    return new JsonObject()
      .put("$text", new JsonObject()
        .put("$search", paramValue));
  }





}
