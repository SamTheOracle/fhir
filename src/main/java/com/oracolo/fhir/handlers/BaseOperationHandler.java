package com.oracolo.fhir.handlers;

import com.oracolo.fhir.model.exceptions.NotValidFhirResourceException;
import com.oracolo.fhir.model.exceptions.WrongDomainResourceForMethodException;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirQueryParameter;
import com.oracolo.fhir.validator.Validator;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseOperationHandler implements OperationHandler {
  protected HttpServerResponse serverResponse;
  protected List<FhirHttpHeader> httpHeaders;
  protected List<FhirQueryParameter> queryParameters;
  protected Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  private Validator validator;

  public BaseOperationHandler(Validator validator) {
    httpHeaders = new ArrayList<>();
    queryParameters = new ArrayList<>();
    this.validator = validator;
  }

  public BaseOperationHandler() {
    httpHeaders = new ArrayList<>();
    queryParameters = new ArrayList<>();
  }

  @Override
  public OperationHandler setResponse(HttpServerResponse response) {
    this.serverResponse = response;
    return this;
  }

  @Override
  public OperationHandler validate(JsonObject jsonObject) {
    if (!validator.validateAgainstJsonSchema(jsonObject)) {
      httpServerResponsePromise.fail(new NotValidFhirResourceException("Not valid resource"));
    }
    return this;
  }

  @Override
  public OperationHandler validateAgainstClass(JsonObject jsonObject) {
    if (!validator.validateAgainstClass(jsonObject)) {
      httpServerResponsePromise.fail(new WrongDomainResourceForMethodException("Not correct resource type for method"));
    }
    return this;
  }

  @Override
  public OperationHandler withQueryParameters(List<FhirQueryParameter> queryParameters) {
    this.queryParameters = queryParameters;
    return this;
  }

  @Override
  public OperationHandler withQueryParameter(FhirQueryParameter queryParameter) {
    this.queryParameters.add(queryParameter);
    return this;
  }

  /**
   * Used to add location, Accept and Prefer header that might be useful
   *
   * @param fhirHttpHeader
   * @return
   */
  @Override
  public OperationHandler withHeader(FhirHttpHeader fhirHttpHeader) {
    httpHeaders.add(fhirHttpHeader);
    return this;
  }

  @Override
  public OperationHandler withHeaders(List<FhirHttpHeader> fhirHttpHeaders) {
    this.httpHeaders = fhirHttpHeaders;
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
    httpHeaders = new ArrayList<>();
    queryParameters = new ArrayList<>();
    httpServerResponsePromise = Promise.promise();
  }
}
