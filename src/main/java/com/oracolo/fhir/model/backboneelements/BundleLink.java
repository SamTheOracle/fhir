package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.Extension;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class BundleLink extends BackboneElement {
  /**
   * A name which details the functional use for this link - see http://www.iana.org/assignments/link-relations/link-relations.xhtml#link-relations-1 .
   * <p>Cardinality: 1..1</p>
   */
  private String relation;
  /**
   * Extension for relation
   * <p>Cardinality: 0..1</p>
   */
  private Extension _relation;
  /**
   * The reference details for the link.
   * <p>Cardinality: 1..1</p>
   */
  private String uri;
  /**
   * Extension for uri
   * <p>Cardinality: 0..1</p>
   */
  private Extension _uri;

  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public Extension get_relation() {
    return _relation;
  }

  public void set_relation(Extension _relation) {
    this._relation = _relation;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Extension get_uri() {
    return _uri;
  }

  public void set_uri(Extension _uri) {
    this._uri = _uri;
  }
}
