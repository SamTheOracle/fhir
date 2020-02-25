package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.backboneelements.ProcedureFocalDevice;
import com.oracolo.fhir.model.backboneelements.ProcedurePerformer;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.elements.Extension;

import java.util.ArrayList;
import java.util.List;

/**
 * An action that is or was performed on or for a patient. This can be
 * a physical intervention like an operation, or less invasive like long term services, counseling, or hypnotherapy.
 * <p>http://hl7.org/fhir/procedure-definitions.html</p>
 * <p>code is mandatory</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Procedure extends FhirDomainResourceAbstract {

  private String resourceType = "Procedure";
  /**
   * Business identifiers assigned to this procedure by the performer or other systems which remain
   * constant as the resource is updated and is propagated from server to server.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * The location where the procedure actually happened. E.g. a newborn at home, a tracheostomy at a restaurant.
   * <p>Cardinality: 0..1</p>
   */
  private Reference location;
  /**
   * The URL pointing to a FHIR-defined protocol, guideline, order set or other definition that is
   * adhered to in whole or in part by this Procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> instantiatesCanonical;
  /**
   * Extension for instantiatesCanonical
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _instantiatesCanonical;
  /**
   * The URL pointing to an externally maintained protocol, guideline, order set or other definition
   * that is adhered to in whole or in part by this Procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> instantiatesUri;
  /**
   * Extension for instantiatesUri
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _instantiatesUri;
  /**
   * A reference to a resource that contains details of the request for this procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> basedOn;
  /**
   * A larger event of which this particular procedure is a component or step.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> partOf;

  private List<ProcedurePerformer> performer;
  /**
   * The coded reason why the procedure was performed. This may be a coded entity of some type, or may simply be present as text.
   * <p>Code is not required</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> reasonCode;
  /**
   * The justification of why the procedure was performed.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> reasonReference;
  /**
   * Detailed and structured anatomical location information. Multiple locations are allowed - e.g.
   * multiple punch biopsies of a lesion.
   * <p>Code is not required</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> bodySite;
  /**
   * This could be a histology result, pathology report, surgical report, etc.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> report;
  /**
   * Any complications that occurred during the procedure, or in the immediate post-performance period.
   * These are generally tracked separately from the notes, which will typically describe the procedure itself rather than any 'post procedure' issues.
   * <p>Code not required</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> complication;
  /**
   * Any complications that occurred during the procedure, or in the immediate post-performance period.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> complicationDetail;
  /**
   * If the procedure required specific follow up - e.g. removal of sutures. The follow up may
   * be represented as a simple note or could potentially be more complex, in which case the CarePlan resource can be used.
   * <p>Code not required</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> followUp;
  /**
   * Any other notes and comments about the procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<Annotation> note;
  /**
   * <p>Cardinality: 0..*</p>
   */
  private List<ProcedureFocalDevice> focalDevice;
  /**
   * Identifies medications, devices and any other substance used as part of the procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> usedReference;
  /**
   * Identifies coded items that were used as part of the procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> usedCode;

  /**
   * A code specifying the state of the procedure. Generally, this will be the in-progress or completed state.
   * <p>Code required http://hl7.org/fhir/valueset-event-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * Captures the reason for the current state of the procedure.
   * <p>Code not required</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept statusReason;

  /**
   * A code that classifies the procedure for searching, sorting and display purposes (e.g. "Surgical Procedure").
   * <p>Code not required</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept category;

  /**
   * The specific procedure that is performed. Use text if the exact nature of the procedure cannot be coded (e.g. "Laparoscopic Appendectomy").
   * <p>Code not required</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept code;
  /**
   * The person, animal or group on which the procedure was performed.
   * <p>Cardinality: 1..1</p>
   */
  private Reference subject;

  /**
   * The Encounter during which this Procedure was created or performed or to which the creation of this record is tightly associated.
   * <p>Cardinality: 0..1</p>
   */
  private Reference encounter;

  /**
   * Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period
   * to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
   * <p>Cardinality: 0..1</p>
   */
  private String performedDateTime;
  /**
   * Extension for performedDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _performedDateTime;
  /**
   * Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period
   * to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
   * <p>Cardinality: 0..1</p>
   */
  private Period performedPeriod;
  /**
   * Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period
   * to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
   * <p>Cardinality: 0..1</p>
   */
  private String performedString;
  /**
   * Extension for performedString
   * <p>Cardinality: 0..1</p>
   */
  private Extension _performedString;

  /**
   * Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period
   * to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
   * <p>Cardinality: 0..1</p>
   */
  private Age performedAge;

  /**
   * Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period
   * to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
   * <p>Cardinality: 0..1</p>
   */
  private Range performedRange;

  /**
   * Individual who recorded the record and takes responsibility for its response.
   * <p>Cardinality: 0..1</p>
   */
  private Reference recorder;

  /**
   * Individual who is making the procedure statement.
   * <p>Cardinality: 0..1</p>
   */
  private Reference asserter;


  /**
   * The outcome of the procedure - did it resolve the reasons for the procedure being performed?
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept outcome;

  public CodeableConcept getOutcome() {
    return outcome;
  }

  public Procedure setOutcome(CodeableConcept outcome) {
    this.outcome = outcome;
    return this;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public List<String> getInstantiatesCanonical() {
    return instantiatesCanonical;
  }

  public void setInstantiatesCanonical(List<String> instantiatesCanonical) {
    this.instantiatesCanonical = instantiatesCanonical;
  }

  public List<Extension> get_instantiatesCanonical() {
    return _instantiatesCanonical;
  }

  public void set_instantiatesCanonical(List<Extension> _instantiatesCanonical) {
    this._instantiatesCanonical = _instantiatesCanonical;
  }

  public List<String> getInstantiatesUri() {
    return instantiatesUri;
  }

  public void setInstantiatesUri(List<String> instantiatesUri) {
    this.instantiatesUri = instantiatesUri;
  }

  public List<Extension> get_instantiatesUri() {
    return _instantiatesUri;
  }

  public void set_instantiatesUri(List<Extension> _instantiatesUri) {
    this._instantiatesUri = _instantiatesUri;
  }

  public List<Reference> getBasedOn() {
    return basedOn;
  }

  public void setBasedOn(List<Reference> basedOn) {
    this.basedOn = basedOn;
  }

  public List<Reference> getPartOf() {
    return partOf;
  }

  public void setPartOf(List<Reference> partOf) {
    this.partOf = partOf;
  }

  public List<ProcedurePerformer> getPerformer() {
    return performer;
  }

  public void setPerformer(List<ProcedurePerformer> performer) {
    this.performer = performer;
  }

  public List<CodeableConcept> getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(List<CodeableConcept> reasonCode) {
    this.reasonCode = reasonCode;
  }

  public List<Reference> getReasonReference() {
    return reasonReference;
  }

  public void setReasonReference(List<Reference> reasonReference) {
    this.reasonReference = reasonReference;
  }

  public List<CodeableConcept> getBodySite() {
    return bodySite;
  }

  public void setBodySite(List<CodeableConcept> bodySite) {
    this.bodySite = bodySite;
  }

  public List<Reference> getReport() {
    return report;
  }

  public void setReport(List<Reference> report) {
    this.report = report;
  }

  public List<CodeableConcept> getComplication() {
    return complication;
  }

  public void setComplication(List<CodeableConcept> complication) {
    this.complication = complication;
  }

  public List<Reference> getComplicationDetail() {
    return complicationDetail;
  }

  public void setComplicationDetail(List<Reference> complicationDetail) {
    this.complicationDetail = complicationDetail;
  }

  public List<CodeableConcept> getFollowUp() {
    return followUp;
  }

  public void setFollowUp(List<CodeableConcept> followUp) {
    this.followUp = followUp;
  }

  public List<Annotation> getNote() {
    return note;
  }

  public void setNote(List<Annotation> note) {
    this.note = note;
  }

  public List<ProcedureFocalDevice> getFocalDevice() {
    return focalDevice;
  }

  public void setFocalDevice(List<ProcedureFocalDevice> focalDevice) {
    this.focalDevice = focalDevice;
  }

  public List<Reference> getUsedReference() {
    return usedReference;
  }

  public void setUsedReference(List<Reference> usedReference) {
    this.usedReference = usedReference;
  }

  public List<CodeableConcept> getUsedCode() {
    return usedCode;
  }

  public void setUsedCode(List<CodeableConcept> usedCode) {
    this.usedCode = usedCode;
  }

  public String getStatus() {
    return status;
  }

  public Procedure setStatus(String status) {
    this.status = status;
    return this;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public CodeableConcept getStatusReason() {
    return statusReason;
  }

  public void setStatusReason(CodeableConcept statusReason) {
    this.statusReason = statusReason;
  }

  public CodeableConcept getCategory() {
    return category;
  }

  public void setCategory(CodeableConcept category) {
    this.category = category;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public Procedure setCode(CodeableConcept code) {
    this.code = code;
    return this;
  }

  public Reference getSubject() {
    return subject;
  }

  public Procedure setSubject(Reference subject) {
    this.subject = subject;
    return this;
  }

  public Reference getEncounter() {
    return encounter;
  }

  public Procedure setEncounter(Reference encounter) {
    this.encounter = encounter;
    return this;
  }

  public String getPerformedDateTime() {
    return performedDateTime;
  }

  public Procedure setPerformedDateTime(String performedDateTime) {
    this.performedDateTime = performedDateTime;
    return this;
  }

  public Extension get_performedDateTime() {
    return _performedDateTime;
  }

  public void set_performedDateTime(Extension _performedDateTime) {
    this._performedDateTime = _performedDateTime;
  }

  public Period getPerformedPeriod() {
    return performedPeriod;
  }

  public void setPerformedPeriod(Period performedPeriod) {
    this.performedPeriod = performedPeriod;
  }

  public String getPerformedString() {
    return performedString;
  }

  public void setPerformedString(String performedString) {
    this.performedString = performedString;
  }

  public Extension get_performedString() {
    return _performedString;
  }

  public void set_performedString(Extension _performedString) {
    this._performedString = _performedString;
  }

  public Age getPerformedAge() {
    return performedAge;
  }

  public void setPerformedAge(Age performedAge) {
    this.performedAge = performedAge;
  }

  public Range getPerformedRange() {
    return performedRange;
  }

  public void setPerformedRange(Range performedRange) {
    this.performedRange = performedRange;
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

  public Reference getLocation() {
    return location;
  }

  public Procedure setLocation(Reference location) {
    this.location = location;
    return this;
  }

  @Override
  public Procedure setId(String id) {
    super.setId(id);
    return this;
  }

  public Procedure addNewReport(Reference report) {
    if (this.report == null) {
      this.report = new ArrayList<>();
    }
    this.report.add(report);
    return this;
  }

  public Procedure addNewContained(FhirDomainResourceAbstract dom) {
    if (contained == null) {
      contained = new ArrayList<>();
    }
    contained.add(dom);
    return this;
  }

  public Procedure addNewPartOf(Reference reference) {
    if (partOf == null) {
      partOf = new ArrayList<>();
    }
    partOf.add(reference);
    return this;
  }

  public Procedure addNewUsedCode(CodeableConcept usedCodeableConcept) {
    if (usedCode == null) {
      usedCode = new ArrayList<>();
    }
    usedCode.add(usedCodeableConcept);
    return this;
  }

  public Procedure addNewNote(Annotation annotation) {
    if (note == null) {
      note = new ArrayList<>();
    }
    note.add(annotation);
    return this;
  }
}
