package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.backboneelements.MedicationAdministrationDosage;
import com.oracolo.fhir.model.backboneelements.MedicationAdministrationPerformer;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.Annotation;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdministration extends DomainResource {
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
  private MedicationAdministrationPerformer performer;
  private CodeableConcept reasonCode;
  private Reference reasonReference;
  private Reference request;
  private List<Reference> device;
  private List<Annotation> note;
  private MedicationAdministrationDosage dosage;
  private Reference eventHistory;

  @Override
  public String getResourceType() {
    return resourceType;
  }

  @Override
  public MedicationAdministration setId(String id) {
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

  public void setContext(Reference context) {
    this.context = context;
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

  public void setEffectiveDateTime(String effectiveDateTime) {
    this.effectiveDateTime = effectiveDateTime;
  }

  public Period getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(Period effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
  }

  public MedicationAdministrationPerformer getPerformer() {
    return performer;
  }

  public void setPerformer(MedicationAdministrationPerformer performer) {
    this.performer = performer;
  }

  public CodeableConcept getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(CodeableConcept reasonCode) {
    this.reasonCode = reasonCode;
  }

  public Reference getReasonReference() {
    return reasonReference;
  }

  public void setReasonReference(Reference reasonReference) {
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

  public Reference getEventHistory() {
    return eventHistory;
  }

  public void setEventHistory(Reference eventHistory) {
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
