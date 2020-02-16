package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.FhirDomainResourceAbstract;

public class Medication extends FhirDomainResourceAbstract {
  private String resourceType = "Medication";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
