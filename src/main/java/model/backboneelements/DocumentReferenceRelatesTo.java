package model.backboneelements;

import model.BackboneElement;
import model.elements.Extension;
import model.elements.Reference;

/**
 * Relationships that this document has with other document references that already exist.
 */
public class DocumentReferenceRelatesTo extends BackboneElement {
  /**
   * The type of relationship that this document has with anther document.
   * <p>Code required http://hl7.org/fhir/valueset-document-relationship-type.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;

  /**
   * The target document of this relationship.
   * <p>Cardinality: 1..1</p>
   */
  private Reference target;

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

  public Reference getTarget() {
    return target;
  }

  public void setTarget(Reference target) {
    this.target = target;
  }


}
