package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;
import com.oracolo.fhir.model.datatypes.*;

/**
 * Every element in a resource or data type includes an optional "extension" child element
 * that may be present any number of times.
 *
 */
public class Extension extends Element {


  /**
   * Source of the definition for the extension code - a logical name or a URL.
   * <p>Cardinality: 1..1</p>
   */
  private String url;
  /**
   * Value of extension - must be one of a constrained set of the data types.
   * <p>See http://hl7.org/fhir/extensibility.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String value;

  private String valueBase64Binary,valueBoolean,valueCanonical,valueCode,valueDate,valueDateTime,valueDecimal,valueId,valueInstant,valueInteger,valueMarkdown,valueOid,valuePositiveInt,valueString,valueTime,valueUnsignedInt,valueUri,valueUrl,valueUuid;
  private Address valueAddress;
  private CodeableConcept valueCodeableConcept;
  private Coding valueCoding;
  private ContactPoint valueContactPoint;
  private HumanName valueHumanName;
  private Identifier valueIdentifier;

  public String getValueBase64Binary() {
    return valueBase64Binary;
  }

  public void setValueBase64Binary(String valueBase64Binary) {
    this.valueBase64Binary = valueBase64Binary;
  }

  public String getValueBoolean() {
    return valueBoolean;
  }

  public void setValueBoolean(String valueBoolean) {
    this.valueBoolean = valueBoolean;
  }

  public String getValueCanonical() {
    return valueCanonical;
  }

  public void setValueCanonical(String valueCanonical) {
    this.valueCanonical = valueCanonical;
  }

  public String getValueCode() {
    return valueCode;
  }

  public void setValueCode(String valueCode) {
    this.valueCode = valueCode;
  }

  public String getValueDate() {
    return valueDate;
  }

  public void setValueDate(String valueDate) {
    this.valueDate = valueDate;
  }

  public String getValueDateTime() {
    return valueDateTime;
  }

  public void setValueDateTime(String valueDateTime) {
    this.valueDateTime = valueDateTime;
  }

  public String getValueDecimal() {
    return valueDecimal;
  }

  public void setValueDecimal(String valueDecimal) {
    this.valueDecimal = valueDecimal;
  }

  public String getValueId() {
    return valueId;
  }

  public void setValueId(String valueId) {
    this.valueId = valueId;
  }

  public String getValueInstant() {
    return valueInstant;
  }

  public void setValueInstant(String valueInstant) {
    this.valueInstant = valueInstant;
  }

  public String getValueInteger() {
    return valueInteger;
  }

  public void setValueInteger(String valueInteger) {
    this.valueInteger = valueInteger;
  }

  public String getValueMarkdown() {
    return valueMarkdown;
  }

  public void setValueMarkdown(String valueMarkdown) {
    this.valueMarkdown = valueMarkdown;
  }

  public String getValueOid() {
    return valueOid;
  }

  public void setValueOid(String valueOid) {
    this.valueOid = valueOid;
  }

  public String getValuePositiveInt() {
    return valuePositiveInt;
  }

  public void setValuePositiveInt(String valuePositiveInt) {
    this.valuePositiveInt = valuePositiveInt;
  }

  public String getValueString() {
    return valueString;
  }

  public void setValueString(String valueString) {
    this.valueString = valueString;
  }

  public String getValueTime() {
    return valueTime;
  }

  public void setValueTime(String valueTime) {
    this.valueTime = valueTime;
  }

  public String getValueUnsignedInt() {
    return valueUnsignedInt;
  }

  public void setValueUnsignedInt(String valueUnsignedInt) {
    this.valueUnsignedInt = valueUnsignedInt;
  }

  public String getValueUri() {
    return valueUri;
  }

  public void setValueUri(String valueUri) {
    this.valueUri = valueUri;
  }

  public String getValueUrl() {
    return valueUrl;
  }

  public void setValueUrl(String valueUrl) {
    this.valueUrl = valueUrl;
  }

  public String getValueUuid() {
    return valueUuid;
  }

  public void setValueUuid(String valueUuid) {
    this.valueUuid = valueUuid;
  }

  public Address getValueAddress() {
    return valueAddress;
  }

  public void setValueAddress(Address valueAddress) {
    this.valueAddress = valueAddress;
  }

  public CodeableConcept getValueCodeableConcept() {
    return valueCodeableConcept;
  }

  public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
    this.valueCodeableConcept = valueCodeableConcept;
  }

  public Coding getValueCoding() {
    return valueCoding;
  }

  public void setValueCoding(Coding valueCoding) {
    this.valueCoding = valueCoding;
  }

  public ContactPoint getValueContactPoint() {
    return valueContactPoint;
  }

  public void setValueContactPoint(ContactPoint valueContactPoint) {
    this.valueContactPoint = valueContactPoint;
  }

  public HumanName getValueHumanName() {
    return valueHumanName;
  }

  public void setValueHumanName(HumanName valueHumanName) {
    this.valueHumanName = valueHumanName;
  }

  public Identifier getValueIdentifier() {
    return valueIdentifier;
  }

  public void setValueIdentifier(Identifier valueIdentifier) {
    this.valueIdentifier = valueIdentifier;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
