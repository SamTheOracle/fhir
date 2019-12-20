package model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.Element;
import model.elements.CodeableConcept;
import model.elements.Extension;
import model.elements.Reference;

/**
 * A numeric or alphanumeric string that is associated with a single object or entity within a given system. Typically, identifiers are
 * used to connect content in resources to external content available in other frameworks or protocols.
 * Identifiers are associated with objects and may be changed or retired due to human or system process and errors.
 * <p>
 * It can be a driving license or passport: any type of personal identifier
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Identifier extends Element {
  /**
   * Defines the type of use of this identifier.
   * <p>Usages: http://hl7.org/fhir/valueset-identifier-use.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String use;
  /**
   * Extension for use
   * <p>Cardinality: 0..1</p>
   */
  private Extension _use;
  /**
   * A coded type for the identifier that can be used to determine which identifier to use for a specific purpose.
   * <p>Types: http://hl7.org/fhir/valueset-identifier-type.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept type;
  /**
   * Establishes the namespace for the value - that is, a URL that describes a set values that are unique.
   * <p>Cardinality: 0..1</p>
   */
  private String system;
  /**
   * Extension for system
   * <p>Cardinality: 0..1</p>
   */
  private Extension _system;
  /**
   * The portion of the identifier typically relevant to the user and which is unique within the context of the system.
   * <p>Cardinality: 0..1</p>
   */
  private String value;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _value;
  /**
   * Time period during which identifier is/was valid for use.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;
  /**
   * Organization that issued/manages the identifier.
   * <p>Cardinality: 0..1</p>
   */
  private Reference assigner;

  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public CodeableConcept getType() {
    return type;
  }

  public void setType(CodeableConcept type) {
    this.type = type;
  }

  public String getSystem() {
    return system;
  }

  public Identifier setSystem(String system) {
    this.system = system;
    return this;
  }

  public String getValue() {
    return value;
  }

  public Identifier setValue(String value) {
    this.value = value;
    return this;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Extension get_use() {
    return _use;
  }

  public void set_use(Extension _use) {
    this._use = _use;
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

  public Reference getAssigner() {
    return assigner;
  }

  public void setAssigner(Reference assigner) {
    this.assigner = assigner;
  }
}
