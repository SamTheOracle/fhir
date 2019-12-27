package com.oracolo.fhir.validator;

import io.vertx.core.json.JsonObject;

public interface Validator {

  static Validator createPatientValidator() {
    return new PatientValidator();
  }

  static Validator createObservationValidator() {
    return new ObservationValidator();
  }

  static Validator createConditionValidator() {
    return new ConditionValidator();
  }

  boolean validateAgainstJsonSchema(JsonObject jsonObject);

  boolean validateAgainstClass(JsonObject jsonObject);
}
