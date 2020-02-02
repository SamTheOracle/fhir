package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

/**
 * Relationships that this document has with other document references that already exist.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
