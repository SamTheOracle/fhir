package model.elements;

import model.Element;
import model.datatypes.Coding;

import java.util.List;

/**
 * Code filters specify additional constraints on the data, specifying the value set of interest for
 * a particular element of the data. Each code filter defines an additional constraint on the data, i.e. code filters are AND'ed,
 * not OR'ed.
 */
public class CodeFilterElement extends Element {
  /**
   * The code-valued attribute of the filter. The specified path SHALL be a FHIRPath resolveable on the specified type of the
   * DataRequirement, and SHALL consist only of identifiers, constant indexers, and .resolve(). The path is allowed to contain
   * qualifiers (.) to traverse sub-elements, as well as indexers ([x]) to traverse multiple-cardinality sub-elements (see the Simple FHIRPath Profile for full details). Note that the index must be an integer constant. The path must resolve to an element of type code, Coding, or CodeableConcept.
   * <p>Cardinality: 0..1</p>
   */
  private String path;
  /**
   * Extension for path
   * <p>Cardinality: 0..1</p>
   */
  private Extension _path;
  /**
   * A token parameter that refers to a search parameter defined on the specified type of the DataRequirement,
   * and which searches on elements of type code, Coding, or CodeableConcept.
   * <p>Cardinality: 0..1</p>
   */
  private String searchParam;
  /**
   * Extension for searchParam
   * <p>Cardinality: 0..1</p>
   */
  private Extension _searchParam;
  /**
   * The valueset for the code filter. The valueSet and code elements are additive. If valueSet is specified,
   * the filter will return only those data items for which the value of the code-valued element specified in the path is a
   * member of the specified valueset.
   * <p>Cardinality: 0..1</p>
   */
  private String valueSet;
  /**
   * Extension for valueSet
   * <p>Cardinality: 0..1</p>
   */
  private Extension _valueSet;

  /**
   * The codes for the code filter. If values are given, the filter will return only those data items for which
   * the code-valued attribute specified by the path has a value that is one of the specified codes. If codes are
   * specified in addition to a value set, the filter returns items matching a code in the value set or one of the specified codes.
   * <p>Cardinality: 0..*</p>
   */
  private List<Coding> code;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Extension get_path() {
    return _path;
  }

  public void set_path(Extension _path) {
    this._path = _path;
  }

  public String getSearchParam() {
    return searchParam;
  }

  public void setSearchParam(String searchParam) {
    this.searchParam = searchParam;
  }

  public Extension get_searchParam() {
    return _searchParam;
  }

  public void set_searchParam(Extension _searchParam) {
    this._searchParam = _searchParam;
  }

  public String getValueSet() {
    return valueSet;
  }

  public void setValueSet(String valueSet) {
    this.valueSet = valueSet;
  }

  public Extension get_valueSet() {
    return _valueSet;
  }

  public void set_valueSet(Extension _valueSet) {
    this._valueSet = _valueSet;
  }

  public List<Coding> getCode() {
    return code;
  }

  public void setCode(List<Coding> code) {
    this.code = code;
  }
}
