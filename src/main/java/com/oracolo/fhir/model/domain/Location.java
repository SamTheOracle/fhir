package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;

public class Location extends DomainResource {

  private String resourceType = "Location";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
