package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;

/**
 * A length of time.
 */
public class Duration extends Element {
  /**
   * The value of the measured amount. The value includes an implicit precision in the presentation of the value.
   * <p>Cardinality: 0..1</p>
   */
  private double value;
  /**
   * Extension for value
   * <p>Cardinality: 0..1</p>
   */
  private Extension _value;
  /**
   * How the value should be understood and represented - whether the actual value is greater or less
   * than the stated value due to measurement issues; e.g. if the comparator is \"\u003c\" , then the real value is \u003c stated value.
   * <p>Cardinality: 0..1</p>
   */
  private String comparator;
  /**
   * Extension for comparator
   * <p>Cardinality: 0..1</p>
   */
  private Extension _comparator;
  /**
   * A human-readable form of the unit.
   * <p>Cardinality: 0..1</p>
   */
  private String unit;
  /**
   * Extension for unit
   * <p>Cardinality: 0..1</p>
   */
  private Extension _unit;
  /**
   * The identification of the system that provides the coded form of the unit.
   * <p>Cardinality: 0..1</p>
   */
  private String system;
  /**
   * Extension for system
   * <p>Cardinality: 0..1</p>
   */
  private Extension _system;
  /**
   * A computer processable form of the unit in some unit representation system.
   * <p>Cardinality: 0..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public Extension get_value() {
    return _value;
  }

  public void set_value(Extension _value) {
    this._value = _value;
  }

  public String getComparator() {
    return comparator;
  }

  public void setComparator(String comparator) {
    this.comparator = comparator;
  }

  public Extension get_comparator() {
    return _comparator;
  }

  public void set_comparator(Extension _comparator) {
    this._comparator = _comparator;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Extension get_unit() {
    return _unit;
  }

  public void set_unit(Extension _unit) {
    this._unit = _unit;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public Extension get_system() {
    return _system;
  }

  public void set_system(Extension _system) {
    this._system = _system;
  }

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
}
