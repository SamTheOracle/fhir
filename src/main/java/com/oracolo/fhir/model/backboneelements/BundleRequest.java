package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.Extension;

import java.time.Instant;

/**
 * Additional information about how this entry should be processed as part of a transaction or batch.
 * For history, it shows how the entry was processed to create the version contained in the entry.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BundleRequest extends BackboneElement {
  /**
   * In a transaction or batch, this is the HTTP action to be executed for this entry. In a history bundle, this indicates the HTTP action that occurred.
   * <p>Cardinality: 1..1</p>
   */
  private String method;
  /**
   * Extension for method
   * <p>Cardinality: 0..1</p>
   */
  private Extension _method;
  /**
   * The URL for this entry, relative to the root (the address to which the request is posted).
   * <p>Cardinality: 1..1</p>
   */
  private String url;
  /**
   * Extension for url
   * <p>Cardinality: 0..1</p>
   */
  private Extension _url;
  /**
   * If the ETag values match, return a 304 Not Modified status. See the API documentation for "Conditional Read".
   * <p>Cardinality: 0..1</p>
   */
  private String ifNoneMatch;
  /**
   * Extension for ifNoneMatch
   * <p>Cardinality: 0..1</p>
   */
  private Extension _ifNoneMatch;
  /**
   * Only perform the operation if the last updated date matches. See https://www.hl7.org/fhir/http.html#cread
   * <p>Cardinality: 0..1</p>
   */
  private Instant ifModifiedSince;
  /**
   * Extension for _ifModifiedSince
   * <p>Cardinality: 0..1</p>
   */
  private Extension _ifModifiedSince;
  /**
   * Instruct the server not to perform the create if a specified resource already exists. For further information, see the API documentation for "Conditional Create" https://www.hl7.org/fhir/http.html#ccreate.
   * This is just the query portion of the URL - what follows the "?" (not including the "?").
   * <p>Cardinality: 0..1</p>
   */
  private String ifNoneExist;
  /**
   * Extension for ifNoneExist
   * <p>Cardinality: 0..1</p>
   */
  private Extension _ifNoneExist;
  /**
   * Only perform the operation if the Etag value matches. For more information, see the API section "Managing Resource Contention" https://www.hl7.org/fhir/http.html#concurrency.
   * <p>Cardinality: 0..1</p>
   */
  private String ifMatch;
  /**
   * Extension for ifMatch
   * <p>Cardinality: 0..1</p>
   */
  private Extension _ifMatch;

  public String getMethod() {
    return method;
  }

  public BundleRequest setMethod(String method) {
    this.method = method;
    return this;
  }

  public Extension get_method() {
    return _method;
  }

  public void set_method(Extension _method) {
    this._method = _method;
  }

  public String getUrl() {
    return url;
  }

  public BundleRequest setUrl(String url) {
    this.url = url;
    return this;
  }

  public Extension get_url() {
    return _url;
  }

  public void set_url(Extension _url) {
    this._url = _url;
  }

  public String getIfNoneMatch() {
    return ifNoneMatch;
  }

  public void setIfNoneMatch(String ifNoneMatch) {
    this.ifNoneMatch = ifNoneMatch;
  }

  public Extension get_ifNoneMatch() {
    return _ifNoneMatch;
  }

  public void set_ifNoneMatch(Extension _ifNoneMatch) {
    this._ifNoneMatch = _ifNoneMatch;
  }

  public Instant getIfModifiedSince() {
    return ifModifiedSince;
  }

  public void setIfModifiedSince(Instant ifModifiedSince) {
    this.ifModifiedSince = ifModifiedSince;
  }

  public Extension get_ifModifiedSince() {
    return _ifModifiedSince;
  }

  public void set_ifModifiedSince(Extension _ifModifiedSince) {
    this._ifModifiedSince = _ifModifiedSince;
  }

  public String getIfNoneExist() {
    return ifNoneExist;
  }

  public void setIfNoneExist(String ifNoneExist) {
    this.ifNoneExist = ifNoneExist;
  }

  public Extension get_ifNoneExist() {
    return _ifNoneExist;
  }

  public void set_ifNoneExist(Extension _ifNoneExist) {
    this._ifNoneExist = _ifNoneExist;
  }

  public String getIfMatch() {
    return ifMatch;
  }

  public void setIfMatch(String ifMatch) {
    this.ifMatch = ifMatch;
  }

  public Extension get_ifMatch() {
    return _ifMatch;
  }

  public void set_ifMatch(Extension _ifMatch) {
    this._ifMatch = _ifMatch;
  }
}
