package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.utils.ResponseFormat;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.function.BiConsumer;

public interface OperationHandler {


  static OperationHandler createUpdateCreateOperationHandler(ValidationHandler validator) {
    return new CreateUpdateOperationHandler(validator);
  }

  static OperationHandler createUpdateCreateOperationHandler() {
    return new CreateUpdateOperationHandler();
  }

  static OperationHandler createReadOperationHandler(ValidationHandler validator) {
    return new ReadOperationHandler(validator);
  }

  static OperationHandler createReadOperationHandler() {
    return new ReadOperationHandler();
  }

  static OperationHandler createDeleteOperationHandler(ValidationHandler validator) {
    return new DeleteOperationHandler(validator);
  }

  static OperationHandler createDeleteOperationHandler() {
    return new DeleteOperationHandler();
  }

  static OperationHandler createSearchOperationHandler() {
    return new SearchOperationHandler();
  }

  OperationHandler setValidationHandler(ValidationHandler validationHandler);

  OperationHandler setResponse(HttpServerResponse response);

  OperationHandler validate(JsonObject jsonObject);

  OperationHandler withResponseFormat(ResponseFormat responseFormat);

  OperationHandler writeResponseBody(JsonObject domainResourceJsonObject);

  OperationHandler validateAgainstClass(JsonObject jsonObject);

  OperationHandler setService(DatabaseService service);

  /**
   * Executes the database service commands and write response body
   *
   * @param databaseServiceConsumer
   * @return
   */
  OperationHandler writeResponseBodyAsync(BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer);

  void reset();

  Promise<HttpServerResponse> releaseAsync();

  HttpServerResponse release();

}
