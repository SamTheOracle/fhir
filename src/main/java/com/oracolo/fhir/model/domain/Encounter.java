package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.backboneelements.*;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Duration;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * An interaction between a patient and healthcare provider(s) for the purpose of providing
 * healthcare service(s) or assessing the health status of a patient.
 * A patient encounter is further characterized by the setting in which it takes place. Amongst them are ambulatory,
 * emergency, home health, inpatient and virtual encounters. An Encounter encompasses the lifecycle from pre-admission,
 * the actual encounter (for ambulatory encounters), and admission, stay and discharge (for inpatient encounters).
 * <p>During the encounter the patient may move from practitioner to practitioner and location to location.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Encounter extends FhirDomainResourceAbstract {

  @JsonIgnore
  public final static String everything = "$everything";


  private String resourceType = "Encounter";
  /**
   * Identifier(s) by which this encounter is known.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
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
  private List<EncounterStatusHistory> statusHistory;

  /**
   * Concepts representing classification of patient encounter such as ambulatory (outpatient),
   * inpatient, emergency, home health or others due to local variations.
   * <p>Cardinality: 1..1</p>
   */
  @JsonProperty("class")
  private Coding clazz;
  /**
   * The class history permits the tracking of the encounters transitions without needing to go through the resource history.
   * This would be used for a case where an admission starts of as an emergency encounter, then transitions into an inpatient
   * scenario. Doing this and not restarting a new encounter ensures that any lab/diagnostic results can more easily
   * follow the patient and not require re-processing and not get lost or cancelled during a kind of discharge from emergency to
   * inpatient.
   */
  private List<EncounterClassHistory> classHistory;
  /**
   * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
   * <p>Code is not required https://www.hl7.org/fhir/valueset-service-type.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> type;

  /**
   * Broad categorization of the service that is to be provided (e.g. cardiology).
   * <p>Code is not required https://www.hl7.org/fhir/valueset-service-type.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept serviceType;

  /**
   * Indicates the urgency of the encounter.
   * <p>Code is not required https://www.hl7.org/fhir/v3/ActPriority/vs.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept priority;

  /**
   * The patient or group present at the encounter.
   * <p>Cardinality: 0..1</p>
   */
  private Reference subject;
  /**
   * Where a specific encounter should be classified as a part of a specific episode(s) of care this field
   * should be used. This association can facilitate grouping of related encounters together for a specific purpose,
   * such as government reporting, issue tracking, association via a common problem. The association is recorded on the encounter
   * as these are typically created after the episode of care and grouped on entry rather than editing the episode of care to
   * append another encounter to it (the episode of care could span years).
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> episodeOfCare;
  /**
   * The request this encounter satisfies (e.g. incoming referral or procedure request).
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> basedOn;
  /**
   * The list of people responsible for providing the service.
   * <p>Cardinality: 0..*</p>
   */
  private List<EncounterParticipant> participant;
  /**
   * The appointment that scheduled this encounter.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> appointment;
  /**
   * The start and end time of the encounter.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;
  /**
   * Quantity of time the encounter lasted. This excludes the time during leaves of absence.
   * <p>Cardinality: 0..1</p>
   */
  private Duration duration;
  /**
   * Reason the encounter takes place, expressed as a code. For admissions, this can be used for a coded admission diagnosis.
   * <p>The code is preferred but not required https://www.hl7.org/fhir/valueset-encounter-reason.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> reasonCode;
  /**
   * Reason the encounter takes place, expressed as a code. For admissions, this can be used for a coded admission diagnosis.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> reasonReference;

  /**
   * The list of diagnosis relevant to this encounter.
   * <p>Cardinality: 0..*</p>
   */
  private List<EncounterDiagnosis> diagnosis;
  /**
   * The set of accounts that may be used for billing for this Encounter.
   * <p>Cardinality: 0..*</p>
   */
  private List<Reference> account;

  /**
   * Details about the admission to a healthcare service
   * <p>Cardinality: 0..*</p>
   */
  private EncounterHospitalization hospitalization;
  /**
   * Encounter locations
   * <p>Cardinality: 0..*</p>
   */
  private List<EncounterLocation> location;
  /**
   * The organization that is primarily responsible for this Encounter's services. This MAY be the same as the organization on the Patient record, however it could be different, such as if the actor performing the services was from an external organization (which may be billed seperately) for an external consultation.
   * Refer to the example bundle showing an abbreviated set of Encounters for a colonoscopy.
   * <p>Cardinality: 0..1</p>
   */
  private Reference serviceProvider;
  /**
   * Another Encounter of which this encounter is a part of (administratively or in time).
   * <p>Cardinality: 0..1</p>
   */
  private Reference partOf;



  public Encounter addNewParticipant(EncounterParticipant encounterParticipant) {
    if (participant == null) {
      participant = new ArrayList<>();
    }
    participant.add(encounterParticipant);
    return this;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public Encounter setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
    return this;
  }

  public String getStatus() {
    return status;
  }

  public Encounter setStatus(String status) {
    this.status = status;
    return this;
  }



  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public List<EncounterStatusHistory> getStatusHistory() {
    return statusHistory;
  }

  public void setStatusHistory(List<EncounterStatusHistory> statusHistory) {
    this.statusHistory = statusHistory;
  }

  public Coding getClazz() {
    return clazz;
  }

  public Encounter setClazz(Coding clazz) {
    this.clazz = clazz;
    return this;
  }

  public List<EncounterClassHistory> getClassHistory() {
    return classHistory;
  }

  public void setClassHistory(List<EncounterClassHistory> classHistory) {
    this.classHistory = classHistory;
  }

  public List<CodeableConcept> getType() {
    return type;
  }

  public void setType(List<CodeableConcept> type) {
    this.type = type;
  }

  public CodeableConcept getServiceType() {
    return serviceType;
  }

  public Encounter setServiceType(CodeableConcept serviceType) {
    this.serviceType = serviceType;
    return this;
  }

  public CodeableConcept getPriority() {
    return priority;
  }

  public Encounter setPriority(CodeableConcept priority) {
    this.priority = priority;
    return this;
  }

  public Reference getSubject() {
    return subject;
  }

  public Encounter setSubject(Reference subject) {
    this.subject = subject;
    return this;
  }

  @Override
  public String getId() {
    return id;
  }

  public List<Reference> getEpisodeOfCare() {
    return episodeOfCare;
  }

  public void setEpisodeOfCare(List<Reference> episodeOfCare) {
    this.episodeOfCare = episodeOfCare;
  }

  public List<Reference> getBasedOn() {
    return basedOn;
  }

  public void setBasedOn(List<Reference> basedOn) {
    this.basedOn = basedOn;
  }

  public List<EncounterParticipant> getParticipant() {
    return participant;
  }

  public void setParticipant(List<EncounterParticipant> participant) {
    this.participant = participant;
  }

  public List<Reference> getAppointment() {
    return appointment;
  }

  public void setAppointment(List<Reference> appointment) {
    this.appointment = appointment;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }


  public List<CodeableConcept> getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(List<CodeableConcept> reasonCode) {
    this.reasonCode = reasonCode;
  }

  public List<Reference> getReasonReference() {
    return reasonReference;
  }

  public void setReasonReference(List<Reference> reasonReference) {
    this.reasonReference = reasonReference;
  }

  public List<EncounterDiagnosis> getDiagnosis() {
    return diagnosis;
  }

  public void setDiagnosis(List<EncounterDiagnosis> diagnosis) {
    this.diagnosis = diagnosis;
  }

  public List<Reference> getAccount() {
    return account;
  }

  public void setAccount(List<Reference> account) {
    this.account = account;
  }

  public EncounterHospitalization getHospitalization() {
    return hospitalization;
  }

  public Encounter setHospitalization(EncounterHospitalization hospitalization) {
    this.hospitalization = hospitalization;
    return this;
  }

  public List<EncounterLocation> getLocation() {
    return location;
  }

  public void setLocation(List<EncounterLocation> location) {
    this.location = location;
  }

  public Reference getServiceProvider() {
    return serviceProvider;
  }

  public Encounter setServiceProvider(Reference serviceProvider) {
    this.serviceProvider = serviceProvider;
    return this;
  }

  public Reference getPartOf() {
    return partOf;
  }

  public Encounter setPartOf(Reference partOf) {
    this.partOf = partOf;
    return this;
  }


  @Override
  public Encounter setId(String id) {
    super.setId(id);
    return this;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public Encounter addNewDiagnosis(EncounterDiagnosis encounterDiagnosis) {
    if (diagnosis == null) {
      diagnosis = new ArrayList<>();
    }
    diagnosis.add(encounterDiagnosis);
    return this;
  }


  public Encounter addNewType(CodeableConcept codeableConcept) {
    if (type == null) {
      type = new ArrayList<>();
    }
    type.add(codeableConcept);
    return this;
  }

  public Encounter addNewLocation(EncounterLocation encounterLocation) {
    if (location == null) {
      location = new ArrayList<>();
    }
    location.add(encounterLocation);
    return this;
  }

  public Encounter addNewReasonCode(CodeableConcept codeableConcept) {
    if (reasonCode == null) {
      reasonCode = new ArrayList<>();
    }
    reasonCode.add(codeableConcept);
    return this;
  }

  public Encounter addNewClassHistory(EncounterClassHistory encounterClassHistory) {
    if (classHistory == null) {
      classHistory = new ArrayList<>();
    }
    classHistory.add(encounterClassHistory);
    return this;
  }

  public Encounter addNewStatusHistory(EncounterStatusHistory encounterStatusHistory) {
    if (statusHistory == null) {
      statusHistory = new ArrayList<>();
    }
    statusHistory.add(encounterStatusHistory);
    return this;
  }

  public Encounter addNewReasonReference(Reference reasonReference) {
    if (this.reasonReference == null) {
      this.reasonReference = new ArrayList<>();
    }
    this.reasonReference.add(reasonReference);
    return this;
  }

  public Encounter addNewEncounterParticipant(EncounterParticipant encounterParticipant) {
    if (participant == null) {
      participant = new ArrayList<>();
    }
    participant.add(encounterParticipant);
    return this;
  }

  public Encounter addNewContained(Object resource) {
    if (contained == null) {
      contained = new ArrayList<>();
    }
    contained.add(resource);
    return this;
  }

  public Encounter addNewIdentifier(Identifier identifier) {
    if (this.identifier == null) {
      this.identifier = new ArrayList<>();
    }
    this.identifier.add(identifier);
    return this;
  }
}
