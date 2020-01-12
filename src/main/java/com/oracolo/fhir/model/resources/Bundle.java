package com.oracolo.fhir.model.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.FhirResourceAbstract;
import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.backboneelements.BundleLink;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.elements.Extension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * One common operation performed with resources is to gather a collection of resources into a single
 * instance with containing context. In FHIR this is referred to as "bundling" the resources together.
 * These resource bundles are useful for a variety of different reasons, including: Returning a set of resources that meet some criteria as part of a server operation (see RESTful Search)
 * Returning a set of versions of resources as part of the history operation on a server (see History)
 * Sending a set of resources as part of a message exchange (see Messaging)
 * Grouping a self-contained set of resources to act as an exchangeable and persistable collection with clinical integrity - e.g. a clinical document (see Documents)
 * Creating/updating/deleting a set of resources on a server as a single operation (including doing so as a single atomic transaction) (see Transactions)
 * Storing a collection of resources
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bundle extends FhirResourceAbstract {


  public final String resourceType = "Bundle";
  /**
   * A persistent identifier for the bundle that won't change as a bundle is copied from server to server.
   * <p>Cardinality: 0..1</p>
   */
  private Identifier identifier;
  /**
   * Indicates the purpose of this bundle - how it is intended to be used.
   * <p>Code REQUIRED: document, message, transaction, transaction-response, batch, batch-response, history, searchset, collection</p>
   * <p>Cardinality: 1..1</p>
   */
  private String type;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _type;
  /**
   * The date/time that the bundle was assembled - i.e. when the resources were placed in the bundle.
   * <p>Cardinality: 0..1</p>
   */
  private Instant timestamp;
  /**
   * Extension for instant
   * <p>Cardinality: 0..1</p>
   */
  private Extension _instant;

  /**
   * If a set of search matches, this is the total number of entries of type 'match' across all pages in the search.
   * It does not include search.mode = 'include' or 'outcome' entries and it does not provide a count of the number of entries in the Bundle.
   * <p>Rule: total only when a search or history	total.empty() or (type = 'searchset') or (type = 'history')</p>
   * <p>Cardinality: 0..1</p>
   */
  private int total;
  /**
   * Extension for total
   * <p>Cardinality: 0..1</p>
   */
  private Extension _total;

  /**
   * A series of links that provide context to this bundle.
   * <p>Cardinality: 0..*</p>
   */
  private List<BundleLink> link;
  /**
   * An entry in a bundle resource - will either contain a resource or information about a resource (transactions and history only).
   * <p>Cardinality: 0..*</p>
   */
  private List<BundleEntry> entry;

  /**
   * Digital Signature - base64 encoded. XML-DSig or a JWT.
   * <p>Cardinality: 0..1</p>
   */
  private String signature;
  /**
   * Extension for signature
   * <p>Cardinality: 0..1</p>
   */
  private Extension _signature;

  public Identifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public String getType() {
    return type;
  }

  public Bundle setType(String type) {
    this.type = type;
    return this;
  }

  public Extension get_type() {
    return _type;
  }

  public void set_type(Extension _type) {
    this._type = _type;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Bundle setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public Extension get_instant() {
    return _instant;
  }

  public void set_instant(Extension _instant) {
    this._instant = _instant;
  }

  public int getTotal() {
    return total;
  }

  public Bundle setTotal(int total) {
    this.total = total;
    return this;
  }

  public Extension get_total() {
    return _total;
  }

  public void set_total(Extension _total) {
    this._total = _total;
  }

  public List<BundleLink> getLink() {
    return link;
  }

  public void setLink(List<BundleLink> link) {
    this.link = link;
  }

  public List<BundleEntry> getEntry() {
    return entry;
  }

  public void setEntry(List<BundleEntry> entry) {
    this.entry = entry;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public Extension get_signature() {
    return _signature;
  }

  public void set_signature(Extension _signature) {
    this._signature = _signature;
  }

  public Bundle addNewEntry(BundleEntry bundleEntry) {
    if (entry == null) {
      entry = new ArrayList<>();
    }
    entry.add(bundleEntry);
    return this;
  }

  public enum BundleTypeCodes {
    DOCUMENT("document", "Document"),
    MESSAGE("message", "Message"),
    TRANSACTION("transaction", "Transaction"),
    TRANSACTIONRESPONSE("transaction-response", "Transaction Response"),
    BATCH("batch", "Batch"),
    BATCHRESPONSE("batch-response", "Batch Response"),
    HISTORY("history", "History List"),
    SEARCHSET("searchset", "Search Results"),
    COLLECTION("collection", "Collection");
    private final String code;
    private final String display;

    BundleTypeCodes(String code, String display) {
      this.code = code;
      this.display = display;
    }

    public String display() {
      return display;
    }

    public String code() {
      return code;
    }
  }
}
