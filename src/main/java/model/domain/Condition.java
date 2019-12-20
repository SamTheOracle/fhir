package model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.DomainResource;
import model.backboneelements.ConditionEvidence;
import model.backboneelements.ConditionStage;
import model.datatypes.Identifier;
import model.datatypes.Period;
import model.elements.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical
 * concept that has risen to a level of concern.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Condition extends DomainResource {
  private String resourceType = "Condition";
  /**
   * Business identifiers assigned to this condition by the performer or other systems which remain
   * constant as the resource is updated and propagates from server to server.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * The clinical status of the condition.
   * <p>Code is required http://hl7.org/fhir/valueset-condition-clinical.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept clinicalStatus;
  /**
   * The verification status to support the clinical status of the condition.
   * verificationStatus is not required. For example, when a patient has abdominal pain in the ED, there
   * is not likely going to be a verification status. The data type is CodeableConcept because verificationStatus has some
   * clinical judgment involved, such that there might need to be more specificity than the required FHIR value set allows.
   * For example, a SNOMED coding might allow for additional specificity.
   * <p>Condition.clinicalStatus SHALL be present if verificationStatus is not entered-in-error and category is problem-list-item</p>
   * <p>Condition.clinicalStatus SHALL NOT be present if verification Status is entered-in-error	</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept verificationStatus;
  /**
   * A category assigned to the condition.
   * <p>Code is required but if not found should use another way to describe the concept </p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> category;
  /**
   * A subjective assessment of the severity of the condition as evaluated by the clinician.
   * <p>Code is preferred http://hl7.org/fhir/valueset-condition-severity.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept severity;
  /**
   * Identification of the condition, problem or diagnosis.
   * <p>Code is not required http://hl7.org/fhir/valueset-condition-code.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept code;
  /**
   * The anatomical location where this condition manifests itself.
   * <p>Code is not required http://hl7.org/fhir/valueset-body-site.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> bodySite;
  /**
   * Indicates the patient or group who the condition record is associated with.
   * <p>Cardinality: 0..1</p>
   */
  private Reference subject;
  /**
   * The Encounter during which this Condition was created or to which the creation of this record is tightly associated.
   * <p>Cardinality: 0..1</p>
   */
  private Reference encounter;
  /**
   * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
   * <p>Cardinality: 0..1</p>
   */
  private String onsetDateTime;
  /**
   * Extension for onsetDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _onsetDateTime;
  /**
   * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
   * <p>Cardinality: 0..1</p>
   */
  private Age onsetAge;
  /**
   * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
   * <p>Cardinality: 0..1</p>
   */
  private Range onsetRange;
  /**
   * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
   * <p>Cardinality: 0..1</p>
   */
  private Period onsetPeriod;
  /**
   * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
   * <p>Cardinality: 0..1</p>
   */
  private String onsetString;
  /**
   * Extension for onsetString
   * <p>Cardinality: 0..1</p>
   */
  private Extension _onsetString;
  /**
   * The date or estimated date that the condition resolved or went into remission. This is called "abatement"
   * because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never
   * really resolved, but they can abate.
   * <p>Cardinality: 0..1</p>
   */
  private String abatementDateTime;
  /**
   * Extension for abatementDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _abatementDateTime;
  /**
   * The date or estimated date that the condition resolved or went into remission. This is called "abatement"
   * because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never
   * really resolved, but they can abate.
   * <p>Cardinality: 0..1</p>
   */
  private Age abatementAge;
  /**
   * The date or estimated date that the condition resolved or went into remission. This is called "abatement"
   * because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never
   * really resolved, but they can abate.
   * <p>Cardinality: 0..1</p>
   */
  private Period abatementPeriod;
  /**
   * The date or estimated date that the condition resolved or went into remission. This is called "abatement"
   * because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never
   * really resolved, but they can abate.
   * <p>Cardinality: 0..1</p>
   */
  private Range abatementRange;
  /**
   * The date or estimated date that the condition resolved or went into remission. This is called "abatement"
   * because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never
   * really resolved, but they can abate.
   * <p>Cardinality: 0..1</p>
   */
  private String abatementString;
  /**
   * Extension for abatementString
   * <p>Cardinality: 0..1</p>
   */
  private Extension _abatementString;
  /**
   * The recordedDate represents when this particular Condition record was created in the system, which is often
   * a system-generated date.
   * <p>Cardinality: 0..1</p>
   */
  private String recordedDate;
  /**
   * Extension for recordedDate
   * <p>Cardinality: 0..1</p>
   */
  private Extension _recordedDate;
  /**
   * Individual who recorded the record and takes responsibility for its content.
   * <p>Cardinality: 0..1</p>
   */
  private Reference recorder;
  /**
   * Individual who is making the condition statement.
   * <p>Cardinality: 0..1</p>
   */
  private Reference asserter;
  /**
   * Clinical stage or grade of a condition. May include formal severity assessments.
   * <p>Cardinality: 0..*</p>
   */
  private List<ConditionStage> stage;
  /**
   * Supporting evidence / manifestations that are the basis of the Condition's verification status,
   * such as evidence that confirmed or refuted the condition.
   * <p>Cardinality: 0..*</p>
   */
  private List<ConditionEvidence> evidence;
  /**
   * Additional information about the Condition. This is a general notes/comments entry for
   * description of the Condition, its diagnosis and prognosis.
   * <p>Cardinality: 0..*</p>
   */
  private List<Annotation> note;

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public CodeableConcept getClinicalStatus() {
    return clinicalStatus;
  }

  public void setClinicalStatus(CodeableConcept clinicalStatus) {
    this.clinicalStatus = clinicalStatus;
  }

  public CodeableConcept getVerificationStatus() {
    return verificationStatus;
  }

  public void setVerificationStatus(CodeableConcept verificationStatus) {
    this.verificationStatus = verificationStatus;
  }

  public List<CodeableConcept> getCategory() {
    return category;
  }

  public void setCategory(List<CodeableConcept> category) {
    this.category = category;
  }

  public CodeableConcept getSeverity() {
    return severity;
  }

  public void setSeverity(CodeableConcept severity) {
    this.severity = severity;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public Condition setCode(CodeableConcept code) {
    this.code = code;
    return this;
  }

  public List<CodeableConcept> getBodySite() {
    return bodySite;
  }

  public void setBodySite(List<CodeableConcept> bodySite) {
    this.bodySite = bodySite;
  }

  public Reference getSubject() {
    return subject;
  }

  public void setSubject(Reference subject) {
    this.subject = subject;
  }

  public Reference getEncounter() {
    return encounter;
  }

  public Condition setEncounter(Reference encounter) {
    this.encounter = encounter;
    return this;
  }

  public String getOnsetDateTime() {
    return onsetDateTime;
  }

  public void setOnsetDateTime(String onsetDateTime) {
    this.onsetDateTime = onsetDateTime;
  }

  public Extension get_onsetDateTime() {
    return _onsetDateTime;
  }

  public void set_onsetDateTime(Extension _onsetDateTime) {
    this._onsetDateTime = _onsetDateTime;
  }

  public Age getOnsetAge() {
    return onsetAge;
  }

  public void setOnsetAge(Age onsetAge) {
    this.onsetAge = onsetAge;
  }

  public Range getOnsetRange() {
    return onsetRange;
  }

  public void setOnsetRange(Range onsetRange) {
    this.onsetRange = onsetRange;
  }

  public Period getOnsetPeriod() {
    return onsetPeriod;
  }

  public void setOnsetPeriod(Period onsetPeriod) {
    this.onsetPeriod = onsetPeriod;
  }

  public String getOnsetString() {
    return onsetString;
  }

  public void setOnsetString(String onsetString) {
    this.onsetString = onsetString;
  }

  public Extension get_onsetString() {
    return _onsetString;
  }

  public void set_onsetString(Extension _onsetString) {
    this._onsetString = _onsetString;
  }

  public String getAbatementDateTime() {
    return abatementDateTime;
  }

  public void setAbatementDateTime(String abatementDateTime) {
    this.abatementDateTime = abatementDateTime;
  }

  public Extension get_abatementDateTime() {
    return _abatementDateTime;
  }

  public void set_abatementDateTime(Extension _abatementDateTime) {
    this._abatementDateTime = _abatementDateTime;
  }

  public Age getAbatementAge() {
    return abatementAge;
  }

  public void setAbatementAge(Age abatementAge) {
    this.abatementAge = abatementAge;
  }

  public Period getAbatementPeriod() {
    return abatementPeriod;
  }

  public void setAbatementPeriod(Period abatementPeriod) {
    this.abatementPeriod = abatementPeriod;
  }

  public Range getAbatementRange() {
    return abatementRange;
  }

  public void setAbatementRange(Range abatementRange) {
    this.abatementRange = abatementRange;
  }

  public String getAbatementString() {
    return abatementString;
  }

  public void setAbatementString(String abatementString) {
    this.abatementString = abatementString;
  }

  public Extension get_abatementString() {
    return _abatementString;
  }

  public void set_abatementString(Extension _abatementString) {
    this._abatementString = _abatementString;
  }

  public String getRecordedDate() {
    return recordedDate;
  }

  public void setRecordedDate(String recordedDate) {
    this.recordedDate = recordedDate;
  }

  public Extension get_recordedDate() {
    return _recordedDate;
  }

  public void set_recordedDate(Extension _recordedDate) {
    this._recordedDate = _recordedDate;
  }

  public Reference getRecorder() {
    return recorder;
  }

  public void setRecorder(Reference recorder) {
    this.recorder = recorder;
  }

  public Reference getAsserter() {
    return asserter;
  }

  public void setAsserter(Reference asserter) {
    this.asserter = asserter;
  }

  public List<ConditionStage> getStage() {
    return stage;
  }

  public Condition setStage(List<ConditionStage> stage) {
    this.stage = stage;
    return this;
  }

  @Override
  public Condition setId(String id) {
    super.setId(id);
    return this;
  }

  public List<ConditionEvidence> getEvidence() {
    return evidence;
  }

  public void setEvidence(List<ConditionEvidence> evidence) {
    this.evidence = evidence;
  }

  public List<Annotation> getNote() {
    return note;
  }

  public void setNote(List<Annotation> note) {
    this.note = note;
  }

  public Condition addNewConditionStage(ConditionStage conditionStage) {
    if (stage == null) {
      stage = new ArrayList<>();
    }
    stage.add(conditionStage);
    return this;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public Condition addNewCategory(CodeableConcept codeableConcept) {
    if (category == null) {
      this.category = new ArrayList<>();
    }
    this.category.add(codeableConcept);
    return this;
  }

  public Condition addNewConditionEvidence(ConditionEvidence conditionEvidence) {
    if (evidence == null) {
      evidence = new ArrayList<>();
    }
    evidence.add(conditionEvidence);
    return this;
  }
}
