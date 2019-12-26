package com.oracolo.fhir.handlers;

import com.oracolo.fhir.database.DatabaseService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import model.exceptions.NotValideFhirResourceException;
import utils.FhirHttpHeader;
import utils.FhirQueryParameter;
import utils.FhirUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DeleteOperationHandler implements OperationHandler {
  private HttpServerResponse serverResponse;
  private List<FhirHttpHeader> httpHeaders;
  private List<FhirQueryParameter> queryParameters;
  private Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  private Promise<JsonObject> domainResourceJsonObjectPromise;


  public DeleteOperationHandler() {
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
    try {

      FhirUtils.validateJsonAgainstSchema(jsonObject);

    } catch (NotValideFhirResourceException e) {
      e.printStackTrace();
      httpServerResponsePromise.fail("Not a valid Fhir Resource");

    }
    return this;
  }

  @Override
  public OperationHandler executeDatabaseOperation(DatabaseService service, Consumer<DatabaseService> databaseServiceConsumerCommand) {
    databaseServiceConsumerCommand.accept(service);
    return this;
  }

  @Override
  public OperationHandler executeDatabaseOperation(DatabaseService service, Promise<JsonObject> jsonObjectPromise, BiConsumer<Promise<JsonObject>, DatabaseService> databaseServiceBiConsumer) {
    databaseServiceBiConsumer.accept(jsonObjectPromise, service);
    domainResourceJsonObjectPromise = jsonObjectPromise;
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
  public OperationHandler writeResponseBody(JsonObject domainResource) {

    domainResourceJsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        serverResponse.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        httpServerResponsePromise.complete(serverResponse);
      })
      .onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }

  @Override
  public OperationHandler writeResponseBody() {

    domainResourceJsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        serverResponse.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        httpServerResponsePromise.complete(serverResponse);
      })
      .onFailure(throwable -> httpServerResponsePromise.fail(throwable));

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
  }
}
