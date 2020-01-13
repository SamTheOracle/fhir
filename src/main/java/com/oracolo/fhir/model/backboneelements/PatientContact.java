package com.oracolo.fhir.model.backboneelements;

import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Address;
import com.oracolo.fhir.model.datatypes.ContactPoint;
import com.oracolo.fhir.model.datatypes.HumanName;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Reference;

import java.util.List;

/**
 * Custom object representing a contact
 */
public class PatientContact extends BackboneElement {


  /**
   * The nature of the relationship between the patient and the contact person.
   * <p>Cardinality: 0..*</p>
   */
  private List<CodeableConcept> relationship;
  /**
   * A name associated with the contact person.
   * <p>Cardinality: 0..1</p>
   */
  private HumanName name;
  /**
   * A contact detail for the person, e.g. a telephone number or an email address.
   * <p>Cardinality: 0..*</p>
   */
  private List<ContactPoint> telecom;
  /**
   * Address for the contact person.
   * <p>Cardinality: 0..1</p>
   */
  private Address address;
  /**
   * Administrative Gender - the gender that the contact person is considered to have for administration and record keeping purposes.
   * <p>Cardinality: 0..1</p>
   */
  private String gender;
  /**
   * Organization on behalf of which the contact is acting or for which the contact is working.
   * <p>Cardinality: 0..1</p>
   */
  private Reference organization;
  /**
   * The period during which this contact person or organization is valid to be contacted relating to this patient.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;

  public List<CodeableConcept> getRelationship() {
    return relationship;
  }

  public void setRelationship(List<CodeableConcept> relationship) {
    this.relationship = relationship;
  }

  public void setName(HumanName name) {
    this.name = name;
  }

  public List<ContactPoint> getTelecom() {
    return telecom;
  }

  public void setTelecom(List<ContactPoint> telecom) {
    this.telecom = telecom;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public Reference getOrganization() {
    return organization;
  }

  public void setOrganization(Reference organization) {
    this.organization = organization;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }
}
