package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.datatypes.Reference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PractitionerQualification {

  private List<Identifier> identifier;
  private CodeableConcept code;
  private Period period;
  private Reference issuer;

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public void setCode(CodeableConcept code) {
    this.code = code;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Reference getIssuer() {
    return issuer;
  }

  public void setIssuer(Reference issuer) {
    this.issuer = issuer;
  }
}
