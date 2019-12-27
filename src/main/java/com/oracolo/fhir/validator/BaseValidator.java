package com.oracolo.fhir.validator;

import io.vertx.core.json.JsonObject;
import model.exceptions.NotValidFhirResourceException;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import utils.FhirUtils;
import utils.ValidationHandler;

import javax.json.JsonReader;
import java.io.*;
import java.util.Objects;

public abstract class BaseValidator implements Validator {


  @Override
  public boolean validateAgainstJsonSchema(JsonObject jsonObject) {
    JsonValidationService jsonValidationService = JsonValidationService.newInstance();
    try {

      InputStream inputStream = new BufferedInputStream(new FileInputStream(Objects.requireNonNull(FhirUtils.class.getClassLoader()
        .getResource("fhir.schema.json")).getFile()));

      JsonSchema jsonSchema = jsonValidationService.readSchema(inputStream);
      InputStream json = new ByteArrayInputStream(jsonObject.encode().getBytes());
      ValidationHandler validationHandler = new ValidationHandler();
      JsonReader reader = jsonValidationService.createReader(json, jsonSchema, validationHandler);

      reader.readValue();
      validationHandler.checkProblems();
    } catch (FileNotFoundException | NotValidFhirResourceException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
