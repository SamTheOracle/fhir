package model.elements;

import model.Element;

/**
 * Specifies clinical/business/etc. metadata that can be used to retrieve, index and/or categorize an artifact.
 * This metadata can either be specific to the applicable population (e.g., age category, DRG) or
 * the specific context of care (e.g., venue, care setting, provider of care).
 */
public class UsageContext extends Element {
  /**
   * A code that identifies the type of context being specified by this usage context.
   * <p>See http://hl7.org/fhir/valueset-usage-context-type.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 1..1</p>
   */
  private Extension _code;
  /**
   * A value that defines the context specified in this context of use. The interpretation of the value is defined by the code.
   * <p>Cardinality: 1..1</p>
   */
  private CodeableConcept valueCodeableConcept;
  /**
   * A value that defines the context specified in this context of use. The interpretation of the value is defined by the code.
   * <p>Cardinality: 1..1</p>
   */
  private Quantity valueQuantity;
  /**
   * A value that defines the context specified in this context of use. The interpretation of the value is defined by the code.
   * <p>Cardinality: 1..1</p>
   */
  private Reference valueReference;
  /**
   * A value that defines the context specified in this context of use. The interpretation of the value is defined by the code.
   * <p>Cardinality: 1..1</p>
   */
  private Range valueRange;

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

  public CodeableConcept getValueCodeableConcept() {
    return valueCodeableConcept;
  }

  public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
    this.valueCodeableConcept = valueCodeableConcept;
  }

  public Quantity getValueQuantity() {
    return valueQuantity;
  }

  public void setValueQuantity(Quantity valueQuantity) {
    this.valueQuantity = valueQuantity;
  }

  public Reference getValueReference() {
    return valueReference;
  }

  public void setValueReference(Reference valueReference) {
    this.valueReference = valueReference;
  }

  public Range getValueRange() {
    return valueRange;
  }

  public void setValueRange(Range valueRange) {
    this.valueRange = valueRange;
  }
}
