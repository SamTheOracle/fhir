package com.oracolo.fhir.model.backboneelements;

import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.*;

import java.util.Date;
import java.util.List;

/**
 * Some observations have multiple component observations. These component observations are expressed as separate code value
 * pairs that share the same attributes. Examples include systolic and diastolic component observations for blood pressure measurement and multiple component observations for genetics observations.
 */
public class ObservationComponent extends BackboneElement {

  /**
   * Describes what was observed. Sometimes this is called the observation "code".
   * <p>Code not required</p>
   * <p>Cardinality: 1..1</p>
   */
  private CodeableConcept code;
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
  private Boolean valueBoolean;
  /**
   * Extension for valueBoolean
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueBoolean;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private int valueInteger;
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
  private Date time;
  /**
   * Extension for time
   * <p>Cardinality: 0..1</p>
   */
  private Extension _time;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Date dateTime;
  /**
   * Extension for dateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _dateTime;
  /**
   * The information determined as a result of making the observation, if the information has a simple value.
   * <p>Cardinality: 0..1</p>
   */
  private Period valuePeriod;
  /**
   * Provides a reason why the expected value in the element Observation.component.value[x] is missing.
   * <p>There must be a code if there is a value in the value set that can descrive the concept. Otherwise text</p>
   * <p>See https://www.hl7.org/fhir/valueset-data-absent-reason.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept dataAbsentReason;
  /**
   * A categorical assessment of an observation value. For example, high, low, normal.
   * <p>There must be a code if there is a value in the value set that can descrive the concept. Otherwise text</p>
   * <p>https://www.hl7.org/fhir/valueset-observation-interpretation.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> interpretation;
  /**
   * Guidance on how to interpret the value by comparison to a normal or recommended range.
   * <p>Cardinality: 0..*</p>
   */
  private List<ObservationReferenceRange> referenceRange;

  public CodeableConcept getCode() {
    return code;
  }

  public ObservationComponent setCode(CodeableConcept code) {
    this.code = code;
    return this;
  }

  public Quantity getValueQuantity() {
    return valueQuantity;
  }

  public ObservationComponent setValueQuantity(Quantity valueQuantity) {
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

  public ObservationComponent setValueString(String valueString) {
    this.valueString = valueString;
    return this;
  }

  public Extension get_valueString() {
    return _valueString;
  }

  public void set_valueString(Extension _valueString) {
    this._valueString = _valueString;
  }

  public Boolean isValueBoolean() {
    return valueBoolean;
  }

  public void setValueBoolean(Boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
  }

  public Extension get_valueBoolean() {
    return _valueBoolean;
  }

  public void set_valueBoolean(Extension _valueBoolean) {
    this._valueBoolean = _valueBoolean;
  }

  public int getValueInteger() {
    return valueInteger;
  }

  public ObservationComponent setValueInteger(int valueInteger) {
    this.valueInteger = valueInteger;
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

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public Extension get_time() {
    return _time;
  }

  public void set_time(Extension _time) {
    this._time = _time;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Extension get_dateTime() {
    return _dateTime;
  }

  public void set_dateTime(Extension _dateTime) {
    this._dateTime = _dateTime;
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

  public List<CodeableConcept> getInterpretation() {
    return interpretation;
  }

  public void setInterpretation(List<CodeableConcept> interpretation) {
    this.interpretation = interpretation;
  }

  public List<ObservationReferenceRange> getReferenceRange() {
    return referenceRange;
  }

  public void setReferenceRange(List<ObservationReferenceRange> referenceRange) {
    this.referenceRange = referenceRange;
  }
}
