package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.Extension;

/**
 * The status history permits the encounter resource to contain the status history
 * without needing to read through the historical versions of the resource, or even have the server store them.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterStatusHistory extends BackboneElement {
  /**
   * Status of Encounter, one of planned | arrived | triaged | in-progress | onleave | finished | cancelled etc.
   * <p>Required https://www.hl7.org/fhir/valueset-encounter-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * The time that the episode was in the specified status.
   * <p>Cardinality: 1..1</p>
   */
  private Period period;

  public String getStatus() {
    return status;
  }

  public EncounterStatusHistory setStatus(String status) {
    this.status = status;
    return this;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public Period getPeriod() {
    return period;
  }

  public EncounterStatusHistory setPeriod(Period period) {
    this.period = period;
    return this;
  }
}
