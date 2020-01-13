package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.backboneelements.PatientContact;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Extension;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.model.elements.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This Resource covers data about patients and animals involved in a wide range of health-related activities, including:
 * <p>
 * 1) Curative activities
 * </p>
 * <p>
 * 2) Psychiatric care
 * </p>
 * <p>
 * 3) Social services
 * </p>
 * <p>
 * 4) Pregnancy care
 * </p>
 * <p>
 * 5) Nursing and assisted living
 * </p>
 * <p>
 * 6) Dietary services
 * </p>
 * <p>
 * 7) Tracking of personal health and exercise data
 * </p>
 * <p>
 * The data in the Resource covers the "who" information about the patient: its attributes are focused on the demographic
 * information necessary to support the administrative, financial and logistic procedures.
 * A Patient record is generally created and maintained by each organization providing care for a patient
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Patient extends DomainResource {

  public static final String SEARCH_PARAM_GIVEN = "_given";
  public static final String SEARCH_PARAM_FAMILY = "_family";
  /**
   * Resource type is "Patient"
   * <p>Cardinality: 1..1</p>
   */
  private String resourceType = "Patient";
  /**
   * A business identifier for this patient.
   * <p>Cardinality: 0..*</p>
   */
  private List<Identifier> identifier;
  /**
   * Whether this patient record is in active use. Many systems use this property to mark as non-current patients,
   * such as those that have not been seen for a period of time based on an organization's business rules.
   * <p>Cardinality: 0..1</p>
   */
  private boolean active;
  /**
   * Extension for active
   * <p>Cardinality: 0..1</p>
   */
  private Extension _active;
  /**
   * A name associated with the individual.
   * <p>Cardinality: 0..*</p>
   */
  private List<HumanName> name;
  /**
   * A patientContact detail (e.g. a telephone number or an email address) by which the individual may be contacted.
   * <p>Cardinality: 0..*</p>
   */
  private List<ContactPoint> telecom;
  /**
   * A patientContact detail (e.g. a telephone number or an email address) by which the individual may be contacted.
   * <p>Cardinality: 0..*</p>
   */
  private List<ContactPoint> contactPoints;

  /**
   * Administrative Gender - the gender that the patient is considered to have for administration and record keeping purposes.
   * <p>See http://hl7.org/fhir/valueset-administrative-gender.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String gender;
  /**
   * Extension for gender
   * <p>Cardinality: 0..1</p>
   */
  private Extension _gender;

  /**
   * The date of birth for the individual.
   * <p>Cardinality: 0..1</p>
   */
  private String birthDate;

  /**
   * Extension for birthDate
   * <p>Cardinality: 0..1</p>
   */
  private Extension _birthDate;
  /**
   * Deceased status
   * <p>Cardinality: 0..1</p>
   */
  private boolean deceasedBoolean;
  /**
   * Extension for deceasedBoolean
   * <p>Cardinality: 0..1</p>
   */
  private Extension _deceasedBoolean;
  /**
   * Deceased status
   * <p>Cardinality: 0..1</p>
   */
  private Date deceasedDateTime;
  /**
   * Extension for deceasedDateTime
   * <p>Cardinality: 0..1</p>
   */
  private Extension _deceasedDateTime;

  /**
   * An address for the individual.
   * <p>Cardinality: 0..*</p>
   */
  private List<Address> address;

  /**
   * This field contains a patient's most recent marital (civil) status.
   * <p>See http://hl7.org/fhir/valueset-marital-status.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private List<CodeableConcept> maritalStatus;

  /**
   * Indicates whether the patient is part of a multiple (boolean) or indicates the actual birth order (integer).
   * <p>Cardinality: 0..1</p>
   */
  private boolean multipleBirthBoolean;
  /**
   * Extension for multipleBirthBoolean
   * <p>Cardinality: 0..1</p>
   */
  private Extension _multipleBirthBoolean;
  /**
   * Indicates whether the patient is part of a multiple (boolean) or indicates the actual birth order (integer).
   * <p>Cardinality: 0..1</p>
   */
  private int multipleBirthInteger;
  /**
   * Extension for multipleBirthInteger
   * <p>Cardinality: 0..1</p>
   */
  private Extension _multipleBirthInteger;

  /**
   * Image of the patient.
   * <p>Cardinality: 0..*</p>
   */
  private List<Attachment> photo;
  /**
   * A patientContact party (e.g. guardian, partner, friend) for the patient.
   * <p>Cardinality: 0..*</p>
   */
  private List<PatientContact> patientContact;

  /**
   * A language which may be used to communicate with the patient about his or her health.
   * <p>Cardinality: 0..*</p>
   */
  private List<Communication> communication;

  /**
   * Patient's nominated care provider.
   * <p>Cardinality:0..*</p>
   */
  private List<Reference> generalPractitioner;

  /**
   * Organization that is the custodian of the patient record.
   * <p>Cardinality: 0..1</p>
   */
  private Reference managingOrganization;

  /**
   * Link to another patient resource that concerns the same actual patient.
   * <p>Cardinality: 0..*</p>
   */
  private List<Link> link;


  public Patient() {
  }

  @Override
  public Patient setId(String id) {
    this.id = id;
    return this;
  }

  @Override
  public Patient setMeta(Metadata meta) {
    this.meta = meta;
    return this;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public boolean isActive() {
    return active;
  }

  public List<ContactPoint> getTelecom() {
    return telecom;
  }

  public void setTelecom(List<ContactPoint> telecom) {
    this.telecom = telecom;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public List<HumanName> getName() {
    return name;
  }

  public Extension get_active() {
    return _active;
  }

  public void set_active(Extension _active) {
    this._active = _active;
  }

  public Extension get_gender() {
    return _gender;
  }

  public void set_gender(Extension _gender) {
    this._gender = _gender;
  }

  public Extension get_birthDate() {
    return _birthDate;
  }

  public void set_birthDate(Extension _birthDate) {
    this._birthDate = _birthDate;
  }

  public Extension get_deceasedBoolean() {
    return _deceasedBoolean;
  }

  public void set_deceasedBoolean(Extension _deceasedBoolean) {
    this._deceasedBoolean = _deceasedBoolean;
  }

  public Extension get_deceasedDateTime() {
    return _deceasedDateTime;
  }

  public void set_deceasedDateTime(Extension _deceasedDateTime) {
    this._deceasedDateTime = _deceasedDateTime;
  }

  public Extension get_multipleBirthBoolean() {
    return _multipleBirthBoolean;
  }

  public void set_multipleBirthBoolean(Extension _multipleBirthBoolean) {
    this._multipleBirthBoolean = _multipleBirthBoolean;
  }

  public Extension get_multipleBirthInteger() {
    return _multipleBirthInteger;
  }

  public void set_multipleBirthInteger(Extension _multipleBirthInteger) {
    this._multipleBirthInteger = _multipleBirthInteger;
  }

  public Patient setName(List<HumanName> name) {
    this.name = name;
    return this;
  }

  public List<ContactPoint> getContactPoints() {
    return contactPoints;
  }

  public void setContactPoints(List<ContactPoint> contactPoints) {
    this.contactPoints = contactPoints;
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

  public Patient setBirthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public boolean isDeceasedBoolean() {
    return deceasedBoolean;
  }

  public Patient setDeceasedBoolean(boolean deceasedBoolean) {
    this.deceasedBoolean = deceasedBoolean;
    return this;
  }

  public Date getDeceasedDateTime() {
    return deceasedDateTime;
  }

  public void setDeceasedDateTime(Date deceasedDateTime) {
    this.deceasedDateTime = deceasedDateTime;
  }

  public List<Address> getAddress() {
    return address;
  }

  public void setAddress(List<Address> address) {
    this.address = address;
  }

  public List<CodeableConcept> getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(List<CodeableConcept> maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

  public boolean isMultipleBirthBoolean() {
    return multipleBirthBoolean;
  }

  public void setMultipleBirthBoolean(boolean multipleBirthBoolean) {
    this.multipleBirthBoolean = multipleBirthBoolean;
  }

  public int getMultipleBirthInteger() {
    return multipleBirthInteger;
  }

  public void setMultipleBirthInteger(int multipleBirthInteger) {
    this.multipleBirthInteger = multipleBirthInteger;
  }

  public List<Attachment> getPhoto() {
    return photo;
  }

  public void setPhoto(List<Attachment> photo) {
    this.photo = photo;
  }

  public List<PatientContact> getPatientContact() {
    return patientContact;
  }

  public void setPatientContact(List<PatientContact> patientContact) {
    this.patientContact = patientContact;
  }

  public List<Communication> getCommunication() {
    return communication;
  }

  public void setCommunication(List<Communication> communication) {
    this.communication = communication;
  }

  public List<Reference> getGeneralPractitioner() {
    return generalPractitioner;
  }

  public void setGeneralPractitioner(List<Reference> generalPractitioner) {
    this.generalPractitioner = generalPractitioner;
  }

  public Reference getManagingOrganization() {
    return managingOrganization;
  }

  public void setManagingOrganization(Reference managingOrganization) {
    this.managingOrganization = managingOrganization;
  }

  public List<Link> getLink() {
    return link;
  }

  public void setLink(List<Link> link) {
    this.link = link;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public Patient addNewHumanName(HumanName humanName) {
    if (this.name == null) {
      this.name = new ArrayList<>();
    }
    name.add(humanName);
    return this;
  }
}
