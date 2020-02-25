package com.oracolo.fhir.model.datatypes;

import com.oracolo.fhir.model.Element;
import com.oracolo.fhir.model.backboneelements.Timing;
import com.oracolo.fhir.model.elements.Extension;

import java.util.Date;
import java.util.List;

/**
 * A description of a triggering event. Triggering events can be named events,
 * data events, or periodic, as determined by the type element.
 */
public class TriggerDefinition extends Element {
  /**
   * The type of triggering event.
   * <p>See http://hl7.org/fhir/valueset-trigger-type.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;
  /**
   * A formal name for the event. This may be an absolute URI that identifies the event formally
   * (e.g. from a trigger registry), or a simple relative URI that identifies the event in a local context.
   * <p>Cardinality: 0..1</p>
   */
  private String name;
  /**
   * Extension for name
   * <p>Cardinality: 0..1</p>
   */
  private Extension _name;
  /**
   * The timing of the event (if this is a periodic trigger).
   * <p>Cardinality: 0..1</p>
   */
  private Timing timingTiming;
  /**
   * The timing of the event (if this is a periodic trigger).
   * <p>Cardinality: 0..1</p>
   */
  private Reference timingReference;
  /**
   * The timing of the event (if this is a periodic trigger).
   * <p>Cardinality: 0..1</p>
   */
  private Date timingDate;
  /**
   * Extension for timingDate
   * <p>Cardinality: 0..1</p>
   */
  private Extension _timingDate;
  /**
   * The timing of the event (if this is a periodic trigger).
   * <p>Cardinality: 0..1</p>
   */
  private Date timingDateTime;
  /**
   * Extension for timingDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _timingDateTime;

  private List<DataRequirement> dataRequirement;
  private ExpressionElement condition;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Extension get_code() {
    return _code;
  }

  public void set_code(Extension _code) {
    this._code = _code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Extension get_name() {
    return _name;
  }

  public void set_name(Extension _name) {
    this._name = _name;
  }

  public Timing getTimingTiming() {
    return timingTiming;
  }

  public void setTimingTiming(Timing timingTiming) {
    this.timingTiming = timingTiming;
  }

  public Reference getTimingReference() {
    return timingReference;
  }

  public void setTimingReference(Reference timingReference) {
    this.timingReference = timingReference;
  }

  public Date getTimingDate() {
    return timingDate;
  }

  public void setTimingDate(Date timingDate) {
    this.timingDate = timingDate;
  }

  public Extension get_timingDate() {
    return _timingDate;
  }

  public void set_timingDate(Extension _timingDate) {
    this._timingDate = _timingDate;
  }

  public Date getTimingDateTime() {
    return timingDateTime;
  }

  public void setTimingDateTime(Date timingDateTime) {
    this.timingDateTime = timingDateTime;
  }

  public Extension get_timingDateTime() {
    return _timingDateTime;
  }

  public void set_timingDateTime(Extension _timingDateTime) {
    this._timingDateTime = _timingDateTime;
  }

  public List<DataRequirement> getDataRequirement() {
    return dataRequirement;
  }

  public void setDataRequirement(List<DataRequirement> dataRequirement) {
    this.dataRequirement = dataRequirement;
  }

  public ExpressionElement getCondition() {
    return condition;
  }

  public void setCondition(ExpressionElement condition) {
    this.condition = condition;
  }
}
