package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.utils.ResponseFormat;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;

public abstract class BaseOperationHandler implements OperationHandler {
  protected Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  protected ResponseFormat responseFormat;
  protected DatabaseService service;


  public BaseOperationHandler() {
  }


  @Override
  public OperationHandler setService(DatabaseService service) {
    this.service = service;
    return this;
  }


  @Override
  public OperationHandler withResponseFormat(ResponseFormat responseFormat) {
    this.responseFormat = responseFormat;
    return this;
  }


  @Override
  public Promise<HttpServerResponse> releaseAsync() {
    return httpServerResponsePromise;
  }


  @Override
  public void reset() {
    httpServerResponsePromise = Promise.promise();
  }
}
