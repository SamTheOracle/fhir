package com.oracolo.fhir.handlers.validator;

import com.oracolo.fhir.model.ResourceType;
import io.vertx.core.json.JsonObject;

public interface ValidationHandler {


  static ValidationHandler from(String resourceType) {

    ResourceType rT = ResourceType.valueOf(resourceType);
    switch (rT) {
      case Patient:
        return new PatientValidator();
      case Condition:
        return new ConditionValidator();
      case Observation:
        return new ObservationValidator();
      case Bundle:
        return new BundleValidator();
      default:
        return new BaseValidator();
    }

  }

  static ValidationHandler from(ResourceType resourceType) {
    switch (resourceType) {
      case Patient:
        return new PatientValidator();
      case Condition:
        return new ConditionValidator();
      case Observation:
        return new ObservationValidator();
      case Bundle:
        return new BundleValidator();
      default:
        return new BaseValidator();
    }
  }

  boolean validateAgainstJsonSchema(JsonObject jsonObject);

  boolean validateAgainstClass(JsonObject jsonObject);
}
