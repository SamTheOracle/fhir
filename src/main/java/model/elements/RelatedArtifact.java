package model.elements;

import model.Element;
import model.datatypes.Attachment;

/**
 * Related artifacts such as additional documentation, justification, or bibliographic references.
 */
public class RelatedArtifact extends Element {
  /**
   * The type of relationship to the related artifact.
   * <p>See http://hl7.org/fhir/valueset-related-artifact-type.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String type;
  /**
   * Extension for type;
   * <p>Cardinality: 0..1</p>
   */
  private Extension _type;
  /**
   * A brief description of the document or knowledge resource being referenced, suitable for display to a consumer.
   * <p>Cardinality: 0..1</p>
   */
  private String label;
  /**
   * Extension for label
   * <p>Cardinality: 0..1</p>
   */
  private Extension _label;
  /**
   * A brief description of the document or knowledge resource being referenced, suitable for display to a consumer.
   * <p>Cardinality: 0..1</p>
   */
  private String display;
  /**
   * Extension for display
   * <p>Cardinality: 0..1</p>
   */
  private Extension _display;
  /**
   * A bibliographic citation for the related artifact. This text SHOULD be formatted according to an accepted citation format.
   * <p>Cardinality: 0..1</p>
   */
  private String citation;
  /**
   * Extension for citation
   * <p>Cardinality: 0..1</p>
   */
  private Extension _citation;
  /**
   * The document being referenced, represented as an attachment. This is exclusive with the resource element.
   * <p>Cardinality: 0..1</p>
   */
  private Attachment document;
  /**
   * The related resource, such as a library, value set, profile, or other knowledge resource.
   * <p>Cardinality: 0..1</p>
   */
  private String canonical;
  /**
   * Extension for canonical
   * <p>Cardinality: 0..1</p>
   */
  private Extension _canonical;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Extension get_type() {
    return _type;
  }

  public void set_type(Extension _type) {
    this._type = _type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Extension get_label() {
    return _label;
  }

  public void set_label(Extension _label) {
    this._label = _label;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public Extension get_display() {
    return _display;
  }

  public void set_display(Extension _display) {
    this._display = _display;
  }

  public String getCitation() {
    return citation;
  }

  public void setCitation(String citation) {
    this.citation = citation;
  }

  public Extension get_citation() {
    return _citation;
  }

  public void set_citation(Extension _citation) {
    this._citation = _citation;
  }

  public Attachment getDocument() {
    return document;
  }

  public void setDocument(Attachment document) {
    this.document = document;
  }

  public String getCanonical() {
    return canonical;
  }

  public void setCanonical(String canonical) {
    this.canonical = canonical;
  }

  public Extension get_canonical() {
    return _canonical;
  }

  public void set_canonical(Extension _canonical) {
    this._canonical = _canonical;
  }
}
