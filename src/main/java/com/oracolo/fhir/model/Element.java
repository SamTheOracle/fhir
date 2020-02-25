package com.oracolo.fhir.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.elements.Extension;

import java.util.List;

/**
 * Base definition for all elements in a resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Element {

  protected String id;

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
