package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * The list of people responsible for providing the service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterParticipant extends BackboneElement {

  private List<CodeableConcept> type;

  private Period period;

  private Reference individual;

  /**
   * Get the participant type
   *
   * @return a CodeableConcept of the type
   */
  public List<CodeableConcept> getType() {
    return type;
  }

  /**
   * Set the Role of participant in encounter.
   * <p>Cardinality: 0..*</p>
   * <p>Codes http://terminology.hl7.org/CodeSystem/v3-ParticipationType</p>
   *
   * @param type A list of CodeableConcept
   * @return a reference to this
   */
  public EncounterParticipant setType(List<CodeableConcept> type) {
    this.type = type;
    return this;
  }

  /**
   * The period of time that the specified participant participated in the encounter.
   * These can overlap or be sub-sets of the overall encounter's period.
   * <p>Cardinality: 0..1</p>
   *
   * @return a Period Resource
   */
  public Period getPeriod() {
    return period;
  }

  /**
   * The period of time that the specified participant participated in the encounter.
   * These can overlap or be sub-sets of the overall encounter's period.
   * <p>Cardinality: 0..1</p>
   *
   * @return a reference to this;
   */

  public EncounterParticipant setPeriod(Period period) {
    this.period = period;
    return this;
  }

  /**
   * Persons involved in the encounter other than the patient.
   * <p>Cardinality: 0..1</p>
   */
  public Reference getIndividual() {
    return individual;
  }

  /**
   * Persons involved in the encounter other than the patient.
   * <p>Cardinality: 0..1</p>
   */
  public EncounterParticipant setIndividual(Reference individual) {
    this.individual = individual;
    return this;
  }

  @Override
  public EncounterParticipant setId(String id) {
    super.setId(id);
    return this;
  }

  /**
   * Utility method to add a new Type
   *
   * @param type
   * @return a reference to this
   */
  public EncounterParticipant addNewType(CodeableConcept type) {
    if (this.type == null) {
      this.type = new ArrayList<>();
    }
    this.type.add(type);
    return this;
  }
}
