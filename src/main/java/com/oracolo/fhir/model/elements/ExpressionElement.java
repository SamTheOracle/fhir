package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;

/**
 * A expression that is evaluated in a specified context and returns a value.
 * The context of use of the expression must specify the context in which the expression is evaluated, and how the result of the expression is used.
 * <p>Cardinality: 0..*</p>
 */
public class ExpressionElement extends Element {
  /**
   * A brief, natural language description of the condition that effectively communicates the intended semantics.
   * <p>Cardinality: 0..1</p>
   */
  private String description;
  /**
   * Extension for description
   * <p>Cardinality: 0..1</p>
   */
  private Extension _description;
  /**
   * A short name assigned to the expression to allow for multiple reuse of the expression in the context where it is defined.
   * <p>Cardinality: 0..1</p>
   */
  private String name;
  /**
   * Extension for name
   * <p>Cardinality: 0..1</p>
   */
  private Extension _name;
  /**
   * The media type of the language for the expression.
   * <p>See http://hl7.org/fhir/valueset-expression-language.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String language;
  /**
   * Extension for language
   * <p>Cardinality: 1..1</p>
   */
  private Extension _language;
  /**
   * An expression in the specified language that returns a value.
   * <p>Cardinality: 0..1</p>
   */
  private String expression;
  /**
   * Extension for expression;
   * <p>Cardinality: 0..1</p>
   */
  private Extension _expression;
  /**
   * A URI that defines where the expression is found.
   * <p>Cardinality: 0..1</p>
   */
  private String reference;
  /**
   * Extension for reference;
   * <p>Cardinality: 0..1</p>
   */
  private Extension _reference;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Extension get_description() {
    return _description;
  }

  public void set_description(Extension _description) {
    this._description = _description;
  }

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

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Extension get_language() {
    return _language;
  }

  public void set_language(Extension _language) {
    this._language = _language;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Extension get_expression() {
    return _expression;
  }

  public void set_expression(Extension _expression) {
    this._expression = _expression;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Extension get_reference() {
    return _reference;
  }

  public void set_reference(Extension _reference) {
    this._reference = _reference;
  }
}
