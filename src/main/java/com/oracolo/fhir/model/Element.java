package com.oracolo.fhir.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.elements.Extension;

import java.util.List;

/**
 * Base definition for all elements in a resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Element {
  /**
   * Unique id for the element within a resource (for internal references). This may be any string value that does not contain spaces.
   * <p>Cardinality: 0..1</p>
   */
  protected String id;
  /**
   * May be used to represent additional information that is not part of the basic definition of the element.
   * To make the use of extensions safe and manageable, there is a strict set of governance applied to the definition and use
   * of extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part
   * of the definition of the extension.
   * <p>Cardinality: 0..*</p>
   */
  protected List<Extension> extension;

  public String getId() {
    return id;
  }

  public Element setId(String id) {
    this.id = id;
    return this;
  }

  public List<Extension> getExtension() {
    return extension;
  }

  public void setExtension(List<Extension> extension) {
    this.extension = extension;
  }
}
