package model.elements;

import model.Element;

/**
 * A set of ordered Quantity values defined by a low and high limit.
 * <p>
 * A Range specifies a set of possible values; usually, one value from the range applies (e.g.
 * "give the patient between 2 and 4 tablets"). Ranges are typically used in instructions.
 */
public class Range extends Element {
  /**
   * The low limit. The boundary is inclusive.
   * If the low element is missing, the low boundary is not known.
   * <p>Cardinality: 0..1</p>
   */
  private Quantity low;
  /**
   * The high limit. The boundary is inclusive.
   * If the high element is missing, the high boundary is not known.
   * <p>Cardinality: 0..1</p>
   */
  private Quantity high;

  public Quantity getLow() {
    return low;
  }

  public void setLow(Quantity low) {
    this.low = low;
  }

  public Quantity getHigh() {
    return high;
  }

  public void setHigh(Quantity high) {
    this.high = high;
  }
}
