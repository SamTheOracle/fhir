package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;

public class Practitioner extends DomainResource {
  String resourceType = "Practitioner";

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
