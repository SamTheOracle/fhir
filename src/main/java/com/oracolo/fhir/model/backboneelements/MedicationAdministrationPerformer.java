package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicationAdministrationPerformer extends BackboneElement {

  private CodeableConcept function;
  private Reference actor;

  public CodeableConcept getFunction() {
    return function;
  }

  public void setFunction(CodeableConcept function) {
    this.function = function;
  }

  public Reference getActor() {
    return actor;
  }

  public void setActor(Reference actor) {
    this.actor = actor;
  }
}
