package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.datatypes.Period;

/**
 * The class history permits the tracking of the encounters transitions without needing to go through
 * the resource history. This would be used for a case where an admission starts of as an emergency encounter,
 * then transitions into an inpatient scenario. Doing this and not restarting a new encounter ensures that any lab/diagnostic
 * results can more easily follow the patient and not require re-processing and not get lost or cancelled
 * during a kind of discharge from emergency to inpatient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterClassHistory extends BackboneElement {

  /**
   * One of inpatient | outpatient | ambulatory | emergency
   * <p>See https://www.hl7.org/fhir/v3/ActEncounterCode/vs.html</p>
   * <p>Cardinality: 1..1</p>
   */
  @JsonProperty(value = "class")
  private Coding clazz;

  /**
   * The time that the episode was in the specified class.
   * <p>Cardinality: 1..1</p>
   */
  private Period period;

  public Coding getClazz() {
    return clazz;
  }

  public EncounterClassHistory setClazz(Coding clazz) {
    this.clazz = clazz;
    return this;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }
}
