package model.elements;

import model.Element;

/**
 * A duration of time during which an organism (or a process) has existed.
 */
public class Age extends Element {
  /**
   * The value of the measured amount. The value includes an implicit precision in the presentation of the value.
   * <p>Cardinality: 0..1</p>
   */
  private double value;
  /**
   * How the value should be understood and represented - whether the actual value is greater or less than the stated value due
   * to measurement issues; e.g. if the comparator is \"\u003c\" , then the real value is \u003c stated value."
   * <p>Cardinality: 0..1</p>
   */
  private Enum comparator;
  /**
   * A human-readable form of the unit.
   * <p>Cardinality: 0..1</p>
   */
  private String unit;
  /**
   * The identification of the system that provides the coded form of the unit.
   * <p>Cardinality: 0..1</p>
   */
  private String system;
}
