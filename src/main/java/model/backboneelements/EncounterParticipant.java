package model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.BackboneElement;
import model.datatypes.Period;
import model.elements.CodeableConcept;
import model.elements.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * The list of people responsible for providing the service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterParticipant extends BackboneElement {
  /**
   * Role of participant in encounter.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> type;
  /**
   * The period of time that the specified participant participated in the encounter.
   * These can overlap or be sub-sets of the overall encounter's period.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;
  /**
   * Persons involved in the encounter other than the patient.
   * <p>Cardinality: 0..1</p>
   */
  private Reference individual;

  public List<CodeableConcept> getType() {
    return type;
  }

  public EncounterParticipant setType(List<CodeableConcept> type) {
    this.type = type;
    return this;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Reference getIndividual() {
    return individual;
  }

  public EncounterParticipant setIndividual(Reference individual) {
    this.individual = individual;
    return this;
  }

  @Override
  public EncounterParticipant setId(String id) {
    super.setId(id);
    return this;
  }

  public EncounterParticipant addNewType(CodeableConcept type) {
    if (this.type == null) {
      this.type = new ArrayList<>();
    }
    this.type.add(type);
    return this;
  }
}
