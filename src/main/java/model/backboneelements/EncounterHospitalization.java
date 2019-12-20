package model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.BackboneElement;
import model.datatypes.Identifier;
import model.elements.CodeableConcept;
import model.elements.Reference;

import java.util.List;

/**
 * Details about the admission to a healthcare service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncounterHospitalization extends BackboneElement {
  /**
   * Pre-admission identifier.
   * <p>Cardinality: 0..1</p>
   */
  private Identifier preAdmissionIdentifier;
  /**
   * The location/organization from which the patient came before admission.
   * <p>Cardinality: 0..1</p>
   */
  private Reference origin;
  /**
   * From where patient was admitted (physician referral, transfer).
   * <p>Code is preferred but not required https://www.hl7.org/fhir/valueset-encounter-admit-source.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept admitSource;
  /**
   * Whether this hospitalization is a readmission and why if known.
   * <p>Code is not required https://www.hl7.org/fhir/v2/0092/index.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept reAdmission;
  /**
   * Diet preferences reported by the patient.
   * <p>Code is not required https://www.hl7.org/fhir/valueset-encounter-diet.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> dietPreference;
  /**
   * Any special requests that have been made for this hospitalization encounter, such as the provision of specific equipment or other things.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> specialArrangement;
  /**
   * Location/organization to which the patient is discharged.
   * <p>Cardinality: 0..1</p>
   */
  private Reference destination;
  /**
   * Category or kind of location after discharge.
   * <p>Code is not required https://www.hl7.org/fhir/valueset-encounter-discharge-disposition.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept dischargeDisposition;

  public Identifier getPreAdmissionIdentifier() {
    return preAdmissionIdentifier;
  }

  public void setPreAdmissionIdentifier(Identifier preAdmissionIdentifier) {
    this.preAdmissionIdentifier = preAdmissionIdentifier;
  }

  public Reference getOrigin() {
    return origin;
  }

  public void setOrigin(Reference origin) {
    this.origin = origin;
  }

  public CodeableConcept getAdmitSource() {
    return admitSource;
  }

  public EncounterHospitalization setAdmitSource(CodeableConcept admitSource) {
    this.admitSource = admitSource;
    return this;
  }

  public CodeableConcept getReAdmission() {
    return reAdmission;
  }

  public void setReAdmission(CodeableConcept reAdmission) {
    this.reAdmission = reAdmission;
  }

  public List<CodeableConcept> getDietPreference() {
    return dietPreference;
  }

  public void setDietPreference(List<CodeableConcept> dietPreference) {
    this.dietPreference = dietPreference;
  }

  public List<CodeableConcept> getSpecialArrangement() {
    return specialArrangement;
  }

  public void setSpecialArrangement(List<CodeableConcept> specialArrangement) {
    this.specialArrangement = specialArrangement;
  }

  public Reference getDestination() {
    return destination;
  }

  public EncounterHospitalization setDestination(Reference destination) {
    this.destination = destination;
    return this;
  }

  public CodeableConcept getDischargeDisposition() {
    return dischargeDisposition;
  }

  public void setDischargeDisposition(CodeableConcept dischargeDisposition) {
    this.dischargeDisposition = dischargeDisposition;
  }
}
