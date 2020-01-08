package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.ResponseFormat;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

//TO-DO
public class SearchOperationHandler extends BaseOperationHandler implements OperationHandler {


  public SearchOperationHandler() {
  }



  /**
   * Executes the database service commands and write response body
   *
   * @param databaseServiceConsumer
   * @return
   */
  @Override
  public OperationHandler createResponseAsync(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {
    Promise<JsonObject> promise = Promise.promise();
    databaseServiceConsumer.accept(service, promise);
    promise
      .future()
      .onSuccess(jsonObject -> {
        Bundle bundle = Json.decodeValue(jsonObject.encodePrettily(), Bundle.class);
        bundle.setType(Bundle.BundleTypeCodes.SEARCHSET.code());
        ResponseFormat responseFormat = super.responseFormat.format(JsonObject.mapFrom(bundle));
        FhirHttpHeader header = responseFormat.contentType();
        String response = responseFormat.response();
        serverResponse
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.getBytes(Charset.defaultCharset()).length))
          .putHeader(header.name(), header.value())
          .write(response);
        httpServerResponsePromise.complete(serverResponse);

      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }

}
