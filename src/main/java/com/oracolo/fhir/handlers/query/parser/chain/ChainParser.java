package com.oracolo.fhir.handlers.query.parser.chain;

import com.oracolo.fhir.utils.ResourceType;

public class ChainParser {

  public ChainParserResult parseChainParameter(String paramName, String paramValue) {
    String queryName = null;
    for (ResourceType type : ResourceType.values()) {
      if (paramName.contains(type.typeName())) {
        //subject:Patient.name becomes split["subject","name"]
        String[] split = paramName.split(":(.*)\\.");
        queryName = split[1];

        // return new ChainParserResult(type.getCollection(),)

      }
    }
    if (queryName == null) {

    }

    return null;
  }
}
