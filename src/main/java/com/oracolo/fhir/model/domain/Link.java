package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

/**
 * An object representing a link to another duplicate resource
 */
public class Link {
  /**
   * The object resource that the link refers to.
   * <p>Cardinality: 1..1</p>
   */
  private Reference other;
  /**
   * The type of link between this patient resource and another patient resource.
   * <p>See type: http://hl7.org/fhir/valueset-link-type.html</p>
   * <p>Cardinaltiy: 1..1</p>
   */
  private String type;
  /**
   * Extension for type
   * <p>Cardinality: 0..1</p>
   */
  private Extension _type;

  public Reference getOther() {
    return other;
  }

  public void setOther(Reference other) {
    this.other = other;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Extension get_type() {
    return _type;
  }

  public void set_type(Extension _type) {
    this._type = _type;
  }
}
