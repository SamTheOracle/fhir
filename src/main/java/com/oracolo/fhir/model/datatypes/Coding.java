package com.oracolo.fhir.model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A reference to a code defined by a terminology system.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coding {
  /**
   * The identification of the code system that defines the meaning of the symbol in the code, via uri.
   * <p>Cardinality: 0..1</p>
   */
  private String system;
  /**
   * The version of the code system which was used when choosing this code.
   * Note that a well-maintained code system does not need the version reported, because the meaning of codes is consistent across
   * versions. However this cannot consistently be assured, and when the meaning is not guaranteed to be consistent,
   * the version SHOULD be exchanged.
   * <p>Cardinality: 0..1</p>
   */
  private String version;
  /**
   * A symbol in syntax defined by the system. The symbol may be a predefined code or an expression
   * in a syntax defined by the coding system (e.g. post-coordination).
   * <p>See code in http://hl7.org/fhir/datatypes.html#code</p>
   * <p>Cardinality: 0..1</p>
   */
  private String code;
  /**
   * A representation of the meaning of the code in the system, following the rules of the system.
   * <p>Cardinality: 0..1</p>
   */
  private String display;
  /**
   * Indicates that this coding was chosen by a user directly - e.g. off a pick list of available items (codes or displays).
   * <p>Cardinality: 0..1</p>
   */
  private boolean userSelected;

  /**
   * The identification of the code system that defines the meaning of the symbol in the code, via uri.
   * <p>Cardinality: 0..1</p>
   *
   * @return A String
   */
  public String getSystem() {
    return system;
  }

  /**
   * The identification of the code system that defines the meaning of the symbol in the code, via uri.
   * <p>Cardinality: 0..1</p>
   *
   * @param system The system
   * @return a reference to this
   */
  public Coding setSystem(String system) {
    this.system = system;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * A symbol in syntax defined by the system. The symbol may be a predefined code or an expression
   * in a syntax defined by the coding system (e.g. post-coordination).
   * <p>See code in http://hl7.org/fhir/datatypes.html#code</p>
   * <p>Cardinality: 0..1</p>
   */
  public String getCode() {
    return code;
  }

  /**
   * A symbol in syntax defined by the system. The symbol may be a predefined code or an expression
   * in a syntax defined by the coding system (e.g. post-coordination).
   * <p>See code in http://hl7.org/fhir/datatypes.html#code</p>
   * <p>Cardinality: 0..1</p>
   */
  public Coding setCode(String code) {
    this.code = code;
    return this;
  }

  public String getDisplay() {
    return display;
  }

  public Coding setDisplay(String display) {
    this.display = display;
    return this;
  }

  public boolean isUserSelected() {
    return userSelected;
  }

  public Coding setUserSelected(boolean userSelected) {
    this.userSelected = userSelected;
    return this;
  }
}
