package com.oracolo.fhir.model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.Element;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Each resource contains an element "meta", of type "Meta", which is a set of metadata that provides technical and workflow context
 * to the resource. The metadata items are all optional,though some or all of them may be
 * required in particular implementations or contexts of use.
 * All Metadata's fields are optional
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata extends Element {

  /**
   * Changes each time the response of the resource changes. Can be referenced in a resource reference.
   * It Can be used to ensure that updates are based on the latest version of the resource.
   * <p>Cardinality: 0..1</p>
   */
  private String versionId;
  /**
   * If populated, this value changes each time the response of the resource changes. It can be used by a system
   * or a human to judge the currency of the resource response.
   * Note that version aware updates do not depend on this element.
   * Note that a timezone code extension may be present on Meta.lastUpdated.
   * <p>Cardinality: 0..1</p>
   */
  private Instant lastUpdated;
  /**
   * A uri that identifies the source system of the resource.
   * <p>Cardinality: 0..1</p>
   */
  private String source;
  /**
   * An assertion that the response conforms to a resource profile
   * <p>Ex. a structure definition http://hl7.org/fhir/structuredefinition.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<String> profile;
  /**
   * Security labels applied to this resource. These tags connect resources in specific ways to the overall security policy
   * and infrastructure. Security tags can be updated when the resource changes, or whenever the security sub-system chooses to.
   * <p>Cardinality: 0..*</p>
   */
  private List<Coding> security;
  /**
   * Tags applied to this resource. Tags are used to relate resources to process and workflow.
   * Applications are not required to consider the tags when interpreting the meaning of a resource.
   * <p>Cardinality: 0..*</p>
   */
  private List<Coding> tag;

  public String getVersionId() {
    return versionId;
  }

  public Metadata setVersionId(String versionId) {
    this.versionId = versionId;
    return this;
  }

  public Instant getLastUpdated() {
    return lastUpdated;
  }

  public Metadata setLastUpdated(Instant lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public List<String> getProfile() {
    return profile;
  }

  public void setProfile(List<String> profile) {
    this.profile = profile;
  }

  public List<Coding> getSecurity() {
    return security;
  }

  public void setSecurity(List<Coding> security) {
    this.security = security;
  }

  public List<Coding> getTag() {
    return tag;
  }

  public void setTag(List<Coding> tag) {
    this.tag = tag;
  }

  public Metadata addNewTag(Coding tagValue) {
    if (tag == null) {
      tag = new ArrayList<>();
    }
    tag.add(tagValue);
    return this;
  }
}
