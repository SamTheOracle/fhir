package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParser;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.Prefix;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParserResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ValueBooleanQuery implements FhirQuery {


//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("valueInteger", new JsonObject().put(prefix.operator(), Integer.parseInt(value)));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    return new JsonObject()
      .put("$valueBoolean", paramValue);
  }


}
