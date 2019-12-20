package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.elements.Extension;
import model.elements.Narrative;

import java.util.List;

/**
 * A resource that includes narrative, extensions, and contained resources.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DomainResource extends Resource {

  /**
   * A human-readable narrative that contains a summary of the resource and can be used to represent the content of the resource
   * to a human. The narrative need not encode all the structured data, but is required to contain sufficient detail to make
   * it "clinically safe" for a human to just read the narrative.
   * Resource definitions may define what content should be represented in the narrative to ensure clinical safety.
   * <p>Cardinality:  0..1</p>
   */
  protected Narrative text;
  /**
   * In some circumstances, the content referred to in the resource reference does not have an independent existence apart from the
   * resource that contains it - it cannot be identified independently, and nor can it have its own independent transaction scope.
   * Typically, such circumstances arise where resources are being assembled by a secondary user of the source data,
   * such as a middleware engine.
   * These are anonymous resources that don't have an existence outside the transaction scope
   * <p>Cardinality: 0..*</p>
   */
  protected List<DomainResource> contained;
  /**
   * May be used to represent additional information that is not part of the basic definition of the resource.
   * To make the use of extensions safe and manageable, there is a strict set of governance applied to the definition and use
   * of extensions.
   * Though any implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition
   * of the extension.
   * <p>Cardinality: 0..*</p>
   */
  protected List<Extension> extension;
  /**
   * May be used to represent additional information that is not part of the basic definition of the resource and that modifies
   * the understanding of the element that contains it and/or the understanding of the containing element's descendants.
   * Usually modifier elements provide negation or qualification. To make the use of extensions safe and manageable, there is
   * a strict set of governance applied to the definition and use of extensions.
   * Though any implementer is allowed to define an extension, there is a set of requirements that SHALL be met as part of
   * the definition of the extension. Applications processing a resource are required to check for modifier extensions.
   * <p>Cardinality: 0..*</p>
   */
  protected List<Extension> modifierExtension;


  public DomainResource() {
  }


  public Narrative getText() {
    return text;
  }

  public void setText(Narrative text) {
    this.text = text;
  }

  public List<DomainResource> getContained() {
    return contained;
  }

  public void setContained(List<DomainResource> contained) {
    this.contained = contained;
  }

  public List<Extension> getExtension() {
    return extension;
  }

  public void setExtension(List<Extension> extension) {
    this.extension = extension;
  }

  public List<Extension> getModifierExtension() {
    return modifierExtension;
  }

  public void setModifierExtension(List<Extension> modifierExtension) {
    this.modifierExtension = modifierExtension;
  }


}
