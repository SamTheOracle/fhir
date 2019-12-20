package model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.BackboneElement;
import model.elements.CodeableConcept;
import model.elements.Reference;

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
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept code;
  /**
   * Links to other relevant information, including pathology reports.
   * (Reference to any FHIR resource)
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> detail;

  public CodeableConcept getCode() {
    return code;
  }

  public void setCode(CodeableConcept code) {
    this.code = code;
  }

  public List<Reference> getDetail() {
    return detail;
  }

  public void setDetail(List<Reference> detail) {
    this.detail = detail;
  }
}
