package com.oracolo.fhir.validator;

import com.oracolo.fhir.model.domain.Patient;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class PatientValidator extends BaseValidator implements Validator {

  @Override
  public boolean validateAgainstClass(JsonObject jsonObject) {
    try {
      Json.decodeValue(jsonObject.encode(), Patient.class);
    } catch (DecodeException e) {
      return false;
    }
    return true;
  }
}
