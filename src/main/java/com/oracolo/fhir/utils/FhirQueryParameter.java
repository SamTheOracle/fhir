package com.oracolo.fhir.utils;

public class FhirQueryParameter {
  private String queryParamName;
  private String queryParamValue;

  private FhirQueryParameter(String queryParamName, String queryParamValue) {
    this.queryParamName = queryParamName;
    this.queryParamValue = queryParamValue;
  }

  static FhirQueryParameter of(String name, String value) {
    return new FhirQueryParameter(name, value);
  }

  public String name() {
    return queryParamName;
  }

  public String value() {
    return queryParamValue;
  }
}
