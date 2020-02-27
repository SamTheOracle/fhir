package com.oracolo.fhir.handlers.response;

import com.oracolo.fhir.database.DatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public class DeleteResponseHandler extends BaseResponseHandler implements ResponseHandler {


  public DeleteResponseHandler() {

  }


  @Override
  public ResponseHandler createResponseAsync(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {

    Promise<JsonObject> jsonObjectPromise = Promise.promise();
    databaseServiceConsumer.accept(service, jsonObjectPromise);
    jsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        String length = String.valueOf("".getBytes(Charset.defaultCharset()).length);
        serverResponse
          .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .write("");
      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }


}
