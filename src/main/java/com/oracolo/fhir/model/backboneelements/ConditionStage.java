package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Clinical stage or grade of a condition. May include formal severity assessments.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionStage extends BackboneElement {
  /**
   * A simple summary of the stage such as "Stage 3". The determination of the stage is disease-specific.
   * <p>Code not required http://hl7.org/fhir/valueset-condition-stage.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept summary;

  /**
   * Reference to a formal record of the evidence on which the staging assessment is based.
   * <p>ClinicalImpression | DiagnosticReport | Observation</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> assessment;
  /**
   * The kind of staging, such as pathological or clinical staging.
   * <p>Code is not required http://hl7.org/fhir/valueset-condition-stage-type.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept type;

  public CodeableConcept getSummary() {
    return summary;
  }

  public ConditionStage setSummary(CodeableConcept summary) {
    this.summary = summary;
    return this;
  }

  public List<Reference> getAssessment() {
    return assessment;
  }

  public void setAssessment(List<Reference> assessment) {
    this.assessment = assessment;
  }

  public CodeableConcept getType() {
    return type;
  }

  public ConditionStage setType(CodeableConcept type) {
    this.type = type;
    return this;
  }

  public ConditionStage addNewAssessment(Reference observation) {
    if (assessment == null) {
      this.assessment = new ArrayList<>();
    }
    assessment.add(observation);
    return this;
  }
}
