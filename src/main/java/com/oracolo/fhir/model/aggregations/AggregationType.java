package com.oracolo.fhir.model.aggregations;

public enum AggregationType {
  ENCOUNTER("AggregationEncounter", AggregationEncounter.class);
  private final Class<? extends AggregationResource> clazz;
  private String type;

  AggregationType(String type, Class<? extends AggregationResource> clazz) {
    this.type = type;
    this.clazz = clazz;
  }


  public String type() {
    return type;
  }

  public Class<? extends AggregationResource> decodeClass() {
    return clazz;
  }
}
