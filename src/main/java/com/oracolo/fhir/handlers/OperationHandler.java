package com.oracolo.fhir.handlers;

import com.oracolo.fhir.database.DatabaseService;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import utils.FhirHttpHeader;
import utils.FhirQueryParameter;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

  OperationHandler setResponse(HttpServerResponse response);

  OperationHandler validate(JsonObject jsonObject);

  OperationHandler executeDatabaseOperation(DatabaseService service, Consumer<DatabaseService> databaseServiceConsumerCommand);

  OperationHandler executeDatabaseOperation(DatabaseService service, Promise<JsonObject> jsonObjectPromise, BiConsumer<Promise<JsonObject>, DatabaseService> databaseServiceBiConsumer);

  OperationHandler withQueryParameters(List<FhirQueryParameter> queryParameters);

  OperationHandler withQueryParameter(FhirQueryParameter queryParameter);

  OperationHandler withHeader(FhirHttpHeader fhirHttpHeader);

  OperationHandler withHeaders(List<FhirHttpHeader> fhirHttpHeaders);

  OperationHandler writeResponseBody(JsonObject domainResourceJsonObject);

  /**
   * Writes the body on the response if a previous database command has been used and
   * the http response is set
   *
   * @return
   */
  OperationHandler writeResponseBody();

  void reset();

  Promise<HttpServerResponse> releaseAsync();

  HttpServerResponse release();

}
