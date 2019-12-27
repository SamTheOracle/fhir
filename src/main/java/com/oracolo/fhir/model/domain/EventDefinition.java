package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.*;

import java.util.Date;
import java.util.List;

/**
 * The EventDefinition provides a reusable description of an event. The resource supports describing different kinds of events,
 * including named events, periodic events, and data-based events. For each of these, the resource also supports a
 * formal description of the event. For example, a 'monitor-emergency-admissions' event can be a named event,
 * but also provide a formal description of the event as monitoring for encounters that occur in emergency department locations.
 */
public abstract class EventDefinition extends DomainResource {

  /**
   * An absolute URI that is used to identify this event definition when it is referenced in a specification, model,
   * design or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal
   * address at which at which an authoritative instance of this event definition is (or will be) published. This URL can
   * be the target of a canonical reference. It SHALL remain the same when the event definition is stored on different servers.
   */
  protected String url;

  /**
   * Extension for url
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _url;
  /**
   * A formal identifier that is used to identify this event definition when it is represented in other formats,
   * or referenced in a specification, model, design or an instance.
   * <p>Cardinality: 0..*</p>
   */
  protected List<Identifier> identifier;
  /**
   * The identifier that is used to identify this version of the event definition when it is referenced in a specification,
   * model, design or instance. This is an arbitrary value managed by the event definition author and is not expected to
   * be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available.
   * There is also no expectation that versions can be placed in a lexicographical sequence.
   * <p>Cardinality: 0..1</p>
   */
  protected String version;
  /**
   * Extension for version
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _version;
  /**
   * A natural language name identifying the event definition. This name should be usable as an identifier for the module
   * by machine processing applications such as code generation.
   */
  protected String name;
  /**
   * Extension for name
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _name;
  /**
   * A short, descriptive, user-friendly title for the event definition.
   * <p>Cardinality: 0..1</p>
   */
  protected String title;
  /**
   * Extension for title
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _title;
  /**
   * An explanatory or alternate title for the event definition giving additional information about its content.
   * <p>Cardinality: 0..1</p>
   */
  protected String subtitle;
  /**
   * Extension for subtitle
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _subtitle;
  /**
   * The status of this event definition. Enables tracking the life-cycle of the content.
   * <p>See status codes: http://hl7.org/fhir/valueset-publication-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  protected String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _status;
  /**
   * A Boolean value to indicate that this event definition is authored for testing purposes
   * (or education/evaluation/marketing) and is not intended to be used for genuine usage.
   * <p>Cardinality: 0..1</p>
   */
  protected boolean experimental;
  /**
   * Extension for experimental
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _experimental;
  /**
   * A code definition that describes the intended subject of the event definition.
   * <p>Cardinality: 0..1</p>
   */
  protected CodeableConcept subjectCodeableConcept;
  /**
   * A reference definition that describes the intended subject of the event definition.
   * <p>Cardinality: 0..1</p>
   */
  protected Reference subjectReference;
  /**
   * The date (and optionally time) when the event definition was published.
   * The date must change when the business version changes and it must change if the status code changes.
   * In addition, it should change when the substantive content of the event definition changes.
   * <p>Cardinality: 0..1</p>
   */
  protected Date dateTime;
  /**
   * Extension for dateTime
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _dateTime;
  /**
   * The name of the organization or individual that published the event definition.
   * <p>Cardinality: 0..1</p>
   */
  protected String publisher;
  /**
   * Contact details to assist a user in finding and communicating with the publisher.
   * <p>Cardinality: 0..*</p>
   */
  protected List<ContactDetail> contact;
  /**
   * A free text natural language description of the event definition from a consumer's perspective.
   * <p>Cardinality: 0..1</p>
   */
  protected String description;
  /**
   * Extension for description
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _description;
  /**
   * The content was developed with a focus and intent of supporting the contexts that are listed.
   * These contexts may be general categories (gender, age, ...) or may be references to specific programs (insurance plans,
   * studies, ...) and may be used to assist with indexing and searching for appropriate event definition instances.
   * <p>Cardinality: 0..*</p>
   */
  protected List<UsageContext> useContext;
  /**
   * A legal or geographic region in which the event definition is intended to be used.
   * <p>See http://hl7.org/fhir/valueset-jurisdiction.html</p>
   * <p>Cardinality: 0..*</p>
   */
  protected List<CodeableConcept> jurisdiction;
  /**
   * Explanation of why this event definition is needed and why it has been designed as it has.
   * <p>Cardinality. 0..1</p>
   */
  protected String purpose;
  /**
   * Extension for purpose
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _purpose;
  /**
   * A detailed description of how the event definition is used from a clinical perspective.
   * <p>Cardinality: 0..1</p>
   */
  protected String usege;
  /**
   * Extension for usage
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _usage;
  /**
   * A copyright statement relating to the event definition and/or its contents.
   * Copyright statements are generally legal restrictions on the use and publishing of the event definition.
   * <p>Cardinality: 0..1</p>
   */
  protected String copyrigth;
  /**
   * Extension for copyright
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _copyright;
  /**
   * The date on which the resource content was approved by the publisher.
   * Approval happens once when the content is officially approved for usage.
   * <p>Cardinality: 0..1</p>
   */
  protected Date approvalDate;
  /**
   * Extension for approvalDate
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _approvalDate;
  /**
   * The date on which the resource content was last reviewed. Review happens periodically
   * after approval but does not change the original approval date.
   * <p>Cardinality: 0..1</p>
   */
  protected Date lastReviewDate;
  /**
   * Extension for lastReviewDate
   * <p>Cardinality: 0..1</p>
   */
  protected Extension _lastReviewDate;
  /**
   * The period during which the event definition content was or is planned to be in active use.
   * <p>Cardinality: 0..1</p>
   */
  protected Period effectivePeriod;
  /**
   * Descriptive topics related to the module. Topics provide a high-level categorization
   * of the module that can be useful for filtering and searching.
   * <p>See http://hl7.org/fhir/valueset-definition-topic.html</p>
   * <p>Cardinality: 0..*</p>
   */
  protected List<CodeableConcept> topic;
  /**
   * An individiual or organization primarily involved in the creation and maintenance of the content.
   * <p>Cardinality: 0..*</p>
   */
  protected List<ContactDetail> author;
  /**
   * An individual or organization primarily responsible for internal coherence of the content.
   * <p>Cardinality: 0..*</p>
   */
  protected List<ContactDetail> editor;
  /**
   * An individual or organization primarily responsible for review of some aspect of the content.
   * <p>Cardinality: 0..*</p>
   */
  protected List<ContactDetail> reviewer;
  /**
   * An individual or organization primarily responsible for review of some aspect of the content.
   * <p>Cardinality: 0..*</p>
   */
  protected List<ContactDetail> endorser;
  /**
   * Related resources such as additional documentation, justification, or bibliographic references.
   * <p>>Cardinality: 0..*</p>
   */
  protected List<RelatedArtifact> relatedArtificat;
  /**
   * The trigger element defines when the event occurs. If more than one trigger condition is
   * specified, the event fires whenever any one of the trigger conditions is met.
   * <p>Cardinality: 1..*</p>
   */
  protected List<TriggerDefinition> trigger;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Extension get_url() {
    return _url;
  }

