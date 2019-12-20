package model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.elements.Extension;

/**
 * A Period specifies a range of time; the context of use will specify whether the entire range applies
 * (e.g. "the patient was an inpatient of the hospital for this time range") or one value from the range applies (e.g. "give
 * to the patient between these two times").
 * Period is not used for a duration (a measure of elapsed time)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Period {
  /**
   * If the low element is missing, the meaning is that the low boundary is not known. And viceversa
   * <p>Cardinality: 0..1</p>
   */
  private String start;
  private String end;
  /**
   * Extension for start,end
   * <p>Cardinality: 0..1</p>
   */
  private Extension _start, _end;

  public String getStart() {
    return start;
  }

  public Period setStart(String start) {
    this.start = start;
    return this;
  }

  public String getEnd() {
    return end;
  }

  public Period setEnd(String end) {
    this.end = end;
    return this;
  }

  public Extension get_start() {
    return _start;
  }

  public void set_start(Extension _start) {
    this._start = _start;
  }

  public Extension get_end() {
    return _end;
  }

  public void set_end(Extension _end) {
    this._end = _end;
  }
}
