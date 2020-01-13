package com.oracolo.fhir.handlers.validator;

import com.oracolo.fhir.model.FhirResourceAbstract;
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

  //  private JsonSchema jsonSchema;
  private static BaseValidator instance;
  private JsonSchema jsonSchema;

  private BaseValidator() {
  }

  static BaseValidator getInstance() {
    if (instance == null) {
      instance = new BaseValidator();
    }
    return instance;
  }

  @Override
  public boolean validateAgainstJsonSchema(JsonObject jsonObject) {

    JsonValidationService jsonValidationService = JsonValidationService.newInstance();
    try {
      if (jsonSchema == null) {
        System.out.println("loading only once");
        InputStream inputStream = new BufferedInputStream(new FileInputStream(Objects.requireNonNull(FhirValidationProblemHandler.class.getClassLoader()
          .getResource("fhir.schema.json")).getFile()));
        jsonSchema = jsonValidationService.readSchema(inputStream);
      }
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
  public boolean validateAgainstClass(JsonObject jsonObject, Class<? extends FhirResourceAbstract> clazz) {
    try {
      Json.decodeValue(jsonObject.encode(), clazz);
    } catch (DecodeException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
