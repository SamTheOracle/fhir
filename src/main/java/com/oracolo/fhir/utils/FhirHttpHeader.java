package com.oracolo.fhir.utils;

public class FhirHttpHeader {


  public static final String PREFER = "Prefer";
  public static final String ACCEPT = "Accept";

  public static final FhirHttpHeader APPLICATION_JSON = of("Content-Type", "application/fhir+json");
  public static final FhirHttpHeader APPLICATION_JSON_VERSION = of("Content-Type", "application/fhir+json; fhirVersion=4.0");
  public static final FhirHttpHeader PREFER_MINIMAL = of("Prefer", "return=minimal");
  public static final FhirHttpHeader PREFER_REPRESENTATION = of("Prefer", "return=representation");
  public static final FhirHttpHeader PREFER_OUTCOME = of("Prefer", "return=outcome");
  public static final FhirHttpHeader APPLICATION_XML = of("Content-Type", "application/fhir+xml");
  public static final FhirHttpHeader TEXT = of("Content-Type", "text/plain");

  private String name, value;

  private FhirHttpHeader(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static FhirHttpHeader of(String name, String value) {
    return new FhirHttpHeader(name, value);
  }

  public static FhirHttpHeader fromPreferString(String preferHeader) {
    if (preferHeader.equals(PREFER_MINIMAL.value)) {
      return FhirHttpHeader.PREFER_MINIMAL;
    }
    if (preferHeader.equals(PREFER_REPRESENTATION.value)) {
      return FhirHttpHeader.PREFER_REPRESENTATION;
    } else {
      return FhirHttpHeader.PREFER_OUTCOME;
    }
  }

  public String name() {
    return name;
  }

  public String value() {
    return value;
  }
}
