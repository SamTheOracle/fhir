package model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.elements.CodeableConcept;
import model.elements.Extension;

import java.util.List;

/**
 * An error, warning, or information message that results from a system action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationOutcomeIssue {
  /**
   * Indicates whether the issue indicates a variation from successful processing.
   * <p>See https://www.hl7.org/fhir/valueset-issue-severity.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String severity;
  /**
   * Extension for severity
   * <p>Cardinality: 0..1</p>
   */
  private Extension _severity;
  /**
   * Describes the type of the issue. The system that creates an OperationOutcome SHALL choose the most applicable code
   * from the IssueType value set, and may additional provide its own code for the error in the details element.
   * <p>See https://www.hl7.org/fhir/valueset-issue-type.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String code;
  /**
   * Extension for code
   * <p>Cardinality: 0..1</p>
   */
  private Extension _code;
  /**
   * Additional details about the error. This may be a text description of the error or a system code that identifies the error.
   * <p>See examples https://www.hl7.org/fhir/terminologies.html#example</p>
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept details;
  /**
   * Additional diagnostic information about the issue.
   * <p>Cardinality: 0..1</p>
   */
  private String diagnostics;
  /**
   * Extension for diagnostics
   * <p>Cardinality: 0..1</p>
   */
  private Extension _diagnostics;
  /**
   * A simple subset of FHIRPath limited to element names, repetition indicators and the default child accessor
   * that identifies one of the elements in the resource that caused this issue to be raised.
   * <p>Cardinality: 0..*</p>
   */
  private List<String> expression;
  /**
   * Extension for expression
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _expression;

  public String getSeverity() {
    return severity;
  }

  public OperationOutcomeIssue setSeverity(String severity) {
    this.severity = severity;
    return this;
  }

  public Extension get_severity() {
    return _severity;
  }

  public void set_severity(Extension _severity) {
    this._severity = _severity;
  }

  public String getCode() {
    return code;
  }

  public OperationOutcomeIssue setCode(String code) {
    this.code = code;
    return this;
  }

  public Extension get_code() {
    return _code;
  }

  public void set_code(Extension _code) {
    this._code = _code;
  }

  public CodeableConcept getDetails() {
    return details;
  }

  public void setDetails(CodeableConcept details) {
    this.details = details;
  }

  public String getDiagnostics() {
    return diagnostics;
  }

  public OperationOutcomeIssue setDiagnostics(String diagnostics) {
    this.diagnostics = diagnostics;
    return this;
  }

  public Extension get_diagnostics() {
    return _diagnostics;
  }

  public void set_diagnostics(Extension _diagnostics) {
    this._diagnostics = _diagnostics;
  }

  public List<String> getExpression() {
    return expression;
  }

  public void setExpression(List<String> expression) {
    this.expression = expression;
  }

  public List<Extension> get_expression() {
    return _expression;
  }

  public void set_expression(List<Extension> _expression) {
    this._expression = _expression;
  }
}
