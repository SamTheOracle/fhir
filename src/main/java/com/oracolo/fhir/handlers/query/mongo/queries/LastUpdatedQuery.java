package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParser;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.Prefix;
import com.oracolo.fhir.handlers.query.mongo.parsers.prefix.OperatorParserResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LastUpdatedQuery implements FhirQuery {


//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$expr", new JsonObject()
//        .put(prefix.operator(), new JsonArray()
//          .add(new JsonObject()
//            .put("$dateFromString", new JsonObject()
//              .put("dateString", "$meta.lastUpdated")))
//          .add(new JsonObject()
//            .put("$dateFromString", new JsonObject()
//              .put("dateString", value))
//          )));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {
    OperatorParserResult operatorParserResult = OperatorParser.parsePrefix(paramValue);
    String value = operatorParserResult.parsedValue();
    Prefix prefix = operatorParserResult.prefix();
    return new JsonObject()
      .put(prefix.operator(), new JsonArray()
        .add(new JsonObject()
          .put("$dateFromString", new JsonObject()
            .put("dateString", "$meta.lastUpdated")))
        .add(new JsonObject()
          .put("$dateFromString", new JsonObject()
            .put("dateString", value))
        ));
  }




}
