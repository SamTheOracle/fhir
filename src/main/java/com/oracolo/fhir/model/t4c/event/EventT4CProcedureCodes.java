package com.oracolo.fhir.model.t4c.event;

public enum EventT4CProcedureCodes {
  INTUBATION("intubation"),
  DRAINAGE("drainage"),
  CHEST_TUBE("chest-tube"),
  START("start"),
  END("end");
  private String procedureId;

  EventT4CProcedureCodes(String procedureId) {
    this.procedureId = procedureId;
  }

  public String display() {
    return this.procedureId;
  }

}
