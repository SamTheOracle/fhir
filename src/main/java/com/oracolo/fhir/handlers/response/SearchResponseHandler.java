package com.oracolo.fhir.handlers.response;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.response.format.Format;
import com.oracolo.fhir.model.resources.Bundle;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

//TO-DO
public class SearchResponseHandler extends BaseResponseHandler implements ResponseHandler {


  public SearchResponseHandler() {
  }


  /**
   * Executes the database service commands and write response body
   *
   * @param databaseServiceConsumer
   * @return
   */
  @Override
  public ResponseHandler createResponseAsync(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {
    Promise<JsonObject> promise = Promise.promise();
    databaseServiceConsumer.accept(service, promise);
    promise
      .future()
      .onSuccess(jsonObject -> {
        Bundle bundle = Json.decodeValue(jsonObject.encodePrettily(), Bundle.class);
        bundle.setType(Bundle.BundleTypeCodes.SEARCHSET.code());
        Format format = super.responseFormat.createFormat(JsonObject.mapFrom(bundle));
        String response = format.getResponse();
        String contentType = format.getContentType();
        serverResponse
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.getBytes(Charset.defaultCharset()).length))
          .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), contentType)
          .write(response);
        httpServerResponsePromise.complete(serverResponse);

      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }

}
