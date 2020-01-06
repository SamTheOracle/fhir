package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.Resource;
import com.oracolo.fhir.model.elements.Extension;

/**
 * Indicates the results of processing the corresponding 'request' entry in the batch or transaction being responded to or what the results of an operation where when returning history.
 * Rule: entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BundleResponse extends BackboneElement {

  /**
   * The status code returned by processing this entry.
   * The status SHALL start with a 3 digit HTTP code (e.g. 404) and may contain the standard HTTP description associated with the status code.
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * The Etag for the resource, if the operation for the entry produced a versioned resource (see Resource Metadata and Versioning and Managing Resource Contention).
   * <p>Cardinality: 0..1</p>
   */
  private String etag;
  /**
   * Extension for etag
   * <p>Cardinality: 0..1</p>
   */
  private Extension _etag;
  /**
   * The date/time that the resource was modified on the server.
   * <p>Cardinality: 0..1</p>
   */
  private String lastModified;
  /**
   * Extension for lastModified;
   * <p>Cardinality: 0..1</p>
   */
  private Extension _lastModified;
  /**
   * The location header created by processing this operation, populated if the operation returns a location.
   * <p>Cardinality: 0..1</p>
   */
  private String location;
  /**
   * Extension for location
   * <p>Cardinality: 0..1</p>
   */
  private Extension _location;
  /**
   * An OperationOutcome containing hints and warnings produced as part of processing this entry in a batch or transaction.
   * <p>Cardinality: 0..1</p>
   */
  private Resource outcome;


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  public Extension get_etag() {
    return _etag;
  }

  public void set_etag(Extension _etag) {
    this._etag = _etag;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public Extension get_lastModified() {
    return _lastModified;
  }

  public void set_lastModified(Extension _lastModified) {
    this._lastModified = _lastModified;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Extension get_location() {
    return _location;
  }

  public void set_location(Extension _location) {
    this._location = _location;
  }

  public Resource getOutcome() {
    return outcome;
  }

  public void setOutcome(Resource outcome) {
    this.outcome = outcome;
  }


}
