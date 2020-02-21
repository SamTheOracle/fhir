package com.oracolo.fhir.handlers.query.parser.chain;

import com.oracolo.fhir.utils.ResourceType;
import org.junit.jupiter.api.Test;

class ChainParserTest {


  @Test
  void parseChainParameter() {
    String paramValue = "giacomo";
    String paramName = "subject.name";


    for (ResourceType type : ResourceType.values()) {
      if (paramName.contains(type.typeName())) {
        String[] split = paramName.split(":(.*)\\.");
        String one = split[0];
        String two = split[1];
        String dkjb = null;
      }
    }

  }
}
