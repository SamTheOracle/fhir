package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;

public class DiagnosticReport extends DomainResource {

  private String resourceType = "DiagnosticReport";

  @Override
  public DiagnosticReport setId(String id) {
    return this;
  }

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
