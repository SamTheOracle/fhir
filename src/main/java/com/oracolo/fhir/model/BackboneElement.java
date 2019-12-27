package com.oracolo.fhir.model;

import com.oracolo.fhir.model.elements.Extension;

import java.util.List;

/**
 * The base definition for complex elements defined as part of a resource definition - that is,
 * elements that have children that are defined in the resource.
 */
public abstract class BackboneElement extends Element {
  /**
   * May be used to represent additional information that is not part of the basic definition of the element and that
   * modifies the understanding of the element in which it is contained and/or the understanding of the containing element's descendants.
   * Usually modifier elements provide negation or qualification. To make the use of extensions safe and manageable, there is a
   * strict set of governance applied to the definition and use of extensions.
   * Though any implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition
   * of the extension. Applications processing a resource are required to check for modifier extensions.
   * <p>
   * Modifier extensions SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of modifierExtension itself).
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> modifierExtension;
}
