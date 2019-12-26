package com.oracolo.fhir.handlers;

import com.oracolo.fhir.database.DatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.elements.Metadata;
import model.exceptions.NotValideFhirResourceException;
import utils.FhirHttpHeader;
import utils.FhirHttpHeaderValues;
import utils.FhirQueryParameter;
import utils.FhirUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ReadOperationHandler implements OperationHandler {
  private HttpServerResponse serverResponse;
  private List<FhirHttpHeader> httpHeaders;
  private List<FhirQueryParameter> queryParameters;
  private Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  private Promise<JsonObject> domainResourceJsonObjectPromise;


  public ReadOperationHandler() {
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
    Metadata metadata = Json.decodeValue(domainResource.getJsonObject("meta").encode(), Metadata.class);
    String lastModified = metadata.getLastUpdated().toString();
    String versionId = metadata.getVersionId();
    //general parameter to be supported
    serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
    serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
    Optional<String> fhirHttpHeader = httpHeaders
      .stream()
      .filter(httpHeader -> httpHeader.name().equals(HttpHeaderNames.ACCEPT.toString()))
      .map(FhirHttpHeader::value)
      .findFirst();
    String domainResourceString = domainResource.encodePrettily();
    String length = String.valueOf(domainResourceString.getBytes(Charset.defaultCharset()).length);
    serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, fhirHttpHeader.orElse(FhirHttpHeaderValues.APPLICATION_JSON));
    serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
    serverResponse.setStatusCode(HttpResponseStatus.OK.code());
    serverResponse.write(domainResourceString);

    return this;
  }

  @Override
  public OperationHandler writeResponseBody() {

    domainResourceJsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        Metadata metadata = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class);
        String lastModified = metadata.getLastUpdated().toString();
        String versionId = metadata.getVersionId();
        //general parameter to be supported
        serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
        serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
        Optional<String> fhirHttpHeader = httpHeaders
          .stream()
          .filter(httpHeader -> httpHeader.name().equals(HttpHeaderNames.ACCEPT.toString()))
          .map(FhirHttpHeader::value)
          .findFirst();
        String domainResourceString = jsonObject.encodePrettily();
        String length = String.valueOf(domainResourceString.getBytes(Charset.defaultCharset()).length);
        serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, fhirHttpHeader.orElse(FhirHttpHeaderValues.APPLICATION_JSON));
        serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
        serverResponse.setStatusCode(HttpResponseStatus.OK.code());
        serverResponse.write(domainResourceString);
        httpServerResponsePromise.complete(serverResponse);
      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

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
