package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParser;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.Prefix;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParserResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

public class ValueQuantityQuery implements FhirQuery {


//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("valueInteger", new JsonObject().put(prefix.operator(), Integer.parseInt(value)));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {
    OperatorParserResult operatorParserResult = OperatorParser.parsePrefix(paramValue);
    String value = operatorParserResult.parsedValue();
    Prefix prefix = operatorParserResult.prefix();
    BigDecimal bigDecimal = BigDecimal.valueOf(Integer.parseInt(value));
    int precision = bigDecimal.precision() - 2;
    double scaleFactor = 5.0 / (Math.pow(10, precision));
    double firstRangeMember = Integer.parseInt(value) - scaleFactor;
    double secondRangeMember = Integer.parseInt(value) + scaleFactor;
    JsonObject rangeQuery = new JsonObject()
      .put("$and", new JsonArray()
        .add(new JsonObject()
          .put("$gte", new JsonArray()
            .add("$valueInteger")
            .add(firstRangeMember)))
        .add(new JsonObject()
          .put("$lt", new JsonArray()
            .add("$valueInteger")
            .add(secondRangeMember))));
    return new JsonObject()
      .put(prefix.operator(), new JsonArray()
        .add("$valueBoolean")
        .add(Boolean.parseBoolean(value)));
  }


}
