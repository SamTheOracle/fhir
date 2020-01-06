package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.Extension;

import java.util.List;

/**
 * An entry in a bundle resource - will either contain a resource or information about a resource (transactions and history only).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BundleEntry extends BackboneElement {

  /**
   * A series of links that provide context to this entry.
   * <p>Cardinality: 0..*</p>
   */
  private List<BundleLink> link;
  /**
   * The Absolute URL for the resource. The fullUrl SHALL NOT disagree with the id in the resource - i.e. if the fullUrl is not a urn:uuid, the URL shall be version-independent URL consistent with the Resource.id. The fullUrl is a version independent reference to the resource.
   * The fullUrl element SHALL have a value except that:
   * fullUrl can be empty on a POST (although it does not need to when specifying a temporary id for reference in the bundle);
   * Results from operations might involve resources that are not identified.
   * <p>Cardinality: 0..1</p>
   */
  private String fullUrl;
  /**
   * Extension for fullUrl
   * <p>Cardinality: 0..1</p>
   */
  private Extension _fullUrl;

  /**
   * The Resource for the entry. The purpose/meaning of the resource is determined by the Bundle.type.
   * <p>Cardinality: 0..1</p>
   */
  private Object resource;
  /**
   * Information about the search process that lead to the creation of this entry.
   * <p>Cardinality: 0..1</p>
   * <p>entry.search only when a search	entry.search.empty() or (type = 'searchset')</p>
   */
  private BundleSearch search;
  /**
   * Additional information about how this entry should be processed as part of a transaction or batch. For history, it shows how the entry was processed to create the version contained in the entry.
   * <p>Rule entry.request mandatory for batch/transaction/history, otherwise prohibited</p>
   * <p>Cardinality: 0..1</p>
   */
  private BundleRequest request;
  /**
   * Indicates the results of processing the corresponding 'request' entry in the batch or transaction being responded to or what the results of an operation where when returning history.
   * <p>Rule:entry.response mandatory for batch-response/transaction-response/history, otherwise prohibited</p>
   * <p>Cardinality: 0..1</p>
   */
  private BundleResponse response;

  public Object getResource() {
    return resource;
  }

  public BundleEntry setResource(Object resource) {
    this.resource = resource;
    return this;
  }

  public BundleSearch getSearch() {
    return search;
  }

  public void setSearch(BundleSearch search) {
    this.search = search;
  }

  public BundleRequest getRequest() {
    return request;
  }

  public void setRequest(BundleRequest request) {
    this.request = request;
  }

  public BundleResponse getResponse() {
    return response;
  }

  public void setResponse(BundleResponse response) {
    this.response = response;
  }

  public List<BundleLink> getLink() {
    return link;
  }

  public void setLink(List<BundleLink> link) {
    this.link = link;
  }

  public String getFullUrl() {
    return fullUrl;
  }

  public BundleEntry setFullUrl(String fullUrl) {
    this.fullUrl = fullUrl;
    return this;
  }

  public Extension get_fullUrl() {
    return _fullUrl;
  }

  public void set_fullUrl(Extension _fullUrl) {
    this._fullUrl = _fullUrl;
  }

}
