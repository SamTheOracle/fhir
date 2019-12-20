package model.elements;

import model.datatypes.ContactPoint;

import java.util.List;

/**
 * The ContactDetail structure defines general contact details.
 */
public class ContactDetail {

  /**
   * The name of an individual to contact.
   * <p>Cardinality: 0..1</p>
   */
  private String name;
  /**
   * Extension for name
   * <p>Cardinality: 0..1</p>
   */
  private Extension _name;
  /**
   * The contact details for the individual (if a name was provided) or the organization.
   * <p>Cardinality: 0..*</p>
   */
  private List<ContactPoint> telecom;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Extension get_name() {
    return _name;
  }

  public void set_name(Extension _name) {
    this._name = _name;
  }

  public List<ContactPoint> getTelecom() {
    return telecom;
  }

  public void setTelecom(List<ContactPoint> telecom) {
    this.telecom = telecom;
  }
}
