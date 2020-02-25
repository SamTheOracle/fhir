package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.backboneelements.ObservationComponent;
import com.oracolo.fhir.model.backboneelements.ObservationReferenceRange;
import com.oracolo.fhir.model.backboneelements.Timing;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.elements.Extension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Measurements and simple assertions made about a patient, device or other subject.
 * <p>Code is mandatory</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Observation extends FhirDomainResourceAbstract {

  private String resourceType = "Observation";
  /**
   * A unique identifier assigned to this observation.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * A plan, proposal or order that is fulfilled in whole or in part by this event. For example,
   * a MedicationRequest may require a patient to have laboratory test performed before it is dispensed.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> basedOn;
  /**
   * A plan, proposal or order that is fulfilled in whole or in part by this event. For example, a MedicationRequest may require a patient to have laboratory test performed before it is dispensed.
   * <p>Cardinality: 0..*</p>
   */
  /**
   * A larger event of which this particular Observation is a component or step. For example, an observation as part of a procedure.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> partOf;
  /**
   * A code that classifies the general type of observation being made.
   * <p>Code is not required, but it would be better if there were any https://www.hl7.org/fhir/valueset-observation-category.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> category;
  /**
   * The actual focus of an observation when it is not the patient of record representing something or
   * someone associated with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record.
   * The focus of an observation could also be an existing condition, an intervention, the subject's diet,
   * another observation of the subject, or a body structure such as tumor or implanted device.
   * An example use case would be using the Observation resource to capture whether the mother is trained to change her
   * child's tracheostomy tube. In this example, the child is the patient of record and the mother is the focus.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> focus;
  /**
   * The actual focus of an observation when it is not the patient of record representing something or someone associated
   * with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record.
   * The focus of an observation could also be an existing condition, an intervention, the subject's diet, another observation
   * of the subject, or a body structure such as tumor or implanted device.
   * An example use case would be using the Observation resource to capture whether the mother is trained to change her child's
   * tracheostomy tube. In this example, the child is the patient of record and the mother is the focus.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> performer;
  /**
   * A categorical assessment of an observation value. For example, high, low, normal.
   * <p>Code is required from value set https://www.hl7.org/fhir/valueset-observation-interpretation.html.
   * If there is no code covering the concept, a suitable text must be provided</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> interpretation;
  /**
   * Comments about the observation or the results.
   * <p>Cardinality: 0..*</p>
   */
  private List<Annotation> note;
  /**
   * Guidance on how to interpret the value by comparison to a normal or recommended range.
   * Multiple reference ranges are interpreted as an "OR". In other words, to represent two distinct target populations,
   * two referenceRange elements would be used.
   * <p>Cardinality: 0..*</p>
   */
  private List<ObservationReferenceRange> referenceRange;
  /**
   * This observation is a group observation (e.g. a battery, a panel of tests, a set of vital sign measurements)
   * that includes the target as a member of the group.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> hasMember;


  /**
   * The target resource that represents a measurement from which this observation value is derived. For example,
   * a calculated anion gap or a fetal measurement based on an ultrasound image.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> derivedFrom;

  private List<ObservationComponent> component;

  /**
   * The status of the result value.
   * <p>The code is mandatory https://www.hl7.org/fhir/valueset-observation-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Describes what was observed. Sometimes this is called the observation "name".
   * <p>Code is not required https://www.hl7.org/fhir/valueset-observation-codes.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private CodeableConcept code;
  /**
   * The patient, or group of patients, location, or device this observation is about and into whose record the observation is placed.
   * If the actual focus of the observation is different from the subject (or a sample of, part, or region of the subject), the focus element or the code itself specifies the actual focus of the observation.
   * <p>Cardinality: 0..1</p>
   */
  private Reference subject;
  /**
   * The healthcare event (e.g. a patient and healthcare provider interaction) during which this observation is made.
   * <p>Cardinality: 0..1</p>
   */
  private Reference encounter;
  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients -
   * this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
   * <p>Cardinality: 0..1</p>
   */
  private String effectiveDateTime;
  /**
   * Extension for effectiveDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _effectiveDateTime;
  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients -
   * this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
   * <p>Cardinality: 0..1</p>
   */
  private Period effectivePeriod;
  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients -
   * this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
   * <p>Cardinality: 0..1</p>
   */
  private Timing effectiveTiming;
  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients -
   * this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
   * <p>Cardinality: 0..1</p>
   */
  private Instant effectiveInstant;
  /**
   * Extension for effectiveInstant
   * <p>Cardinality: 0..1</p>
   */
  private Extension _effectiveInstant;
  /**
   * The date and time this version of the observation was made available to providers, typically after the results have been reviewed and verified.
   * <p>Cardinality: 0..1</p>
   */
  private Instant issued;
  /**
   * Extension for issued
   * <p>Cardinality: 0..1</p>
   */
  private Extension _issued;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Quantity valueQuantity;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept valueCodeableConcept;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private String valueString;
  /**
   * Extension for valueString
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueString;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Boolean valueBoolean = null;
  /**
   * Extension for valueBoolean
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueBoolean;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Integer valueInteger = null;
  /**
   * Extension for valueInteger
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueInteger;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Range valueRange;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Ratio valueRatio;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private SampleData valueSampleData;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Date valueTime;
  /**
   * Extension for valueTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueTime;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZZZZZ")
  private String valueDateTime;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Period valuePeriod;
  /**
   * Provides a reason why the expected value in the element Observation.value[x] is missing.
   * <p>Code required if covers context https://www.hl7.org/fhir/valueset-data-absent-reason.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept dataAbsentReason;
  /**
   * Indicates the site on the subject's body where the observation was made (i.e. the target site).
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept bodySite;
  /**
   * Indicates the mechanism used to perform the observation.
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept method;
  /**
   * The specimen that was used when this observation was made.
   * <p>Cardinality: 0..1</p>
   */
  private Reference specimen;


  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
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

  public List<CodeableConcept> getCategory() {
    return category;
  }

  public void setCategory(List<CodeableConcept> category) {
    this.category = category;
  }

  public List<Reference> getFocus() {
    return focus;
  }

  public void setFocus(List<Reference> focus) {
    this.focus = focus;
  }

  public List<Reference> getPerformer() {
    return performer;
  }

  public void setPerformer(List<Reference> performer) {
    this.performer = performer;
  }

  public List<CodeableConcept> getInterpretation() {
    return interpretation;
  }

  public void setInterpretation(List<CodeableConcept> interpretation) {
    this.interpretation = interpretation;
  }

  public List<Annotation> getNote() {
    return note;
  }

  public void setNote(List<Annotation> note) {
    this.note = note;
  }

  public List<ObservationReferenceRange> getReferenceRange() {
    return referenceRange;
  }

  public void setReferenceRange(List<ObservationReferenceRange> referenceRange) {
    this.referenceRange = referenceRange;
  }

  public List<Reference> getHasMember() {
    return hasMember;
  }

  public void setHasMember(List<Reference> hasMember) {
    this.hasMember = hasMember;
  }

  public List<Reference> getDerivedFrom() {
    return derivedFrom;
  }

  public void setDerivedFrom(List<Reference> derivedFrom) {
    this.derivedFrom = derivedFrom;
  }

  public List<ObservationComponent> getComponent() {
    return component;
  }

  public void setComponent(List<ObservationComponent> component) {
    this.component = component;
  }

  /**
   * The status of the result value.
   * <p>The code is mandatory https://www.hl7.org/fhir/valueset-observation-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  public String getStatus() {
    return status;
  }

  /**
   * The status of the result value.
   * <p>The code is mandatory https://www.hl7.org/fhir/valueset-observation-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  public Observation setStatus(String status) {
    this.status = status;
    return this;
  }

  public Reference getSubject() {
    return subject;
  }

  public Observation setSubject(Reference subject) {
    this.subject = subject;
    return this;
  }

  public Reference getEncounter() {
    return encounter;
  }

  public Observation setEncounter(Reference encounter) {
    this.encounter = encounter;
    return this;
  }

  public String getEffectiveDateTime() {
    return effectiveDateTime;
  }

  public Observation setEffectiveDateTime(String effectiveDateTime) {
    this.effectiveDateTime = effectiveDateTime;
    return this;
  }

  public Extension get_effectiveDateTime() {
    return _effectiveDateTime;
  }

  public void set_effectiveDateTime(Extension _effectiveDateTime) {
    this._effectiveDateTime = _effectiveDateTime;
  }

  public Period getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(Period effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
  }

  public Timing getEffectiveTiming() {
    return effectiveTiming;
  }

  public void setEffectiveTiming(Timing effectiveTiming) {
    this.effectiveTiming = effectiveTiming;
  }

  public Instant getEffectiveInstant() {
    return effectiveInstant;
  }

  public void setEffectiveInstant(Instant effectiveInstant) {
    this.effectiveInstant = effectiveInstant;
  }

  public Extension get_effectiveInstant() {
    return _effectiveInstant;
  }

  public void set_effectiveInstant(Extension _effectiveInstant) {
    this._effectiveInstant = _effectiveInstant;
  }

  public Instant getIssued() {
    return issued;
  }

  public void setIssued(Instant issued) {
    this.issued = issued;
  }

  public Extension get_issued() {
    return _issued;
  }

  public void set_issued(Extension _issued) {
    this._issued = _issued;
  }

  public Quantity getValueQuantity() {
    return valueQuantity;
  }

  public Observation setValueQuantity(Quantity valueQuantity) {
    this.valueQuantity = valueQuantity;
    return this;
  }

  public CodeableConcept getValueCodeableConcept() {
    return valueCodeableConcept;
  }

  public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
    this.valueCodeableConcept = valueCodeableConcept;
  }

  public String getValueString() {
    return valueString;
  }

  public Observation setValueString(String valueString) {
    this.valueString = valueString;
    return this;
  }

  public Extension get_valueString() {
    return _valueString;
  }

  public void set_valueString(Extension _valueString) {
    this._valueString = _valueString;
  }

  public Boolean getValueBoolean() {
    return valueBoolean;
  }

  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  public Observation setValueBoolean(Boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
    return this;
  }

  public Extension get_valueBoolean() {
    return _valueBoolean;
  }

  public void set_valueBoolean(Extension _valueBoolean) {
    this._valueBoolean = _valueBoolean;
  }

  public Integer getValueInteger() {
    return valueInteger;
  }

  public Observation setValueInteger(int valueInteger) {
    this.valueInteger = valueInteger;
    return this;
  }

  @Override
  public Observation setId(String id) {
    super.setId(id);
    return this;
  }

  public Extension get_valueInteger() {
    return _valueInteger;
  }

  public void set_valueInteger(Extension _valueInteger) {
    this._valueInteger = _valueInteger;
  }

  public Range getValueRange() {
    return valueRange;
  }

  public void setValueRange(Range valueRange) {
    this.valueRange = valueRange;
  }

  public Ratio getValueRatio() {
    return valueRatio;
  }

  public void setValueRatio(Ratio valueRatio) {
    this.valueRatio = valueRatio;
  }

  public SampleData getValueSampleData() {
    return valueSampleData;
  }

  public void setValueSampleData(SampleData valueSampleData) {
    this.valueSampleData = valueSampleData;
  }

  public Date getValueTime() {
    return valueTime;
  }

  public void setValueTime(Date valueTime) {
    this.valueTime = valueTime;
  }

  public Extension get_valueTime() {
    return _valueTime;
  }

  public void set_valueTime(Extension _valueTime) {
    this._valueTime = _valueTime;
  }

  public String getValueDateTime() {
    return valueDateTime;
  }

  public void setValueDateTime(String valueDateTime) {
    this.valueDateTime = valueDateTime;
  }

  public Period getValuePeriod() {
    return valuePeriod;
  }

  public void setValuePeriod(Period valuePeriod) {
    this.valuePeriod = valuePeriod;
  }

  public CodeableConcept getDataAbsentReason() {
    return dataAbsentReason;
  }

  public void setDataAbsentReason(CodeableConcept dataAbsentReason) {
    this.dataAbsentReason = dataAbsentReason;
  }

  public CodeableConcept getBodySite() {
    return bodySite;
  }

  public Observation setBodySite(CodeableConcept bodySite) {
    this.bodySite = bodySite;
    return this;
  }

  public CodeableConcept getMethod() {
    return method;
  }

  public void setMethod(CodeableConcept method) {
    this.method = method;
  }

  public Reference getSpecimen() {
    return specimen;
  }

  public void setSpecimen(Reference specimen) {
    this.specimen = specimen;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public Observation setCode(CodeableConcept code) {
    this.code = code;
    return this;
  }

  public Observation addNewPartOfReference(Reference reference) {
    if (partOf == null) {
      partOf = new ArrayList<>();
    }
    partOf.add(reference);
    return this;
  }

  public Observation addNewHasMember(Reference reference) {
    if (hasMember == null) {
      hasMember = new ArrayList<>();
    }
    hasMember.add(reference);
    return this;
  }

  public Observation addNewContained(Object observation) {
    if (contained == null) {
      contained = new ArrayList<>();
    }
    return this;
  }

  public Observation addNewObservationComponent(ObservationComponent observationComponent) {
    if (component == null) {
      component = new ArrayList<>();
    }
    component.add(observationComponent);
    return this;
  }

  public Observation addNewIdentifier(Identifier identifier) {
    if (this.identifier == null) {
      this.identifier = new ArrayList<>();
    }
    this.identifier.add(identifier);
    return this;
  }

  public Observation addNewNote(Annotation annotation) {
    if (note == null) {
      note = new ArrayList<>();
    }
    note.add(annotation);
    return this;
  }
}
