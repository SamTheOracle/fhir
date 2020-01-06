package com.oracolo.fhir.handlers.validator;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ObservationValidator extends BaseValidator implements ValidationHandler {

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
