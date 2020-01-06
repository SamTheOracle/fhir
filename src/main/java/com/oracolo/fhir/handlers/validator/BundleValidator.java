package com.oracolo.fhir.handlers.validator;

import com.oracolo.fhir.model.resources.Bundle;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class BundleValidator extends BaseValidator implements ValidationHandler {

  @Override
  public boolean validateAgainstClass(JsonObject jsonObject) {
    try {
      Json.decodeValue(jsonObject.encode(), Bundle.class);
    } catch (DecodeException e) {
      return false;
    }
    return true;
  }
}
