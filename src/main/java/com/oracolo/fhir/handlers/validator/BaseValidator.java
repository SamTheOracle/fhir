package com.oracolo.fhir.handlers.validator;

import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.exceptions.NotValidFhirResourceException;
import com.oracolo.fhir.utils.FhirValidationProblemHandler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;

import javax.json.JsonReader;
import java.io.*;
import java.util.Objects;

public class BaseValidator implements ValidationHandler {


  @Override
  public boolean validateAgainstJsonSchema(JsonObject jsonObject) {
    JsonValidationService jsonValidationService = JsonValidationService.newInstance();
    try {

      InputStream inputStream = new BufferedInputStream(new FileInputStream(Objects.requireNonNull(FhirValidationProblemHandler.class.getClassLoader()
        .getResource("fhir.schema.json")).getFile()));
      JsonSchema jsonSchema = jsonValidationService.readSchema(inputStream);
      InputStream json = new ByteArrayInputStream(jsonObject.encode().getBytes());
      FhirValidationProblemHandler validationHandler = new FhirValidationProblemHandler();

      JsonReader reader = jsonValidationService.createReader(json, jsonSchema, validationHandler);

      reader.readValue();

      validationHandler.checkProblems();
    } catch (FileNotFoundException | NotValidFhirResourceException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean validateAgainstClass(JsonObject jsonObject) {
    try {
      Json.decodeValue(jsonObject.encode(), DomainResource.class);
    } catch (DecodeException e) {
      return false;
    }
    return true;
  }
}
