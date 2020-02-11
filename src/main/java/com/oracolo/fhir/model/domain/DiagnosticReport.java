package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.backboneelements.DiagnosticReportMedia;
import com.oracolo.fhir.model.datatypes.Attachment;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Reference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagnosticReport extends DomainResource {

  private String resourceType = "DiagnosticReport";

  private List<Identifier> identifier;
  private List<Reference> basedOn;
  private String status;
  private Extension _status;
  private List<CodeableConcept> category;
  private CodeableConcept code;
  private Reference subject;
  private Reference encounter;
  private String effectiveDatetime;
  private Extension _effectiveDateTime;
  private Period effectivePeriod;
  private Instant issued;
  private Extension _issued;
  private List<Reference> performer, resultsInterpreter, specimen, result, imagingStudy;
  private DiagnosticReportMedia media;
  private String conclusion;
  private Extension _conclusion;
  private List<CodeableConcept> conclusionCode;
  private List<Attachment> presentedForm;


  public Reference getEncounter() {
    return encounter;
  }

  public DiagnosticReport setEncounter(Reference encounter) {
    this.encounter = encounter;
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

  public List<Reference> getBasedOn() {
    return basedOn;
  }

  public void setBasedOn(List<Reference> basedOn) {
    this.basedOn = basedOn;
  }

  public String getStatus() {
    return status;
  }

  public DiagnosticReport setStatus(String status) {
    this.status = status;
    return this;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public List<CodeableConcept> getCategory() {
    return category;
  }

  public void setCategory(List<CodeableConcept> category) {
    this.category = category;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public DiagnosticReport setCode(CodeableConcept code) {
    this.code = code;
    return this;
  }

  public Reference getSubject() {
    return subject;
  }

  public void setSubject(Reference subject) {
    this.subject = subject;
  }

  public String getEffectiveDatetime() {
    return effectiveDatetime;
  }

  public DiagnosticReport setEffectiveDatetime(String effectiveDatetime) {
    this.effectiveDatetime = effectiveDatetime;
    return this;
  }

  public Extension get_effectiveDateTime() {
    return _effectiveDateTime;
  }

  public void set_effectiveDateTime(Extension _effectiveDateTime) {
    this._effectiveDateTime = _effectiveDateTime;
  }

  public Period getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(Period effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
  }

  public Instant getIssued() {
    return issued;
  }

  public void setIssued(Instant issued) {
    this.issued = issued;
  }

  public Extension get_issued() {
    return _issued;
  }

  public void set_issued(Extension _issued) {
    this._issued = _issued;
  }

  public List<Reference> getPerformer() {
    return performer;
  }

  public void setPerformer(List<Reference> performer) {
    this.performer = performer;
  }

  public List<Reference> getResultsInterpreter() {
    return resultsInterpreter;
  }

  public void setResultsInterpreter(List<Reference> resultsInterpreter) {
    this.resultsInterpreter = resultsInterpreter;
  }

  public List<Reference> getSpecimen() {
    return specimen;
  }

  public void setSpecimen(List<Reference> specimen) {
    this.specimen = specimen;
  }

  public List<Reference> getResult() {
    return result;
  }

  public void setResult(List<Reference> result) {
    this.result = result;
  }

  public List<Reference> getImagingStudy() {
    return imagingStudy;
  }

  public void setImagingStudy(List<Reference> imagingStudy) {
    this.imagingStudy = imagingStudy;
  }

  public DiagnosticReportMedia getMedia() {
    return media;
  }

  public void setMedia(DiagnosticReportMedia media) {
    this.media = media;
  }

  public String getConclusion() {
    return conclusion;
  }

  public DiagnosticReport setConclusion(String conclusion) {
    this.conclusion = conclusion;
    return this;
  }

  public Extension get_conclusion() {
    return _conclusion;
  }

  public void set_conclusion(Extension _conclusion) {
    this._conclusion = _conclusion;
  }

  public List<CodeableConcept> getConclusionCode() {
    return conclusionCode;
  }

  public void setConclusionCode(List<CodeableConcept> conclusionCode) {
    this.conclusionCode = conclusionCode;
  }

  public List<Attachment> getPresentedForm() {
    return presentedForm;
  }

  public void setPresentedForm(List<Attachment> presentedForm) {
    this.presentedForm = presentedForm;
  }

  @Override
  public DiagnosticReport setId(String id) {
    this.id = id;
    return this;
  }

  @Override
  public String getResourceType() {
    return resourceType;
  }

  public DiagnosticReport addNewResult(Reference observation) {
    if (result == null) {
      result = new ArrayList<>();
    }
    result.add(observation);
    return this;
  }

  public DiagnosticReport addNewContained(Object object) {
    if (contained == null) {
      contained = new ArrayList<>();
    }
    contained.add(object);
    return this;
  }
}
