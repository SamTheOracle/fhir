package model.elements;

public enum ExpressionLanguage {
  TEXT_FHIRPATH("text/fhirpath");
  //should be filled in with values at  http://hl7.org/fhir/valueset-trigger-type.html

  private String display;

  ExpressionLanguage(String display) {
    this.display = display;
  }

  public String display() {
    return display;
  }


}
