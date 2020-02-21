package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.FhirDomainResourceAbstract;
import com.oracolo.fhir.model.backboneelements.PractitionerQualification;
import com.oracolo.fhir.model.datatypes.*;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Practitioner extends FhirDomainResourceAbstract {
  private String resourceType = "Practitioner";


  private List<Identifier> identifier;

  private Boolean active;

  private List<HumanName> name;

  private List<ContactPoint> telecom;

  private List<Address> address;

  private String gender;

  private String birthDate;

  private List<Attachment> photo;

  private List<PractitionerQualification> qualification;

  private List<CodeableConcept> communication;

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public Boolean getActive() {
    return active;
  }

  public Practitioner setActive(Boolean active) {
    this.active = active;
    return this;
  }

  public List<HumanName> getName() {
    return name;
  }

  public void setName(List<HumanName> name) {
    this.name = name;
  }

  public List<ContactPoint> getTelecom() {
    return telecom;
  }

  public void setTelecom(List<ContactPoint> telecom) {
    this.telecom = telecom;
  }

  public List<Address> getAddress() {
    return address;
  }

  public void setAddress(List<Address> address) {
    this.address = address;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public List<Attachment> getPhoto() {
    return photo;
  }

  public void setPhoto(List<Attachment> photo) {
    this.photo = photo;
  }

  public List<PractitionerQualification> getQualification() {
    return qualification;
  }

  public void setQualification(List<PractitionerQualification> qualification) {
    this.qualification = qualification;
  }

  public List<CodeableConcept> getCommunication() {
    return communication;
  }

  public void setCommunication(List<CodeableConcept> communication) {
    this.communication = communication;
  }

  @Override
  public Practitioner setId(String id) {
    this.id = id;
    return this;
  }

  @Override
  public String getResourceType() {
    return resourceType;
  }

  public Practitioner addNewIdentifier(Identifier identifier) {
    if (this.identifier == null) {
      this.identifier = new ArrayList<>();
    }
    this.identifier.add(identifier);
    return this;
  }
}
