package com.oracolo.fhir.model.datatypes;

import com.oracolo.fhir.model.Element;
import com.oracolo.fhir.model.elements.Extension;

import java.util.Date;

/**
 * Date filters specify additional constraints on the data in terms of the applicable date range
 * for specific elements. Each date filter specifies an additional constraint on the data, i.e. date filters are AND'ed, not OR'ed.
 */
public class DateFilterElement extends Element {
  /**
   * The date-valued attribute of the filter. The specified path SHALL be a FHIRPath resolveable on the
   * specified type of the DataRequirement, and SHALL consist only of identifiers, constant indexers, and .resolve().
   * The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x])
   * to traverse multiple-cardinality sub-elements (see the http://hl7.org/fhir/fhirpath.html#simple for full details).
   * Note that the index must be an integer constant. The path must resolve to an element of type date, dateTime, Period,
   * Schedule, or Timing.
   * <p>Cardinality: 0..1</p>
   */
  private String path;
  /**
   * Extension for path
   * <p>Cardinality: 0..1</p>
   */
  private Extension _path;
  /**
   * A date parameter that refers to a search parameter defined on the specified type of the DataRequirement,
   * and which searches on elements of type date, dateTime, Period, Schedule, or Timing.
   * <p>Cardinality: 0..1</p>
   */
  private String searchParam;
  /**
   * Extension for searchParam
   * <p>Cardinality: 0..1</p>
   */
  private Extension _searchParam;
  /**
   * The value of the filter. If period is specified, the filter will return only those data items that fall
   * within the bounds determined by the Period, inclusive of the period boundaries. If dateTime is specified, the filter will
   * return only those data items that are equal to the specified dateTime. If a Duration is specified, the filter
   * will return only those data items that fall within Duration before now.
   * <p>Cardinality: 0..1</p>
   */
  private Date valueDateTime;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueDateTime;
  /**
   * The value of the filter. If period is specified, the filter will return only those data items that fall
   * within the bounds determined by the Period, inclusive of the period boundaries. If dateTime is specified, the filter will
   * return only those data items that are equal to the specified dateTime. If a Duration is specified, the filter
   * will return only those data items that fall within Duration before now.
   * <p>Cardinality: 0..1</p>
   */
  private Period valuePeriod;
  /**
   * The value of the filter. If period is specified, the filter will return only those data items that fall
   * within the bounds determined by the Period, inclusive of the period boundaries. If dateTime is specified, the filter will
   * return only those data items that are equal to the specified dateTime. If a Duration is specified, the filter
   * will return only those data items that fall within Duration before now.
   * <p>Cardinality: 0..1</p>
   */
  private Duration valueDuration;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Extension get_path() {
    return _path;
  }

  public void set_path(Extension _path) {
    this._path = _path;
  }

  public String getSearchParam() {
    return searchParam;
  }

  public void setSearchParam(String searchParam) {
    this.searchParam = searchParam;
  }

  public Extension get_searchParam() {
    return _searchParam;
  }

  public void set_searchParam(Extension _searchParam) {
    this._searchParam = _searchParam;
  }

  public Date getValueDateTime() {
    return valueDateTime;
  }

  public void setValueDateTime(Date valueDateTime) {
    this.valueDateTime = valueDateTime;
  }

  public Extension get_valueDateTime() {
    return _valueDateTime;
  }

  public void set_valueDateTime(Extension _valueDateTime) {
    this._valueDateTime = _valueDateTime;
  }

  public Period getValuePeriod() {
    return valuePeriod;
  }

  public void setValuePeriod(Period valuePeriod) {
    this.valuePeriod = valuePeriod;
  }

  public Duration getValueDuration() {
    return valueDuration;
  }

  public void setValueDuration(Duration valueDuration) {
    this.valueDuration = valueDuration;
  }
}
