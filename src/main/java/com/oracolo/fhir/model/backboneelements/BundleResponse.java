package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.FhirResourceAbstract;
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
  private FhirResourceAbstract outcome;


  public String getStatus() {
    return status;
  }

  public BundleResponse setStatus(String status) {
    this.status = status;
    return this;
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

  public BundleResponse setEtag(String etag) {
    this.etag = etag;
    return this;
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

  public BundleResponse setLastModified(String lastModified) {
    this.lastModified = lastModified;
    return this;
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

  public BundleResponse setLocation(String location) {
    this.location = location;
    return this;
  }

  public Extension get_location() {
    return _location;
  }

  public void set_location(Extension _location) {
    this._location = _location;
  }

  public FhirResourceAbstract getOutcome() {
    return outcome;
  }

  public BundleResponse setOutcome(FhirResourceAbstract outcome) {
    this.outcome = outcome;
    return this;
  }


}
