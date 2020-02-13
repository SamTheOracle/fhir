package com.oracolo.fhir.utils;

import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.FhirResourceAbstract;
import com.oracolo.fhir.model.domain.*;

public enum ResourceType {

  PATIENT("Patient", "patients", Patient.class),
  ENCOUNTER("Encounter", "encounters", Encounter.class),
  OBSERVATION("Observation", "observations", Observation.class),
  CONDITION("Condition", "conditions", Condition.class),
  PROCEDURE("Procedure", "procedures", Procedure.class),
  DIAGNOSTICREPORT("DiagnosticReport", "diagnosticreports", DiagnosticReport.class),
  MEDICATIONADMINISTRATION("MedicationAdministration", "medicationadministrations", MedicationAdministration.class),//TO-DO
  PRACTITIONER("Practitioner", "practitioners", Practitioner.class);


  private final String collection;
  private final Class<? extends FhirResourceAbstract> clazz;
  private final String value;

  ResourceType(String value, String collection, Class<? extends DomainResource> clazz) {
    this.collection = collection;
    this.clazz = clazz;
    this.value = value;
  }

  public String getCollection() {
    return collection;
  }

  public String typeName() {
    return value;
  }

  public Class<? extends FhirResourceAbstract> getResourceClass() {
    return clazz;
  }
}
