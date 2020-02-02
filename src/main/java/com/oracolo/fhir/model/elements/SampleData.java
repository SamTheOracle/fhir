package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;

/**
 * A series of measurements taken by a device, with upper and lower limits. There may be more than one dimension in the data.
 */
public class SampleData extends Element {
  /**
   * The base quantity that a measured value of zero represents. In addition, this provides the units
   * of the entire measurement series.
   * <p>Cardinality: 1..1</p>
   */
  private Quantity origin;
  /**
   * The length of time between sampling times, measured in milliseconds.
   * <p>Cardinality: 1..1</p>
   */
  private double period;
  /**
   * Extension for period
   * <p>Cardinality: 0..1</p>
   */
  private Extension _period;
  /**
   * The number of sample points at each time point. If this value is greater than one, then the dimensions will be interlaced -
   * all the sample points for a point in time will be recorded at once.
   * <p>Must be positive int</p>
   * <p>Cardinality: 1..1</p>
   */
  private int dimensions;
  /**
   * Extension for dimensions
   * <p>Cardinality: 0..1</p>
   */
  private Extension _dimensions;
  /**
   * A correction factor that is applied to the sampled data points before they are added to the origin.
   * <p>Cardinality: 0..1</p>
   */
  private double factor;
  /**
   * Extension for factor
   * <p>Cardinality: 0..1</p>
   */
  private Extension _factor;
  /**
   * The lower limit of detection of the measured points. This is needed if any of the data points
   * have the value "L" (lower than detection limit).
   * <p>Cardinality: 0..1</p>
   */
  private double lowerLimit;
  /**
   * Extension for lowerLimit
   * <p>Cardinality: 0..1</p>
   */
  private Extension _lowerLimit;
  /**
   * The upper limit of detection of the measured points. This is needed if any of the data points
   * have the value "U" (higher than detection limit).
   * <p>Cardinality: 0..1</p>
   */
  private double upperLimit;
  /**
   * Extension for upperLimit
   * <p>Cardinality: 0..1</p>
   */
  private Extension _upperLimit;
  /**
   * A series of data points which are decimal values separated by a single space (character u20). The special values
   * "E" (error), "L" (below detection limit) and "U" (above detection limit) can also be used in place of a decimal value.
   * <p>Cardinality: 0..1</p>
   */
  private String data;
  /**
   * Extension for data
   * <p>Cardinality: 0..1</p>
   */
  private String _data;

  public Quantity getOrigin() {
    return origin;
  }

  public void setOrigin(Quantity origin) {
    this.origin = origin;
  }

  public double getPeriod() {
    return period;
  }

  public void setPeriod(double period) {
    this.period = period;
  }

  public Extension get_period() {
    return _period;
  }

  public void set_period(Extension _period) {
    this._period = _period;
  }

  public int getDimensions() {
    return dimensions;
  }

  public void setDimensions(int dimensions) {
    this.dimensions = dimensions;
  }

  public Extension get_dimensions() {
    return _dimensions;
  }

  public void set_dimensions(Extension _dimensions) {
    this._dimensions = _dimensions;
  }

  public double getFactor() {
    return factor;
  }

  public void setFactor(double factor) {
    this.factor = factor;
  }

  public Extension get_factor() {
    return _factor;
  }

  public void set_factor(Extension _factor) {
    this._factor = _factor;
  }

  public double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Extension get_lowerLimit() {
    return _lowerLimit;
  }

  public void set_lowerLimit(Extension _lowerLimit) {
    this._lowerLimit = _lowerLimit;
  }

  public double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(double upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Extension get_upperLimit() {
    return _upperLimit;
  }

  public void set_upperLimit(Extension _upperLimit) {
    this._upperLimit = _upperLimit;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String get_data() {
    return _data;
  }

  public void set_data(String _data) {
    this._data = _data;
  }
}
