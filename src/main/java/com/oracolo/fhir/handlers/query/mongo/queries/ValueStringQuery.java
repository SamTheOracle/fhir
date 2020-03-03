package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParser;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParserResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ValueStringQuery implements FhirQuery {



  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {
    OperatorParserResult operatorParserResult = OperatorParser.parsePrefix(paramValue);
    return new JsonObject()
      .put(operatorParserResult.prefix().operator(), new JsonArray()
        .add("$valueString")
        .add(operatorParserResult.parsedValue()));
  }


}
