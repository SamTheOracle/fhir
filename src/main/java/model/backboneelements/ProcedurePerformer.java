package model.backboneelements;

import model.BackboneElement;
import model.elements.CodeableConcept;
import model.elements.Reference;

/**
 * Limited to "real" people rather than equipment.
 */
public class ProcedurePerformer extends BackboneElement {
  /**
   * Distinguishes the type of involvement of the performer in the procedure. For example, surgeon, anaesthetist, endoscopist.
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept function;
  /**
   * The practitioner who was involved in the procedure.
   * <p>Cardinality: 1..1</p>
   */
  private Reference actor;
  /**
   * The organization the device or practitioner was acting on behalf of.
   * <p>Cardinality: 0..1</p>
   */
  private Reference onBehalfOf;

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

  public Reference getOnBehalfOf() {
    return onBehalfOf;
  }

  public void setOnBehalfOf(Reference onBehalfOf) {
    this.onBehalfOf = onBehalfOf;
  }
}
