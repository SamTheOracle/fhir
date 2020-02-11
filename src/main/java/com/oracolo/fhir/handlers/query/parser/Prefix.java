package com.oracolo.fhir.handlers.query.parser;

public enum Prefix {
  EQUAL("eq", "$eq"),
  NOTEQUAL("ne", "$ne"),
  GREATERTHAN("gt", "$gt"),
  LESSTHAN("lt", "$lt"),
  LESSTHANEQUAL("le", "$lte"),
  GREATEREQUAL("ge", "$gte");

  private String value;
  private String operator;

  Prefix(String value, String operator) {
    this.value = value;
    this.operator = operator;
  }

  public String value() {
    return value;
  }

  public String operator() {
    return operator;
  }
}
