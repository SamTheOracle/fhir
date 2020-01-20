package com.oracolo.fhir.handlers.query.parser;

import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixResult;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

class QueryParserHandlerTest {

  @Test
  void parseQueryParameter() {

    String parameterValue = "gt12.3";
    QueryPrefixResult queryParserResult = QueryPrefixHandler.parsePrefix(parameterValue);
    JsonObject query = new JsonObject()
      .put(queryParserResult.prefix().operator(), queryParserResult.parsedValue());

  }
}
