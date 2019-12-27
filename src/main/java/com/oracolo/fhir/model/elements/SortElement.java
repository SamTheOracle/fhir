package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;

/**
 * Specifies the order of the results to be returned.
 */
public class SortElement extends Element {
  /**
   * The attribute of the sort. The specified path must be resolvable from the type of the required data.
   * The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x])
   * to traverse multiple-cardinality sub-elements. Note that the index must be an integer constant.
   * <p>Cardinality: 1..1</p>
   */
  private String path;
  /**
   * Extension for path
   * <p>Cardinality: 0..1</p>
   */
  private Extension _path;
  /**
   * The direction of the sort, ascending or descending.
   * <p>See http://hl7.org/fhir/valueset-sort-direction.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String direction;
  /**
   * Extension for direction
   * <p>Cardinality: 0..1</p>
   */
  private Extension _direction;

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

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public Extension get_direction() {
    return _direction;
  }

  public void set_direction(Extension _direction) {
    this._direction = _direction;
  }
}
