package model.datatypes;

import model.Element;
import model.elements.Extension;

/**
 * Details of a Technology mediated contact point (phone, fax, email, etc.)
 */
public class ContactPoint extends Element {

  /**
   * Telecommunications form for contact point - what communications system is required to make use of the contact.
   * <p>See http://hl7.org/fhir/valueset-contact-point-system.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String system;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _system;
  /**
   *
   * The actual contact point details, in a form that is meaningful to the designated communication system
   * (i.e. phone number or email address).
   * <p>Cardinality: 0..1</p>
   *
   */
  private String value;
  /**
   * Extension for value
   * <p>Cardinality: 0..1</p>
   */
  private Extension _value;
  /**
   * Identifies the purpose for the contact point.
   * <p>See http://hl7.org/fhir/valueset-contact-point-use.html</p>
   *<p>Cardinality: 0..1</p>
   */
  private String use;
  /**
   * Extension for use
   * <p>Cardinality: 0..1</p>
   */
  private Extension _use;
  /**
   * Specifies a preferred order in which to use a set of contacts. ContactPoints with lower rank values are more
   * preferred than those with higher rank values.
   * <p>Cardinality: 0..1</p>
   */
  private int rank;
  /**
   * Extension for rank
   * <p>Cardinality: 0..1</p>
   */
  private Extension _rank;
  /**
   * Time period when the contact point was/is in use.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Extension get_system() {
    return _system;
  }

  public void set_system(Extension _system) {
    this._system = _system;
  }

  public Extension get_value() {
    return _value;
  }

  public void set_value(Extension _value) {
    this._value = _value;
  }

  public Extension get_use() {
    return _use;
  }

  public void set_use(Extension _use) {
    this._use = _use;
  }

  public Extension get_rank() {
    return _rank;
  }

  public void set_rank(Extension _rank) {
    this._rank = _rank;
  }
}
