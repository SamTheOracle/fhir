package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentReferenceContext extends BackboneElement {
  /**
   * Describes the clinical encounter or type of care that the document content is associated with.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> encounter;
  /**
   * This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented.
   * In some cases, the event is inherent in the type Code, such as a "History and Physical Report" in which
   * the procedure being documented is necessarily a "History and Physical" act.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> event;
  /**
   * The time period over which the service that is described by the document was provided.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;
  /**
   * The kind of facility where the patient was seen.
   * <p>Code as example https://www.hl7.org/fhir/valueset-c80-facilitycodes.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept facilityType;
  /**
   * This property may convey specifics about the practice setting where the content was created, often reflecting the clinical specialty.
   * <p>Code as example https://www.hl7.org/fhir/terminologies.html#example</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept practiceSetting;

  /**
   * The Patient Information as known when the document was published. May be a reference to a version specific, or contained.
   * <p>Cardinality: 0..1</p>
   */
  private Reference sourcePatientInfo;
  /**
   * Related identifiers or resources associated with the DocumentReference.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> related;

  public List<Reference> getEncounter() {
    return encounter;
  }

  public void setEncounter(List<Reference> encounter) {
    this.encounter = encounter;
  }

  public List<CodeableConcept> getEvent() {
    return event;
  }

  public void setEvent(List<CodeableConcept> event) {
    this.event = event;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public CodeableConcept getFacilityType() {
    return facilityType;
  }

  public void setFacilityType(CodeableConcept facilityType) {
    this.facilityType = facilityType;
  }

  public CodeableConcept getPracticeSetting() {
    return practiceSetting;
  }

  public void setPracticeSetting(CodeableConcept practiceSetting) {
    this.practiceSetting = practiceSetting;
  }

  public Reference getSourcePatientInfo() {
    return sourcePatientInfo;
  }

  public void setSourcePatientInfo(Reference sourcePatientInfo) {
    this.sourcePatientInfo = sourcePatientInfo;
  }

  public List<Reference> getRelated() {
    return related;
  }

  public void setRelated(List<Reference> related) {
    this.related = related;
  }
}
