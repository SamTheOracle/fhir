package model.datatypes;

import model.Element;
import model.elements.Extension;

import java.util.ArrayList;
import java.util.List;

/**
 * A name of a human with text, parts and usage information.
 * <p>
 * Names may be changed or repudiated. People may have different names in different contexts.
 * Names may be divided into parts of different type that have variable significance depending on context, though the division
 * into parts is not always significant. With personal names, the different parts might or might not be imbued with some implicit meaning;
 * various cultures associate different importance with the name parts and the degree to which systems SHALL care about name parts around the world varies widely.
 * </p>
 */
public class HumanName extends Element {

  /**
   * Identifies the purpose for this name.
   * <p>Cardinality: 0..1</p>
   */
  private String use;
  /**
   * Extension for use
   * <p>Cardinality: 0..1</p>
   */
  private Extension _use;

  /**
   * Specifies the entire name as it should be displayed e.g. on an application UI.
   * This may be provided instead of or as well as the specific parts.
   * <p>Cardinality: 0..1</p>
   */
  private String text;
  /**
   * Extension for text
   * <p>Cardinality: 0..1</p>
   */
  private Extension _text;


  /**
   * The part of a name that links to the genealogy.
   * In some cultures (e.g. Eritrea) the family name of a son is the first name of his father.
   * <p>Cardinality: 0..1</p>
   */
  private String family;
  /**
   * Extension for family
   * <p>Cardinality: 0..1</p>
   */
  private Extension _family;

  /**
   * Given name. It must be ordered (e.g. first name => middle name 1 => middle name 2 etc.)
   * <p>Cardinality: 0..*</p>
   */
  private List<String> given;
  /**
   * Extension for given
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _given;

  /**
   * Part of the name that is acquired as a title due to academic, legal, employment or nobility status, etc.
   * and that appears at the start of the name.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> prefix;
  /**
   * Extension for prefix
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _prefix;
  /**
   * Part of the name that is acquired as a title due to academic, legal, employment or nobility status, etc.
   * and that appears at the end of the name.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> suffix;
  /**
   * Extension for suffix
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _suffix;

  /**
   * Indicates the period of time when this name was valid for the named person.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;

  public HumanName() {

  }

  public Extension get_use() {
    return _use;
  }

  public void set_use(Extension _use) {
    this._use = _use;
  }

  public Extension get_text() {
    return _text;
  }

  public void set_text(Extension _text) {
    this._text = _text;
  }

  public Extension get_family() {
    return _family;
  }

  public void set_family(Extension _family) {
    this._family = _family;
  }

  public List<Extension> get_given() {
    return _given;
  }

  public void set_given(List<Extension> _given) {
    this._given = _given;
  }

  public List<Extension> get_prefix() {
    return _prefix;
  }

  public void set_prefix(List<Extension> _prefix) {
    this._prefix = _prefix;
  }

  public List<Extension> get_suffix() {
    return _suffix;
  }

  public void set_suffix(List<Extension> _suffix) {
    this._suffix = _suffix;
  }

  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getFamily() {
    return family;
  }

  public HumanName setFamily(String family) {
    this.family = family;
    return this;
  }

  public List<String> getGiven() {
    return given;
  }

  public HumanName setGiven(List<String> given) {
    this.given = given;
    return this;
  }

  public List<String> getPrefix() {
    return prefix;
  }

  public void setPrefix(List<String> prefix) {
    this.prefix = prefix;
  }

  public List<String> getSuffix() {
    return suffix;
  }

  public void setSuffix(List<String> suffix) {
    this.suffix = suffix;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public HumanName addNewGiven(String given) {
    if (this.given == null) {
      this.given = new ArrayList<>();
    }
    this.given.add(given);
    return this;
  }
}
