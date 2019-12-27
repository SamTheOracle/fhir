package com.oracolo.fhir.validator;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ConditionValidator extends BaseValidator implements Validator {

  @Override
  public boolean validateAgainstClass(JsonObject jsonObject) {
    try {
      Json.decodeValue(jsonObject.encode(), ObservationValidator.class);
    } catch (DecodeException e) {
      return false;
    }
    return true;
  }
}
