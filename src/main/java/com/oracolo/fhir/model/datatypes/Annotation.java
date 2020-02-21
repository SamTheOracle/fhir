package com.oracolo.fhir.model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.elements.Extension;

import java.util.Date;

/**
 * A text note which also contains information about who made the statement and when.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Annotation {
  /**
   * The individual responsible for making the annotation.
   * <p>Cardinality: 0..1</p>
   */
  private Reference authorReference;
  /**
   * The individual responsible for making the annotation.
   * <p>Cardinality: 0..1</p>
   */
  private String authorString;
  /**
   * Extension for authorString
   * <p>Cardinality: 0..1</p>
   */
  private Extension _authorString;
  /**
   * The text of the annotation in markdown format.
   * <p>Cardinality: 0..1</p>
   */
  private String text;
  /**
   * Extension for text
   * <p>Cardinality: 0..1</p>
   */
  private Extension _text;
  /**
   * Indicates when this particular annotation was made.
   * <p>Cardinality: 0..1</p>
   */
  private Date time;
  /**
   * Extension for time
   * <p>Cardinality: 0..1</p>
   */
  private Extension _time;

  public Reference getAuthorReference() {
    return authorReference;
  }

  public void setAuthorReference(Reference authorReference) {
    this.authorReference = authorReference;
  }

  public String getAuthorString() {
    return authorString;
  }

  public void setAuthorString(String authorString) {
    this.authorString = authorString;
  }

  public Extension get_authorString() {
    return _authorString;
  }

  public void set_authorString(Extension _authorString) {
    this._authorString = _authorString;
  }

  public String getText() {
    return text;
  }

  public Annotation setText(String text) {
    this.text = text;
    return this;
  }

  public Extension get_text() {
    return _text;
  }

  public void set_text(Extension _text) {
    this._text = _text;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public Extension get_time() {
    return _time;
  }

  public void set_time(Extension _time) {
    this._time = _time;
  }
}
