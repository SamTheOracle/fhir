package com.oracolo.fhir.handlers.query.mongo.parser.prefix;

public class QueryPrefixHandler {

  public static final String pattern = "(gt)|(ge)|(lt)|(le)|(eb)|(sa)|(eq)|(ne)";

  public static QueryPrefixResult parsePrefix(String paramValue) {
    for (Prefix p : Prefix.values()) {
      if (paramValue.contains(p.value())) {
        String[] value = paramValue.split(pattern);
        return new QueryPrefixResult(p, value[1]);
      }
    }
    return new QueryPrefixResult(Prefix.EQUAL, paramValue);
  }
}

