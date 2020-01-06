package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.exceptions.NotValidFhirResourceException;
import com.oracolo.fhir.model.exceptions.WrongDomainResourceForMethodException;
import com.oracolo.fhir.utils.ResponseFormat;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public abstract class BaseOperationHandler implements OperationHandler {
  protected HttpServerResponse serverResponse;

  protected Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  protected ResponseFormat responseFormat;
  protected DatabaseService service;

  private ValidationHandler validationHandler;

  public BaseOperationHandler(ValidationHandler validator) {
    this.validationHandler = validator;
    this.responseFormat = new ResponseFormat();
  }

  public BaseOperationHandler() {
  }

  @Override
  public OperationHandler setService(DatabaseService service) {
    this.service = service;
    return this;
  }

  @Override
  public OperationHandler setValidationHandler(ValidationHandler validationHandler) {
    this.validationHandler = validationHandler;
    return this;
  }

  @Override
  public OperationHandler setResponse(HttpServerResponse response) {
    this.serverResponse = response;
    return this;
  }

  @Override
  public OperationHandler validate(JsonObject jsonObject) {
    if (!validationHandler.validateAgainstJsonSchema(jsonObject)) {
      httpServerResponsePromise.fail(new NotValidFhirResourceException("Not valid resource"));
    }
    return this;
  }

  @Override
  public OperationHandler validateAgainstClass(JsonObject jsonObject) {
    if (!validationHandler.validateAgainstClass(jsonObject)) {
      httpServerResponsePromise.fail(new WrongDomainResourceForMethodException("Not correct resource type for method"));
    }
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
  public HttpServerResponse release() {
    return serverResponse;
  }

  @Override
  public void reset() {
    serverResponse = null;
    httpServerResponsePromise = Promise.promise();
  }
}
