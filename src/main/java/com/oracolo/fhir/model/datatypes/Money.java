package com.oracolo.fhir.model.datatypes;

import com.oracolo.fhir.model.Element;
import com.oracolo.fhir.model.elements.Extension;

/**
 * An amount of economic utility in some recognized currency.
 */
public class Money extends Element {
  /**
   * Numerical value (with implicit precision).
   * <p>Cardinality 0..1</p>
   */
  private double value;
  /**
   * Extension for value
   * <p>Cardinality: 0..1</p>
   */
  private Extension _value;
  /**
   * ISO 4217 Currency Code.
   * <p>Cardinality: 0..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;
}
