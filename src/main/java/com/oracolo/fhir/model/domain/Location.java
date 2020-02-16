package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.FhirDomainResourceAbstract;

public class Location extends FhirDomainResourceAbstract {

  private String resourceType = "Location";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
