package com.oracolo.fhir.validator;

import com.oracolo.fhir.model.exceptions.NotValidFhirResourceException;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ValidationHandler;
import io.vertx.core.json.JsonObject;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;

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
