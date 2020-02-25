package com.oracolo.fhir.model.datatypes;

import com.oracolo.fhir.model.Element;

/**
 * A relationship of two Quantity values - expressed as a numerator and a denominator.
 */
public class Ratio extends Element {
  /**
   * The value of the numerator.
   * <p>Cardinality: 0..1</p>
   */
  private Quantity numerator;
  /**
   * The value of the denumerator.
   * <p>Cardinality: 0..1</p>
   */
  private Quantity denumerator;

  public Quantity getNumerator() {
    return numerator;
  }

  public void setNumerator(Quantity numerator) {
    this.numerator = numerator;
  }

  public Quantity getDenumerator() {
    return denumerator;
  }

  public void setDenumerator(Quantity denumerator) {
    this.denumerator = denumerator;
  }
}
