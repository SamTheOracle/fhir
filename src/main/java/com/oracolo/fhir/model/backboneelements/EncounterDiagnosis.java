package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

/**
 * The list of diagnosis relevant to this encounter.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterDiagnosis extends BackboneElement {

  /**
   * Reason the encounter takes place, as specified using information from another resource.
   * For admissions, this is the admission diagnosis. The indication will typically be a Condition
   * (with other resources referenced in the evidence.detail), or a Procedure.
   * <p>Cardinality: 1..1</p>
   */
  private Reference condition;
  /**
   * Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …).
   * <p>Code is preferred but not required https://www.hl7.org/fhir/valueset-diagnosis-role.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept use;
  /**
   * Ranking of the diagnosis (for each role type).
   * <p>Must be a positive int</p>
   * <p>Cardinality: 0..1</p>
   */
  private int rank;
  /**
   * Extension for rank
   * <p>Cardinality: 0..1</p>
   */
  private Extension _rank;

  public Reference getCondition() {
    return condition;
  }

  public EncounterDiagnosis setCondition(Reference condition) {
    this.condition = condition;
    return this;
  }

  public Extension get_rank() {
    return _rank;
  }

  public void set_rank(Extension _rank) {
    this._rank = _rank;
  }

  public CodeableConcept getUse() {
    return use;
  }

  public EncounterDiagnosis setUse(CodeableConcept use) {
    this.use = use;
    return this;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }
}
