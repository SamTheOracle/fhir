package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonObject;

public class FamilyQuery implements FhirQuery {

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("name.family", new JsonObject()
//        .put("$regex", value)
//        .put("$options", "i"));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    return new JsonObject()
      .put("$regexMatch",
        new JsonObject()
          .put("input", "$name.family")
          .put("regex", paramValue)
          .put("options", "i"));
  }


}
