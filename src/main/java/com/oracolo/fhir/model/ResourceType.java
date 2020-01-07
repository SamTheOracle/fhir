package com.oracolo.fhir.model;

import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.utils.FhirUtils;

public enum ResourceType {

  PATIENT("Patient", FhirUtils.PATIENTS_COLLECTION, Patient.class),
  ENCOUNTER("Encounter", FhirUtils.ENCOUNTER_COLLECTION, Encounter.class),
  OBSERVATION("Observation", FhirUtils.OBSERVATIONS_COLLECTION, Observation.class),
  CONDITION("Condition", FhirUtils.CONDITIONS_COLLECTION, Condition.class),
  PROCEDURE("Procedure", FhirUtils.PROCEDURE_COLLECTION, Procedure.class),
  DIAGNOSTIC_REPORT("DiagnosticReport", FhirUtils.DIAGNOSTICREPORTS_COLLECTION, DiagnosticReport.class), //TO-DO
  MEDICATION_ADMINISTRATION("MedicationAdministration", FhirUtils.MEDICATIONADMINISTRATIONS_COLLECTIONS, MedicationAdministration.class),//TO-DO
  MEDICATION("Medication", FhirUtils.MEDICATIONS_COLLECTION, Medication.class),//TO-DO
  PRACTITIONER("Practitioner", FhirUtils.PRACTITIONERS_COLLECTION, Practitioner.class),//TO-DO
  LOCATION("Location", FhirUtils.LOCATIONS_COLLECTIONS, Location.class),//TO-DO
  ORGANIZATION("Organization", FhirUtils.ORGANIZATIONS_COLLECTION, Organization.class);//TO-DO


  private final String collection;
  private final Class<? extends Resource> clazz;
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

  public Class<? extends Resource> getResourceClass() {
    return clazz;
  }
}
