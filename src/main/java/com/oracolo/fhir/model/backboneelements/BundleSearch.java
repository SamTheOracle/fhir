package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.elements.Extension;

/**
 * Information about the search process that lead to the creation of this entry.
 * RULE: entry.search only when a search	entry.search.empty() or (type = 'searchset')
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BundleSearch extends BackboneElement {
  /**
   * Why this entry is in the result set - whether it's included as a match or because of an _include requirement,
   * or to convey information or warning information about the search process.
   * <p>Required: https://www.hl7.org/fhir/valueset-search-entry-mode.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String mode;
  /**
   * Extension for mode
   * <p>Cardinality: 0..1</p>
   */
  private Extension _mode;
  /**
   * When searching, the server's search ranking score for the entry.
   * <p>Cardinality: 0..1</p>
   */
  private double decimal;
  /**
   * Extension for decimal
   * <p>Cardinality: 0..1</p>
   */
  private Extension _decimal;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public Extension get_mode() {
    return _mode;
  }

  public void set_mode(Extension _mode) {
    this._mode = _mode;
  }

  public double getDecimal() {
    return decimal;
  }

  public void setDecimal(double decimal) {
    this.decimal = decimal;
  }

  public Extension get_decimal() {
    return _decimal;
  }

  public void set_decimal(Extension _decimal) {
    this._decimal = _decimal;
  }

}
