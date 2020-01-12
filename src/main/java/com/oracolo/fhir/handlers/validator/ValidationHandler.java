package com.oracolo.fhir.handlers.validator;

import com.oracolo.fhir.model.FhirResourceAbstract;
import io.vertx.core.json.JsonObject;

public interface ValidationHandler {


  static ValidationHandler createValidator() {
    return new BaseValidator();
  }

  boolean validateAgainstJsonSchema(JsonObject jsonObject);

  boolean validateAgainstClass(JsonObject jsonObject, Class<? extends FhirResourceAbstract> clazz);
}
