package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.backboneelements.MedicationAdministrationDosage;
import com.oracolo.fhir.model.backboneelements.MedicationAdministrationPerformer;
import com.oracolo.fhir.model.datatypes.*;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdministration extends FhirDomainResourceAbstract {
  private String resourceType = "MedicationAdministration";

  private List<Identifier> identifier;

  private List<String> instantiates;

  private List<Reference> partOf;
  private String status;
  private List<CodeableConcept> statusReason;
  private CodeableConcept category;
  private CodeableConcept medicationCodeableConcept;
  private Reference medicationReference;
  private Reference subject;
  private Reference context;
  private Reference supportingInformation;
  private String effectiveDateTime;
  private Period effectivePeriod;
  private List<MedicationAdministrationPerformer> performer;
  private List<CodeableConcept> reasonCode;
  private List<Reference> reasonReference;
  private Reference request;
  private List<Reference> device;
  private List<Annotation> note;
  private MedicationAdministrationDosage dosage;
  private List<Reference> eventHistory;

  @Override
  public String getResourceType() {
    return resourceType;
  }

  @Override
  public MedicationAdministration setId(String id) {
    this.id = id;
    return this;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public List<String> getInstantiates() {
    return instantiates;
  }

  public void setInstantiates(List<String> instantiates) {
    this.instantiates = instantiates;
  }

  public List<Reference> getPartOf() {
    return partOf;
  }

  public void setPartOf(List<Reference> partOf) {
    this.partOf = partOf;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<CodeableConcept> getStatusReason() {
    return statusReason;
  }

  public void setStatusReason(List<CodeableConcept> statusReason) {
    this.statusReason = statusReason;
  }

  public CodeableConcept getCategory() {
    return category;
  }

  public void setCategory(CodeableConcept category) {
    this.category = category;
  }

  public CodeableConcept getMedicationCodeableConcept() {
    return medicationCodeableConcept;
  }

  public MedicationAdministration setMedicationCodeableConcept(CodeableConcept medicationCodeableConcept) {
    this.medicationCodeableConcept = medicationCodeableConcept;
    return this;
  }

  public Reference getMedicationReference() {
    return medicationReference;
  }

  public void setMedicationReference(Reference medicationReference) {
    this.medicationReference = medicationReference;
  }

  public Reference getSubject() {
    return subject;
  }

  public void setSubject(Reference subject) {
    this.subject = subject;
  }

  public Reference getContext() {
    return context;
  }

  public MedicationAdministration setContext(Reference context) {
    this.context = context;
    return this;
  }

  public Reference getSupportingInformation() {
    return supportingInformation;
  }

  public void setSupportingInformation(Reference supportingInformation) {
    this.supportingInformation = supportingInformation;
  }

  public String getEffectiveDateTime() {
    return effectiveDateTime;
  }

  public MedicationAdministration setEffectiveDateTime(String effectiveDateTime) {
    this.effectiveDateTime = effectiveDateTime;
    return this;
  }

  public Period getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(Period effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
  }

  public List<MedicationAdministrationPerformer> getPerformer() {
    return performer;
  }

  public void setPerformer(List<MedicationAdministrationPerformer> performer) {
    this.performer = performer;
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

  public Reference getRequest() {
    return request;
  }

  public void setRequest(Reference request) {
    this.request = request;
  }

  public List<Reference> getDevice() {
    return device;
  }

  public void setDevice(List<Reference> device) {
    this.device = device;

  }

  public List<Annotation> getNote() {
    return note;
  }

  public MedicationAdministration setNote(List<Annotation> annotations) {
    note = annotations;
    return this;
  }

  public MedicationAdministrationDosage getDosage() {
    return dosage;
  }

  public void setDosage(MedicationAdministrationDosage dosage) {
    this.dosage = dosage;
  }

  public List<Reference> getEventHistory() {
    return eventHistory;
  }

  public void setEventHistory(List<Reference> eventHistory) {
    this.eventHistory = eventHistory;
  }

  public MedicationAdministration addNewNote(Annotation annotation) {
    if (note == null) {
      note = new ArrayList<>();
    }
    note.add(annotation);
    return this;
  }
}
