package com.oracolo.fhir.handlers.response;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.response.format.FormatHandler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;

public abstract class BaseResponseHandler implements ResponseHandler {
  protected Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  protected FormatHandler responseFormat;
  protected DatabaseService service;

  @Override
  public ResponseHandler withService(DatabaseService service) {
    this.service = service;
    return this;
  }


  @Override
  public ResponseHandler withFormatHandler(FormatHandler responseFormat) {
    this.responseFormat = responseFormat;
    return this;
  }

  @Override
  public Promise<HttpServerResponse> releaseAsync() {
    return httpServerResponsePromise;
  }
}
