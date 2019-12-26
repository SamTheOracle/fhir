package model.domain;

import model.DomainResource;
import model.backboneelements.DocumentReferenceRelatesTo;
import model.datatypes.Identifier;
import model.elements.CodeableConcept;
import model.elements.Extension;
import model.elements.Reference;

import java.time.Instant;
import java.util.List;

/**
 * A DocumentReference resource is used to index a document, clinical note, and other binary objects to make them available to a
 * healthcare system. A document is some sequence of bytes that is identifiable, establishes its own context
 * (e.g., what subject, author, etc. can be displayed to the user), and has defined update management.
 * The DocumentReference resource can be used with any document format that has a recognized mime type and that conforms to this definition.
 */
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

}
