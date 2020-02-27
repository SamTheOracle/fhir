package com.oracolo.fhir.handlers.response;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.response.format.FormatHandler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.function.BiConsumer;

public interface ResponseHandler {


  static ResponseHandler deleteResponseHandler() {
    return new DeleteResponseHandler();
  }

  static ResponseHandler searchResponseHandler() {
    return new SearchResponseHandler();
  }

  static ResponseHandler updateResponseHandler() {
    return new UpdateResponseHandler();
  }

  static ResponseHandler readResponseHandler() {
    return new ReadResponseHandler();
  }

  static ResponseHandler createResponseHandler() {
    return new CreateResponseHandler();
  }

  ResponseHandler withFormatHandler(FormatHandler responseFormat);

  ResponseHandler createResponseAsync(HttpServerResponse response, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer);

  ResponseHandler withService(DatabaseService service);

  Promise<HttpServerResponse> releaseAsync();


}
