package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;

public class MedicationAdministration extends DomainResource {
  private String resourceType = "MedicationAdministration";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
