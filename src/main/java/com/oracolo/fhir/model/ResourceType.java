package com.oracolo.fhir.model;

import com.oracolo.fhir.utils.FhirUtils;

public enum ResourceType {

  Patient(FhirUtils.PATIENTS_COLLECTION),
  Encounter(FhirUtils.ENCOUNTER_COLLECTION),
  Observation(FhirUtils.OBSERVATIONS_COLLECTION),
  Condition(FhirUtils.CONDITIONS_COLLECTION),
  Procedure(FhirUtils.PROCEDURE_COLLECTION),
  Bundle(FhirUtils.BUNDLE_COLLECTION);

  private final String collection;

  ResourceType(String collection) {
    this.collection = collection;
  }

  public String getCollection() {
    return collection;
  }

}
