package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.elements.Reference;

public class DiagnosticReport extends DomainResource {

  private String resourceType = "DiagnosticReport";

  private Reference encounter;

  public Reference getEncounter() {
    return encounter;
  }

  public DiagnosticReport setEncounter(Reference encounter) {
    this.encounter = encounter;
    return this;
  }

  @Override
  public DiagnosticReport setId(String id) {
    return this;
  }

  @Override
  public String getResourceType() {
    return resourceType;
  }
}
