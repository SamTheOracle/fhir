package model.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.Element;
import model.datatypes.Identifier;

/**
 * References SHALL be a reference to an actual FHIR resource, and SHALL be resolveable (allowing for access control,
 * temporary unavailability, etc.). Resolution can be either by retrieval from the URL, or, where applicable by resource type,
 * by treating an absolute reference as a canonical URL and looking it up in a local registry/repository.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reference extends Element {
  /**
   * A reference to a location at which the other resource is found. The reference may be a relative reference,
   * in which case it is relative to the patients.service base URL, or an absolute URL that resolves to the location where the resource is found.
   * The reference may be version specific or not. If the reference is not to a FHIR RESTful patients.server,
   * then it should be assumed to be version specific.
   * Internal fragment references (start with '#') refer to contained resources.
   * <p>Cardinality: 0..1</p>
   */
  private String reference;
  /**
   * The expected type of the target of the reference. If both Reference.type and Reference.reference are populated and Reference.reference
   * is a FHIR URL, both SHALL be consistent.
   * The type is the Canonical URL of Resource Definition that is the type this reference refers to.
   * References are URLs that are relative to http://hl7.org/fhir/StructureDefinition/ e.g. "Patient" is a reference to
   * http://hl7.org/fhir/StructureDefinition/Patient.
   * Absolute URLs are only allowed for logical models (and can only be used in references in logical models, not resources).
   *    * <p>Code is required but it is extensible if it does not cover the concept</p>
   * <p>Cardinality:  0..1</p>
   */
  private String type;
  /**
   * An identifier for the target resource. This is used when there is no way to reference the other resource directly, either because
   * the entity it represents is not available through a FHIR patients.server, or because there is no way for the author of the resource
   * to convert a known identifier to an actual location. There is no requirement that a Reference.identifier point to something
   * that is actually exposed as a FHIR instance, but it SHALL point to a business concept that would be expected to be exposed as a
   * FHIR instance, and that instance would need to be of a FHIR resource type allowed by the reference.
   * <p>Cardinality:  0..1</p>
   */
  private Identifier identifier;
  /**
   * Text to display
   * <p>Cardinality: 0..1</p>
   */
  private String display;

  public String getReference() {
    return reference;
  }

  public Reference setReference(String reference) {
    this.reference = reference;
    return this;
  }

  public String getType() {
    return type;
  }

  public Reference setType(String type) {
    this.type = type;
    return this;
  }

  public Identifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public String getDisplay() {
    return display;
  }

  public Reference setDisplay(String display) {
    this.display = display;
    return this;
  }
}
