package com.oracolo.fhir.model.backboneelements;

import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Quantity;
import com.oracolo.fhir.model.elements.Range;

import java.util.List;

/**
 * Guidance on how to interpret the value by comparison to a normal or recommended range.
 * Multiple reference ranges are interpreted as an "OR". In other words, to represent two distinct target populations, two referenceRange elements would be used.
 */
public class ObservationReferenceRange extends BackboneElement {
  /**
   * The value of the low bound of the reference range. The low bound of the
   * reference range endpoint is inclusive of the value (e.g. reference range is >=5 - <=9). If the low bound is omitted,
   * it is assumed to be meaningless (e.g. reference range is <=2.3).
   * <p>Cardinality: 0..1</p>
   */
  private Quantity low;
  /**
   * The value of the high bound of the reference range. The high bound of the reference range endpoint is inclusive of the value (e.g. reference range is >=5 - <=9).
   * If the high bound is omitted, it is assumed to be meaningless (e.g. reference range is >= 2.3).
   * <p>Cardinality: 0..1</p>
   */
  private Quantity high;
  /**
   * Codes to indicate the what part of the targeted reference population it applies to. For example, the normal or therapeutic range.
   * <p>Code is preferred but not required https://www.hl7.org/fhir/valueset-referencerange-meaning.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept type;
  /**
   * The age at which this reference range is applicable. This is a neonatal age (e.g. number of weeks at term) if the meaning says so.
   * <p>Cardinality: 0..1</p>
   */
  private Range age;
  /**
   * Text based reference range in an observation which may be used when a quantitative
   * range is not appropriate for an observation. An example would be a reference value of "Negative" or a list or table of "normals".
   * <p>Cardinality: 0..1</p>
   */
  private String text;
  /**
   * Extension for text
   * <p>Cardinality: 0..1</p>
   */
  private Extension _text;
  /**
   * Codes to indicate the target population this reference range applies to. For example, a reference range may be based on
   * the normal population or a particular sex or race. Multiple appliesTo are interpreted as an "AND" of the target populations.
   * For example, to represent a target population of African American females, both a code of female and a code for African American would be used.
   * <p>Code not required</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> appliesTo;

  public Quantity getLow() {
    return low;
  }

  public void setLow(Quantity low) {
    this.low = low;
  }

  public Quantity getHigh() {
    return high;
  }

  public void setHigh(Quantity high) {
    this.high = high;
  }

  public CodeableConcept getType() {
    return type;
  }

  public void setType(CodeableConcept type) {
    this.type = type;
  }

  public Range getAge() {
    return age;
  }

  public void setAge(Range age) {
    this.age = age;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Extension get_text() {
    return _text;
  }

  public void set_text(Extension _text) {
    this._text = _text;
  }

  public List<CodeableConcept> getAppliesTo() {
    return appliesTo;
  }

  public void setAppliesTo(List<CodeableConcept> appliesTo) {
    this.appliesTo = appliesTo;
  }
}
