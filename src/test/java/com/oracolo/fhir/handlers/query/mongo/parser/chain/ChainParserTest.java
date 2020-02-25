package com.oracolo.fhir.handlers.query.mongo.parser.chain;

import org.junit.jupiter.api.Test;

class ChainParserTest {


  @Test
  void parseChainParameter() {
    String paramValue = "giacomo";
    String paramName = "subject:Patient.name";

    ChainParserResult chainParserResult = ChainParserHandler.createLookupPipelineStage(paramName, paramValue);

  }
}
