package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.utils.ResponseFormat;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.function.BiConsumer;

public interface OperationHandler {


  static OperationHandler createUpdateCreateOperationHandler() {
    return new CreateUpdateOperationHandler();
  }


  static OperationHandler createReadOperationHandler() {
    return new ReadOperationHandler();
  }


  static OperationHandler createDeleteOperationHandler() {
    return new DeleteOperationHandler();
  }

  static OperationHandler createSearchOperationHandler() {
    return new SearchOperationHandler();
  }


  OperationHandler withResponseFormat(ResponseFormat responseFormat);


  /**
   * Executes the database service commands and write response body
   *
   * @param databaseServiceConsumer
   * @return
   */
  OperationHandler createResponseAsync(HttpServerResponse response, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer);


  OperationHandler setService(DatabaseService service);


  void reset();

  Promise<HttpServerResponse> releaseAsync();





}
