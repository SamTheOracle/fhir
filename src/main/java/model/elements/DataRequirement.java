package model.elements;

import model.Element;

import java.util.List;

/**
 * Describes a required data item for evaluation in terms of the type of data, and optional code or date-based filters of the data.
 */
public class DataRequirement extends Element {
  /**
   * The type of the required data, specified as the type name of a resource. For profiles, this value is set
   * to the type of the base resource of the profile.
   * <p>Cardinality: 1..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;
  /**
   * The profile of the required data, specified as the uri of the profile definition.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> profile;
  /**
   * Extension for profile
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _profile;

  /**
   * The intended subjects of the data requirement. If this element is not provided, a Patient subject is assumed.
   * <p>See http://hl7.org/fhir/valueset-subject-type.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept subjectCodeableConcept;

  /**
   * The intended subjects of the data requirement. If this element is not provided, a Patient subject is assumed.
   * <p>Cardinality: 0..1</p>
   */
  private Reference subjectReference;
  /**
   * Indicates that specific elements of the type are referenced by the knowledge module and must be supported by the consumer
   * in order to obtain an effective evaluation. This does not mean that a value is required for this element, only that the
   * consuming system must understand the element and be able to provide values for it if they are available.
   * <p>Cardinality: 0..*</p>
   */
  private String mustSupport;
  /**
   * Extension for mustSupport
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _mustSupport;

  private CodeFilterElement codeFilter;
  private DateFilterElement dateFilter;
  /**
   * Specifies a maximum number of results that are required (uses the _count search parameter).
   * <p>Must be positive</p>
   * <p>Cardinality: 0..1</p>
   */
  private int limit;

  /**
   * Extension for limit
   * <p>Cardinality: 0..1</p>
   */
  private Extension _limit;

  private SortElement sort;


}
