package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Supporting evidence / manifestations that are the basis of the Condition's verification status,
 * such as evidence that confirmed or refuted the condition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionEvidence extends BackboneElement {
  /**
   * A manifestation or symptom that led to the recording of this condition.
   * <p>Code is not required http://hl7.org/fhir/valueset-manifestation-or-symptom.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> code;
  /**
   * Links to other relevant information, including pathology reports.
   * (Reference to any FHIR resource)
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> detail;

  public List<CodeableConcept> getCode() {
    return code;
  }

  public ConditionEvidence setCode(List<CodeableConcept> code) {
    this.code = code;
    return this;
  }

  public ConditionEvidence addNewCode(CodeableConcept codeableConcept) {
    if (code == null) {
      code = new ArrayList<>();
    }
    code.add(codeableConcept);
    return this;
  }

  public List<Reference> getDetail() {
    return detail;
  }

  public void setDetail(List<Reference> detail) {
    this.detail = detail;
  }

  public ConditionEvidence addNewDetail(Reference any) {
    if (detail == null) {
      detail = new ArrayList<>();
    }
    detail.add(any);
    return this;
  }
}
