package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;

public class Medication extends DomainResource {
  private String resourceType = "Medication";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
