package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.backboneelements.DocumentReferenceContent;
import com.oracolo.fhir.model.backboneelements.DocumentReferenceContext;
import com.oracolo.fhir.model.backboneelements.DocumentReferenceRelatesTo;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DocumentReference resource is used to index a document, clinical note, and other binary objects to make them available to a
 * healthcare system. A document is some sequence of bytes that is identifiable, establishes its own context
 * (e.g., what subject, author, etc. can be displayed to the user), and has defined update management.
 * The DocumentReference resource can be used with any document format that has a recognized mime type and that conforms to this definition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentReference extends DomainResource {
  /**
   * Document identifier as assigned by the source of the document. This identifier is specific to this version of the document.
   * This unique identifier may be used elsewhere to identify this version of the document.
   * <p>Cardinality: 0..1</p>
   */
  private Identifier masterIdentifier;
  /**
   * Other identifiers associated with the document, including version independent identifiers.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * Other identifiers associated with the document, including version independent identifiers.
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * The status of the underlying document.
   * <p>Code required: http://hl7.org/fhir/composition-status</p>
   * <p>Cardinality: 0..1</p>
   */
  private String codeStatus;
  /**
   * Extension for codeStatus
   * <p>Cardinality: 0..1</p>
   */
  private Extension _codeStatus;
  /**
   * Specifies the particular kind of document referenced (e.g. History and Physical, Discharge Summary, Progress Note).
   * This usually equates to the purpose of making the document referenced.
   * <p>Code preferred: http://hl7.org/fhir/valueset-c80-doc-typecodes.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept type;
  /**
   * A categorization for the type of document referenced - helps for indexing and searching. This may be implied by or derived from the code specified in the DocumentReference.type.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> category;
  /**
   * Who or what the document is about. The document can be about a person, (patient or healthcare practitioner),
   * a device (e.g. a machine) or even a group of subjects (such as a document about a herd of farm animals,
   * or a set of patients that share a common exposure).
   * <p>Cardinality: 0..1</p>
   */
  private Reference subject;
  /**
   * When the document reference was created.
   * <p>Cardinality: 0..1</p>
   */
  private Instant date;
  /**
   * Extension for date
   * <p>Cardinality: 0..1</p>
   */
  private Extension _date;

  /**
   * Identifies who is responsible for adding the information to the document.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> author;
  /**
   * Which person or organization authenticates that this document is valid.
   * <p>Cardinality: 0..1</p>
   */
  private Reference authenticator;
  /**
   * Identifies the organization or group who is responsible for ongoing maintenance of and access to the document.
   * <p>Cardinality: 0..1</p>
   */
  private Reference custodian;
  /**
   * Relationships that this document has with other document references that already exist.
   * Cardinality: 0..*
   */
  private List<DocumentReferenceRelatesTo> relatesTo;

  /**
   * Human-readable description of the source document.
   * <p>Cardinality: 0..1</p>
   */
  private String description;
  /**
   * Extension for description
   * <p>Cardinality: 0..1</p>
   */
  private Extension _description;

  /**
   * A set of Security-Tag codes specifying the level of privacy/security of the Document. Note that DocumentReference.meta.security contains
   * the security labels of the "reference" to the document, while DocumentReference.securityLabel contains a snapshot of the security labels on the document the reference refers to.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> securityLabel;

  /**
   * <p>Cardinality: 1..*</p>
   */
  private List<DocumentReferenceContent> content;

  /**
   * The clinical context in which the document was prepared.
   * <p>Cardinality: 0..1</p>
   */
  private DocumentReferenceContext context;

  public Identifier getMasterIdentifier() {
    return masterIdentifier;
  }

  public void setMasterIdentifier(Identifier masterIdentifier) {
    this.masterIdentifier = masterIdentifier;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
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

  public String getCodeStatus() {
    return codeStatus;
  }

  public void setCodeStatus(String codeStatus) {
    this.codeStatus = codeStatus;
  }

  public Extension get_codeStatus() {
    return _codeStatus;
  }

  public void set_codeStatus(Extension _codeStatus) {
    this._codeStatus = _codeStatus;
  }

  public CodeableConcept getType() {
    return type;
  }

  public void setType(CodeableConcept type) {
    this.type = type;
  }

  public List<CodeableConcept> getCategory() {
    return category;
  }

  public void setCategory(List<CodeableConcept> category) {
    this.category = category;
  }

  public Reference getSubject() {
    return subject;
  }

  public void setSubject(Reference subject) {
    this.subject = subject;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }

  public Extension get_date() {
    return _date;
  }

  public void set_date(Extension _date) {
    this._date = _date;
  }

  public List<Reference> getAuthor() {
    return author;
  }

  public void setAuthor(List<Reference> author) {
    this.author = author;
  }

  public Reference getAuthenticator() {
    return authenticator;
  }

  public void setAuthenticator(Reference authenticator) {
    this.authenticator = authenticator;
  }

  public Reference getCustodian() {
    return custodian;
  }

  public void setCustodian(Reference custodian) {
    this.custodian = custodian;
  }

  public List<DocumentReferenceRelatesTo> getRelatesTo() {
    return relatesTo;
  }

  public void setRelatesTo(List<DocumentReferenceRelatesTo> relatesTo) {
    this.relatesTo = relatesTo;
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

  public List<CodeableConcept> getSecurityLabel() {
    return securityLabel;
  }

  public void setSecurityLabel(List<CodeableConcept> securityLabel) {
    this.securityLabel = securityLabel;
  }

  public List<DocumentReferenceContent> getContent() {
    return content;
  }

  public void setContent(List<DocumentReferenceContent> content) {
    this.content = content;
  }

  public DocumentReferenceContext getContext() {
    return context;
  }

  public void setContext(DocumentReferenceContext context) {
    this.context = context;
  }

  public DocumentReference addNewDocumentContent(DocumentReferenceContent documentReferenceContent) {
    if (content == null) {
      content = new ArrayList<>();
    }
    content.add(documentReferenceContent);
    return this;
  }

  @Override
  public DocumentReference setId(String id) {
    this.id = id;
    return this;
  }
}
