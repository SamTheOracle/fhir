package com.oracolo.fhir.model.datatypes;

import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;

import java.util.List;

/**
 * Custom object representing communication to deal with patient
 */
public class Communication {
  /**
   * The ISO-639-1 alpha 2 code in lower case for the language, optionally followed by a hyphen and the ISO-3166-1 alpha 2 code
   * for the region in upper case; e.g. "en" for English, or "en-US" for American English versus "en-EN" for England English.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> language;
  /**
   * Indicates whether or not the patient prefers this language (over other languages he masters up a certain level).
   * <p>Cardinality: 0..1</p>
   */
  private boolean preferred;
  /**
   * Extension for preferred
   * <p>Cardinality: 0..1</p>
   */
  private Extension _preferred;

  public List<CodeableConcept> getLanguage() {
    return language;
  }

  public void setLanguage(List<CodeableConcept> language) {
    this.language = language;
  }

  public boolean isPreferred() {
    return preferred;
  }

  public void setPreferred(boolean preferred) {
    this.preferred = preferred;
  }

  public Extension get_preferred() {
    return _preferred;
  }

  public void set_preferred(Extension _preferred) {
    this._preferred = _preferred;
  }
}
