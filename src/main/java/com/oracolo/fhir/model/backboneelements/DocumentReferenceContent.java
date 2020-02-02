package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Attachment;
import com.oracolo.fhir.model.datatypes.Coding;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentReferenceContent extends BackboneElement {

  /**
   * The document or URL of the document along with critical metadata to prove response has integrity.
   * <p>Cardinality: 1..1</p>
   */
  private Attachment attachment;

  /**
   * An identifier of the document encoding, structure, and template that the document conforms to beyond the base format indicated in the mimeType.
   * <p>Code preferred: https://www.hl7.org/fhir/valueset-formatcodes.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private Coding format;

  public Attachment getAttachment() {
    return attachment;
  }

  public DocumentReferenceContent setAttachment(Attachment attachment) {
    this.attachment = attachment;
    return this;
  }

  public Coding getFormat() {
    return format;
  }

  public void setFormat(Coding format) {
    this.format = format;
  }
}
