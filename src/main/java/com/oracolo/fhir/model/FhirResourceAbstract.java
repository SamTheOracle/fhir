package com.oracolo.fhir.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Metadata;

/**
 * This specification defines a series of different types of resource that can be used to exchange and/or store data in order to solve a wide range of healthcare related problems, both clinical and administrative. In addition, this specification defines several different ways of exchanging the resources.
 * <p>
 * A resource is an entity that:
 * <p>
 * 1) Has a known identity (a URL) by which it can be addressed
 * </p>
 * <p>
 * 2) Identifies itself as one of the types of resource defined in this specification
 * </p>
 * <p>
 * 3) Contains a set of structured data items as described by the definition of the resource type
 * </p>
 * <p>
 * 4) Has an identified version that changes if the contents of the resource change
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class FhirResourceAbstract {

  /**
   * The logical id of the resource, as used in the URL for the resource. Once assigned, this value never changes.
   * <p>Cardinality: 0..1</p>
   */
  protected String id;
  /**
   * The metadata about the resource. This is content that is maintained by the infrastructure.
   * Changes to the content might not always be associated with version changes to the resource.
   * <p>Cardinality: 0..1</p>
   */
  protected Metadata meta;
  /**
   * A reference to a set of rules that were followed when the resource was constructed, and which must be understood when processing the content.
   * Often, this is a reference to an implementation guide that defines the special rules along with other profiles etc.
   * <p>Cardinality: 0..1</p>
   */
  protected String implicitRules;
  /**
   * Extension for implicitRules
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _implicitRules;
  /**
   * The base language in which the resource is written
   * <p>Cardinality: 0..1</p>
   */
  protected String language;
  /**
   * Extension for language
   * <p>Cardinality: 0..1</p>
   */
  private Extension _language;


  public FhirResourceAbstract() {
  }

  public Extension get_implicitRules() {
    return _implicitRules;
  }

  public void set_implicitRules(Extension _implicitRules) {
    this._implicitRules = _implicitRules;
  }

  public Extension get_language() {
    return _language;
  }

  public void set_language(Extension _language) {
    this._language = _language;
  }

  public String getId() {
    return id;
  }


  public FhirResourceAbstract setId(String id) {
    this.id = id;
    return this;
  }

  public Metadata getMeta() {
    return meta;
  }

  public Patient setMeta(Metadata meta) {
    this.meta = meta;

    return null;
  }

  public String getImplicitRules() {
    return implicitRules;
  }

  public void setImplicitRules(String implicitRules) {
    this.implicitRules = implicitRules;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

}
