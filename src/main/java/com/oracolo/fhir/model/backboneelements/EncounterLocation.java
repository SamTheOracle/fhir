package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.datatypes.Reference;
import com.oracolo.fhir.model.elements.Extension;

/**
 * List of locations where the patient has been during this encounter.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterLocation extends BackboneElement {
  /**
   * The location where the encounter takes place.
   * <p>Cardinality: 1..1</p>
   */
  private Reference location;
  /**
   * The status of the participants' presence at the specified location during the period specified.
   * If the participant is no longer at the location, then the period will have an end date/time.
   * <p>Code is required https://www.hl7.org/fhir/valueset-encounter-location-status.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either messaging or query.
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept physicalType;
  /**
   * Time period during which the patient was present at the location.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;

  public Reference getLocation() {
    return location;
  }

  /**
   * The location where the encounter takes place.
   * <p>Cardinality: 1..1</p>
   */
  public EncounterLocation setLocation(Reference location) {
    this.location = location;
    return this;
  }

  public String getStatus() {
    return status;
  }

  /**
   * The status of the participants' presence at the specified location during the period specified.
   * If the participant is no longer at the location, then the period will have an end date/time.
   * <p>Code is required https://www.hl7.org/fhir/valueset-encounter-location-status.html</p>
   * <p>planned,active,reserved,completed</p>
   * <p>Cardinality: 0..1</p>
   */
  public EncounterLocation setStatus(String status) {
    this.status = status;
    return this;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public CodeableConcept getPhysicalType() {
    return physicalType;
  }

  /**
   * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either messaging or query.
   * <p>Cardinality: 0..1</p>
   */
  public EncounterLocation setPhysicalType(CodeableConcept physicalType) {
    this.physicalType = physicalType;
    return this;
  }

  public Period getPeriod() {
    return period;
  }

  public EncounterLocation setPeriod(Period period) {
    this.period = period;
    return this;
  }
}
