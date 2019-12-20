package model.datatypes;

import model.Element;
import model.elements.Extension;

import java.util.List;

/**
 * An address expressed using postal conventions (as opposed to GPS or other location definition formats).
 * This data type may be used to convey addresses for use in delivering mail as well as for visiting locations which might not be valid
 * for mail delivery. There are a variety of postal address formats defined around the world.
 */
public class Address extends Element {
  /**
   * The purpose of this address.
   * <p>See http://hl7.org/fhir/valueset-address-use.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String use;
  /**
   * Extension for use
   * <p>Cardinality: 0..1</p>
   */
  private Extension _use;
  /**
   * Distinguishes between physical addresses (those you can visit) and mailing addresses
   * (e.g. PO Boxes and care-of addresses). Most addresses are both.
   * <p>See http://hl7.org/fhir/valueset-address-type.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String type;
  /**
   * Extension for type
   * <p>Cardinality: 0..1</p>
   */
  private Extension _type;
  /**
   * Specifies the entire address as it should be displayed e.g. on a postal label.
   * This may be provided instead of or as well as the specific parts.
   * <p>Cardinality: 0..1</p>
   */
  private String text;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _text      ;
  /**
   * This component contains the house number, apartment number, street name, street direction, P.O. Box number,
   * delivery hints, and similar address information.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> line;
  /**
   * Extension for line
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _line  ;
  /**
   * The name of the city, town, suburb, village or other community or delivery center.
   * <p>Cardinality: 0..1</p>
   */
  private String city;
  /**
   * Extension for city
   * <p>Cardinality: 0..1</p>
   */
  private Extension _city      ;
  /**
   * The name of the administrative area (county).
   * <p>Cardinality: 0..1</p>
   */
  private String district;
  /**
   * Extension for district
   * <p>Cardinality: 0..1</p>
   */
  private Extension _district      ;
  /**
   * Sub-unit of a country with limited sovereignty in a federally organized country. A code may be used
   * if codes are in common use (e.g. US 2 letter state codes).
   * <p>Cardinality: 0..1</p>
   */
  private String state;
  /**
   * Extension for state
   * <p>Cardinality: 0..1</p>
   */
  private Extension _state      ;
  /**
   * A postal code designating a region defined by the postal patients.service.
   * <p>Cardinality: 0..1</p>
   */
  private String postalCode;
  /**
   * Extension for postalCode
   * <p>Cardinality: 0..1</p>
   */
  private Extension _postalCode      ;
  /**
   * Country - a nation as commonly understood or generally accepted.
   * <p>Cardinality: 0..1</p>
   */
  private String country;
  /**
   * Extension for country
   * <p>Cardinality: 0..1</p>
   */
  private Extension _country      ;
  /**
   * Time period when address was/is in use.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;

  public Extension get_use() {
    return _use;
  }

  public void set_use(Extension _use) {
    this._use = _use;
  }

  public Extension get_type() {
    return _type;
  }

  public void set_type(Extension _type) {
    this._type = _type;
  }

  public Extension get_text() {
    return _text;
  }

  public void set_text(Extension _text) {
    this._text = _text;
  }

  public List<Extension> get_line() {
    return _line;
  }

  public void set_line(List<Extension> _line) {
    this._line = _line;
  }

  public Extension get_city() {
    return _city;
  }

  public void set_city(Extension _city) {
    this._city = _city;
  }

  public Extension get_district() {
    return _district;
  }

  public void set_district(Extension _district) {
    this._district = _district;
  }

  public Extension get_state() {
    return _state;
  }

  public void set_state(Extension _state) {
    this._state = _state;
  }

  public Extension get_postalCode() {
    return _postalCode;
  }

  public void set_postalCode(Extension _postalCode) {
    this._postalCode = _postalCode;
  }

  public Extension get_country() {
    return _country;
  }

  public void set_country(Extension _country) {
    this._country = _country;
  }

  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<String> getLine() {
    return line;
  }

  public void setLine(List<String> line) {
    this.line = line;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }
}
