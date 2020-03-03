package com.oracolo.fhir.handlers.query.mongo.parsers.prefix;

public class OperatorParser {

  public static final String pattern = "(gt)|(ge)|(lt)|(le)|(eb)|(sa)|(eq)|(ne)";

  public static OperatorParserResult parsePrefix(String paramValue) {
    for (Prefix p : Prefix.values()) {
      if (paramValue.contains(p.value())) {
        String[] value = paramValue.split(pattern);
        return new OperatorParserResult(p, value[1]);
      }
    }
    return new OperatorParserResult(Prefix.EQUAL, paramValue);
  }
}

