package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.Quantity;
import com.oracolo.fhir.model.datatypes.Ratio;
import com.oracolo.fhir.model.datatypes.SimpleQuantity;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class MedicationAdministrationDosage extends BackboneElement {
  private String text;
  private CodeableConcept site, route, method;
  private Quantity dose;
  private Ratio rateRatio;
  private SimpleQuantity rateQuantity;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public CodeableConcept getSite() {
    return site;
  }

  public void setSite(CodeableConcept site) {
    this.site = site;
  }

  public CodeableConcept getRoute() {
    return route;
  }

  public void setRoute(CodeableConcept route) {
    this.route = route;
  }

  public CodeableConcept getMethod() {
    return method;
  }

  public void setMethod(CodeableConcept method) {
    this.method = method;
  }

  public Quantity getDose() {
    return dose;
  }

  public MedicationAdministrationDosage setDose(Quantity dose) {
    this.dose = dose;
    return this;
  }

  public Ratio getRateRatio() {
    return rateRatio;
  }

  public void setRateRatio(Ratio rateRatio) {
    this.rateRatio = rateRatio;
  }

  public SimpleQuantity getRateQuantity() {
    return rateQuantity;
  }

  public void setRateQuantity(SimpleQuantity rateQuantity) {
    this.rateQuantity = rateQuantity;
  }
}
