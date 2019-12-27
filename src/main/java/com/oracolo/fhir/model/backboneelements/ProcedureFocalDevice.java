package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

/**
 * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement, fitting a prosthesis,
 * attaching a wound-vac, etc.) as a focal portion of the Procedure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcedureFocalDevice extends BackboneElement {

  /**
   * The kind of change that happened to the device during the procedure.
   * <p>Code preferred http://hl7.org/fhir/valueset-device-action.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept action;

  private Reference manipulated;

  public CodeableConcept getAction() {
    return action;
  }

  public void setAction(CodeableConcept action) {
    this.action = action;
  }

  public Reference getManipulated() {
    return manipulated;
  }

  /**
   * The device that was manipulated (changed) during the procedure.
   * <p>Cardinality: 1..1</p>
   */
  public ProcedureFocalDevice setManipulated(Reference manipulated) {
    this.manipulated = manipulated;
    return this;
  }
}
