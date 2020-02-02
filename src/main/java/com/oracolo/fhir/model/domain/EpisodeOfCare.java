package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.backboneelements.EpisodeOfCareDiagnosis;
import com.oracolo.fhir.model.backboneelements.EpisodeOfCareStatusHistory;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

import java.util.List;

/**
 * An association between a patient and an organization / healthcare provider(s)
 * during which time encounters may occur. The managing organization assumes a level of responsibility for the patient during this time.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodeOfCare extends DomainResource {
  private String resourceType = "EpisodeOfCare";

  @Override
  public String getResourceType() {
    return resourceType;
  }

  /**
   * The EpisodeOfCare may be known by different identifiers for different contexts of use,
   * such as when an external agency is tracking the Episode for funding purposes.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * planned | waitlist | active | onhold | finished | cancelled
   * <p>Required</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status;
  /**
   * A classification of the type of episode of care; e.g. specialist referral, disease management, type of funded care.
   * <p>Cardinality: 0..*</p>
   * <p>Code is not required http://www.hl7.org/fhir/valueset-episodeofcare-type.html</p>
   */
  private List<CodeableConcept> type;
  /**
   * The list of diagnosis relevant to this episode of care.
   * <p>Cardinality: 0..*</p>
   */
  private List<EpisodeOfCareDiagnosis> diagnosis;
  /**
   * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the resource).
   * <p>Cardinality: 0..*</p>
   */
  private List<EpisodeOfCareStatusHistory> statusHistory;
  /**
   * The patient who is the focus of this episode of care.
   * <p>Cardinality: 1..1</p>
   */
  private Reference patient;

  /**
   * The organization that has assumed the specific responsibilities for the specified duration.
   * <p>Cardinality: 0..1</p>
   */
  private Reference managingOrganization;
  /**
   * The interval during which the managing organization assumes the defined responsibility.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;
  /**
   * Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> referralRequest;
  /**
   * The practitioner that is the care manager/care coordinator for this patient.
   * <p>Cardinality: 0..1</p>
   */
  private Reference careManager;


  /**
   * Care team that took part
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> careTeam;
  /**
   * Accounts
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> account;

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public List<CodeableConcept> getType() {
    return type;
  }

  public void setType(List<CodeableConcept> type) {
    this.type = type;
  }

  public List<EpisodeOfCareDiagnosis> getDiagnosis() {
    return diagnosis;
  }

  public void setDiagnosis(List<EpisodeOfCareDiagnosis> diagnosis) {
    this.diagnosis = diagnosis;
  }

  public List<EpisodeOfCareStatusHistory> getStatusHistory() {
    return statusHistory;
  }

  public void setStatusHistory(List<EpisodeOfCareStatusHistory> statusHistory) {
    this.statusHistory = statusHistory;
  }

  public Reference getPatient() {
    return patient;
  }

  public void setPatient(Reference patient) {
    this.patient = patient;
  }

  public Reference getManagingOrganization() {
    return managingOrganization;
  }

  public void setManagingOrganization(Reference managingOrganization) {
    this.managingOrganization = managingOrganization;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public List<Reference> getReferralRequest() {
    return referralRequest;
  }

  public void setReferralRequest(List<Reference> referralRequest) {
    this.referralRequest = referralRequest;
  }

  public Reference getCareManager() {
    return careManager;
  }

  public void setCareManager(Reference careManager) {
    this.careManager = careManager;
  }

  public List<Reference> getCareTeam() {
    return careTeam;
  }

  public void setCareTeam(List<Reference> careTeam) {
    this.careTeam = careTeam;
  }

  public List<Reference> getAccount() {
    return account;
  }

  public void setAccount(List<Reference> account) {
    this.account = account;
  }
}
