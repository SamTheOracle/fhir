package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IdQuery implements FhirQuery {




//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("id", value);
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    return new JsonObject()
      .put("$eq", new JsonArray()
        .add("$id")
        .add(paramValue));
  }



}
