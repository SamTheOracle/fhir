package com.oracolo.fhir.model.domain;

import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.Resource;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.elements.Extension;

import java.time.Instant;
import java.util.List;

public class Media extends FhirDomainResourceAbstract implements Resource {
  private String resourceType = "Media";

  private List<Identifier> identifier;
  private List<Reference> basedOn;
  private List<Reference> partOf;
  private String status;
  private Extension _status;
  private CodeableConcept type, modality, view;
  private Reference subject;
  private Reference encounter;
  private String createdDateTime;
  private Extension _createdDatetime;
  private Period createdPeriod;
  private Instant issued;
  private Extension _issued;
  private Reference operator;
  private List<CodeableConcept> reasonCode;
  private CodeableConcept bodySite;
  private String deviceName;
  private Extension _deviceName;
  private Reference device;
  private Integer height, width, frames;
  private Double duration;
  private Attachment content;
  private List<Annotation> note;
  private Extension _height, _width, _frames, _duration;

  public List<CodeableConcept> getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(List<CodeableConcept> reasonCode) {
    this.reasonCode = reasonCode;
  }

  public CodeableConcept getBodySite() {
    return bodySite;
  }

  public void setBodySite(CodeableConcept bodySite) {
    this.bodySite = bodySite;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public Extension get_deviceName() {
    return _deviceName;
  }

  public void set_deviceName(Extension _deviceName) {
    this._deviceName = _deviceName;
  }

  public Reference getDevice() {
    return device;
  }

  public void setDevice(Reference device) {
    this.device = device;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getFrames() {
    return frames;
  }

  public void setFrames(Integer frames) {
    this.frames = frames;
  }

  public Double getDuration() {
    return duration;
  }

  public void setDuration(Double duration) {
    this.duration = duration;
  }

  public Attachment getContent() {
    return content;
  }

  public void setContent(Attachment content) {
    this.content = content;
  }

  public List<Annotation> getNote() {
    return note;
  }

  public void setNote(List<Annotation> note) {
    this.note = note;
  }

  public Extension get_height() {
    return _height;
  }

  public void set_height(Extension _height) {
    this._height = _height;
  }

  public Extension get_width() {
    return _width;
  }

  public void set_width(Extension _width) {
    this._width = _width;
  }

  public Extension get_frames() {
    return _frames;
  }

  public void set_frames(Extension _frames) {
    this._frames = _frames;
  }

  public Extension get_duration() {
    return _duration;
  }

  public void set_duration(Extension _duration) {
    this._duration = _duration;
  }

  @Override
  public Media setId(String id) {
    this.id = id;
    return this;
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

  public List<Reference> getPartOf() {
    return partOf;
  }

  public void setPartOf(List<Reference> partOf) {
    this.partOf = partOf;
  }

  public String getStatus() {
    return status;
  }

  public Media setStatus(String status) {
    this.status = status;
    return this;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public CodeableConcept getType() {
    return type;
  }

  public Media setType(CodeableConcept type) {
    this.type = type;
    return this;
  }

  public CodeableConcept getModality() {
    return modality;
  }

  public void setModality(CodeableConcept modality) {
    this.modality = modality;
  }

  public CodeableConcept getView() {
    return view;
  }

  public void setView(CodeableConcept view) {
    this.view = view;
  }

  public Reference getSubject() {
    return subject;
  }

  public void setSubject(Reference subject) {
    this.subject = subject;
  }

  public Reference getEncounter() {
    return encounter;
  }

  public void setEncounter(Reference encounter) {
    this.encounter = encounter;
  }

  public String getCreatedDateTime() {
    return createdDateTime;
  }

  public Media setCreatedDateTime(String createdDateTime) {
    this.createdDateTime = createdDateTime;
    return this;
  }

  public Extension get_createdDatetime() {
    return _createdDatetime;
  }

  public void set_createdDatetime(Extension _createdDatetime) {
    this._createdDatetime = _createdDatetime;
  }

  public Period getCreatedPeriod() {
    return createdPeriod;
  }

  public void setCreatedPeriod(Period createdPeriod) {
    this.createdPeriod = createdPeriod;
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

  public Reference getOperator() {
    return operator;
  }

  public void setOperator(Reference operator) {
    this.operator = operator;
  }

  @Override
  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }
}
