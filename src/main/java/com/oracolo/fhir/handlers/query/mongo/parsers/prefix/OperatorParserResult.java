package com.oracolo.fhir.handlers.query.mongo.parsers.prefix;

public class OperatorParserResult {
  private Prefix prefix;
  private String parsedValue;

  public OperatorParserResult(Prefix prefix, String parsedValue) {
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
