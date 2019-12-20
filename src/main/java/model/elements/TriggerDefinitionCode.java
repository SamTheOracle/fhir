package model.elements;

public enum TriggerDefinitionCode {
  NAMED_EVENT("Named-Event");
  //should be filled in with values at  http://hl7.org/fhir/valueset-trigger-type.html

  private String display;

  TriggerDefinitionCode(String display) {
    this.display = display;
  }

  public String display() {
    return display;
  }


}
