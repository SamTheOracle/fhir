package com.oracolo.fhir.handlers;

import com.oracolo.fhir.database.DatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.elements.Metadata;
import model.exceptions.NotValidFhirResourceException;
import utils.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CreateUpdateOperationHandler implements OperationHandler {
  private HttpServerResponse serverResponse;
  private List<FhirHttpHeader> httpHeaders;
  private List<FhirQueryParameter> queryParameters;
  private Promise<HttpServerResponse> httpServerResponsePromise = Promise.promise();
  private Promise<JsonObject> domainResourceJsonObjectPromise;


  public CreateUpdateOperationHandler() {
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

    } catch (NotValidFhirResourceException e) {
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
    String versionId = metadata.getVersionId();
    String lastModified = metadata.getLastUpdated().toString();
    String id = domainResource.getString("id");
    //general parameter to be supported


    Optional<String> acceptableType = httpHeaders
      .stream()
      .filter(fhirHttpHeader ->
        fhirHttpHeader.name().contentEquals(HttpHeaderNames.ACCEPT))
      .map(FhirHttpHeader::value)
      .findFirst();
    Optional<String> preferHeader = httpHeaders
      .stream()
      .filter(fhirHttpHeader ->
        fhirHttpHeader.name().contentEquals(FhirHttpHeaderNames.PREFER))
      .map(FhirHttpHeader::value)
      .findFirst();
    Optional<String> location = httpHeaders
      .stream()
      .filter(fhirHttpHeader ->
        fhirHttpHeader.name().contentEquals(HttpHeaderNames.LOCATION))
      .map(FhirHttpHeader::value)
      .findFirst();


    serverResponse.setStatusCode(HttpResponseStatus.CREATED.code());
    serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
    serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);
    serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + versionId);
    serverResponse.setStatusCode(HttpResponseStatus.CREATED.code());


    String prettyDomainResource = JsonObject.mapFrom(domainResource).encodePrettily();
    if (acceptableType.isPresent() && !preferHeader.isPresent()) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
      String length = String.valueOf(prettyDomainResource.getBytes(Charset.defaultCharset()).length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
      serverResponse.write(prettyDomainResource);
    }
    if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_MINIMAL)) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
      String message = "Successful operation";
      String length = String.valueOf(message.getBytes(Charset.defaultCharset()).length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
      serverResponse.write(message);
    } else if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_REPRESENTATION)) {
      String length = String.valueOf(prettyDomainResource.getBytes(Charset.defaultCharset()).length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON)
        .write(prettyDomainResource);
    } else if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_OPERATION_OUTCOME)) {
      OperationOutcome operationOutcome = new OperationOutcome();
      operationOutcome.setId(UUID.randomUUID().toString());
      OperationOutcomeIssue operationOutcomeIssue = new OperationOutcomeIssue();
      operationOutcomeIssue.setCode("informational");
      operationOutcomeIssue.setSeverity("information")
        .setDiagnostics("Resource correctly created");
      operationOutcome.setIssue(operationOutcomeIssue);
      String operationOutcomeString = Json.encodePrettily(operationOutcome);
      String length = String.valueOf(operationOutcomeString.getBytes(Charset.defaultCharset()).length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
      serverResponse.write(operationOutcomeString);
    } else {
      String message = "Resource created, but prefer header is wrong " + preferHeader;
      String length = String.valueOf(message.getBytes(Charset.defaultCharset()).length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).write(message);
    }
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
        String id = jsonObject.getString("id");
        //general parameter to be supported
        serverResponse.setStatusCode(HttpResponseStatus.CREATED.code());
        serverResponse.putHeader(HttpHeaderNames.ETAG, versionId);
        serverResponse.putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified);

        serverResponse.setStatusCode(HttpResponseStatus.CREATED.code());

        Optional<String> locationHeader = httpHeaders
          .stream()
          .filter(fhirHttpHeader -> fhirHttpHeader.name().contentEquals(HttpHeaderNames.LOCATION))
          .map(FhirHttpHeader::value)
          .findFirst();
        serverResponse.putHeader(HttpHeaderNames.LOCATION, locationHeader.orElse("/_history/" + versionId));
        Optional<String> acceptableType = httpHeaders
          .stream()
          .filter(fhirHttpHeader ->
            fhirHttpHeader.name().contentEquals(HttpHeaderNames.ACCEPT))
          .map(FhirHttpHeader::value)
          .findFirst();
        Optional<String> preferHeader = httpHeaders
          .stream()
          .filter(fhirHttpHeader ->
            fhirHttpHeader.name().contentEquals(FhirHttpHeaderNames.PREFER))
          .map(FhirHttpHeader::value)
          .findFirst();
        String prettyDomainResource = JsonObject.mapFrom(jsonObject).encodePrettily();
        if (acceptableType.isPresent() && !preferHeader.isPresent()) {
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON);
          String length = String.valueOf(prettyDomainResource.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.write(prettyDomainResource);
        }
        if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_MINIMAL)) {
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
          String message = "Successful operation";
          String length = String.valueOf(message.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.write(message);
        } else if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_REPRESENTATION)) {
          String length = String.valueOf(prettyDomainResource.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON)
            .write(prettyDomainResource);
        } else if (preferHeader.isPresent() && preferHeader.get().equalsIgnoreCase(FhirHttpHeaderValues.RETURN_OPERATION_OUTCOME)) {
          OperationOutcome operationOutcome = new OperationOutcome();
          operationOutcome.setId(UUID.randomUUID().toString());
          OperationOutcomeIssue operationOutcomeIssue = new OperationOutcomeIssue();
          operationOutcomeIssue.setCode("informational");
          operationOutcomeIssue.setSeverity("information")
            .setDiagnostics("Resource correctly created");
          operationOutcome.setIssue(operationOutcomeIssue);
          String operationOutcomeString = Json.encodePrettily(operationOutcome);
          String length = String.valueOf(operationOutcomeString.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.write(operationOutcomeString);
        } else {
          String message = "Resource created, but prefer header is wrong " + preferHeader;
          String length = String.valueOf(message.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).write(message);
        }
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
