package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.Reference;
import com.oracolo.fhir.model.elements.Extension;

/**
 * The list of diagnosis relevant to this episode of care.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodeOfCareDiagnosis extends BackboneElement {

  /**
   * A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.
   * <p>Cardinality: 1..1</p>
   */
  private Reference condition;
  /**
   * Role that this diagnosis has within the episode of care (e.g. admission, billing, discharge …).
   * <p>Code preferred http://www.hl7.org/fhir/valueset-diagnosis-role.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept role;
  /**
   * Ranking of the diagnosis (for each role type).
   * <p>Cardinality: 0..1</p>
   */
  private int rank;
  /**
   * Extension for rank
   * <p>Cardinality: 0..1</p>
   */
  private Extension _rank    ;

  public Reference getCondition() {
    return condition;
  }

  public void setCondition(Reference condition) {
    this.condition = condition;
  }

  public CodeableConcept getRole() {
    return role;
  }

  public void setRole(CodeableConcept role) {
    this.role = role;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public Extension get_rank() {
    return _rank;
  }

  public void set_rank(Extension _rank) {
    this._rank = _rank;
  }
}