  public void set_url(Extension _url) {
    this._url = _url;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Extension get_version() {
    return _version;
  }

  public void set_version(Extension _version) {
    this._version = _version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Extension get_name() {
    return _name;
  }

  public void set_name(Extension _name) {
    this._name = _name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Extension get_title() {
    return _title;
  }

  public void set_title(Extension _title) {
    this._title = _title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public Extension get_subtitle() {
    return _subtitle;
  }

  public void set_subtitle(Extension _subtitle) {
    this._subtitle = _subtitle;
  }

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

  public boolean isExperimental() {
    return experimental;
  }

  public void setExperimental(boolean experimental) {
    this.experimental = experimental;
  }

  public Extension get_experimental() {
    return _experimental;
  }

  public void set_experimental(Extension _experimental) {
    this._experimental = _experimental;
  }

  public CodeableConcept getSubjectCodeableConcept() {
    return subjectCodeableConcept;
  }

  public void setSubjectCodeableConcept(CodeableConcept subjectCodeableConcept) {
    this.subjectCodeableConcept = subjectCodeableConcept;
  }

  public Reference getSubjectReference() {
    return subjectReference;
  }

  public void setSubjectReference(Reference subjectReference) {
    this.subjectReference = subjectReference;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Extension get_dateTime() {
    return _dateTime;
  }

  public void set_dateTime(Extension _dateTime) {
    this._dateTime = _dateTime;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public List<ContactDetail> getContact() {
    return contact;
  }

  public void setContact(List<ContactDetail> contact) {
    this.contact = contact;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Extension get_description() {
    return _description;
  }

  public void set_description(Extension _description) {
    this._description = _description;
  }

  public List<UsageContext> getUseContext() {
    return useContext;
  }

  public void setUseContext(List<UsageContext> useContext) {
    this.useContext = useContext;
  }

  public List<CodeableConcept> getJurisdiction() {
    return jurisdiction;
  }

  public void setJurisdiction(List<CodeableConcept> jurisdiction) {
    this.jurisdiction = jurisdiction;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public Extension get_purpose() {
    return _purpose;
  }

  public void set_purpose(Extension _purpose) {
    this._purpose = _purpose;
  }

  public String getUsege() {
    return usege;
  }

  public void setUsege(String usege) {
    this.usege = usege;
  }

  public Extension get_usage() {
    return _usage;
  }

  public void set_usage(Extension _usage) {
    this._usage = _usage;
  }

  public String getCopyrigth() {
    return copyrigth;
  }

  public void setCopyrigth(String copyrigth) {
    this.copyrigth = copyrigth;
  }

  public Extension get_copyright() {
    return _copyright;
  }

  public void set_copyright(Extension _copyright) {
    this._copyright = _copyright;
  }

  public Date getApprovalDate() {
    return approvalDate;
  }

  public void setApprovalDate(Date approvalDate) {
    this.approvalDate = approvalDate;
  }

  public Extension get_approvalDate() {
    return _approvalDate;
  }

  public void set_approvalDate(Extension _approvalDate) {
    this._approvalDate = _approvalDate;
  }

  public Date getLastReviewDate() {
    return lastReviewDate;
  }

  public void setLastReviewDate(Date lastReviewDate) {
    this.lastReviewDate = lastReviewDate;
  }

  public Extension get_lastReviewDate() {
    return _lastReviewDate;
  }

  public void set_lastReviewDate(Extension _lastReviewDate) {
    this._lastReviewDate = _lastReviewDate;
  }

  public Period getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(Period effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
  }

  public List<CodeableConcept> getTopic() {
    return topic;
  }

  public void setTopic(List<CodeableConcept> topic) {
    this.topic = topic;
  }

  public List<ContactDetail> getAuthor() {
    return author;
  }

  public void setAuthor(List<ContactDetail> author) {
    this.author = author;
  }

  public List<ContactDetail> getEditor() {
    return editor;
  }

  public void setEditor(List<ContactDetail> editor) {
    this.editor = editor;
  }

  public List<ContactDetail> getReviewer() {
    return reviewer;
  }

  public void setReviewer(List<ContactDetail> reviewer) {
    this.reviewer = reviewer;
  }

  public List<ContactDetail> getEndorser() {
    return endorser;
  }

  public void setEndorser(List<ContactDetail> endorser) {
    this.endorser = endorser;
  }

  public List<RelatedArtifact> getRelatedArtificat() {
    return relatedArtificat;
  }

  public void setRelatedArtificat(List<RelatedArtifact> relatedArtificat) {
    this.relatedArtificat = relatedArtificat;
  }

  public List<TriggerDefinition> getTrigger() {
    return trigger;
  }

  public void setTrigger(List<TriggerDefinition> trigger) {
    this.trigger = trigger;
  }
}
