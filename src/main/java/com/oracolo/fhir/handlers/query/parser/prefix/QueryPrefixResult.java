package com.oracolo.fhir.handlers.query.parser.prefix;

public class QueryPrefixResult {
  private Prefix prefix;
  private String parsedValue;

  public QueryPrefixResult(Prefix prefix, String parsedValue) {
    this.prefix = prefix;
    this.parsedValue = parsedValue;
  }

  public String parsedValue() {
    return parsedValue;
  }

  public Prefix prefix() {
    return prefix;
  }

}
