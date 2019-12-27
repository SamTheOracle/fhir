package com.oracolo.fhir.handlers;

import com.oracolo.fhir.validator.Validator;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.elements.Metadata;
import utils.FhirHttpHeader;
import utils.FhirHttpHeaderNames;
import utils.FhirHttpHeaderValues;
import utils.FhirUtils;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

public class CreateUpdateOperationHandler extends BaseOperationHandler implements OperationHandler {


  public CreateUpdateOperationHandler(Validator validator) {
    super(validator);
  }

  public CreateUpdateOperationHandler() {
  }


  /**
   * Create a HttpServerResponse with the headers previously added (Location,Prefer and Accept)
   *
   * @param domainResource the domainResource to write
   * @return
   */
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

  /**
   * Writes the response on the HttServerResponse previously set and form an adequate response based on previously
   * added headers and query parameters (these ones not yet supported fully)
   *
   * @param databasePromise the promise that will be completed with database result
   * @return
   */
  @Override
  public OperationHandler writeResponseBodyAsync(Promise<JsonObject> databasePromise) {

    databasePromise
      .future()
      .onSuccess(jsonObject -> {
        Metadata metadata = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class);
        String lastModified = metadata.getLastUpdated().toString();
        String versionId = metadata.getVersionId();
        String id = jsonObject.getString("id");
        String resourceType = jsonObject.getString("resourceType");
        serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
          .putHeader(HttpHeaderNames.ETAG, versionId)
          .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
          .putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
          .setStatusCode(HttpResponseStatus.CREATED.code());

        //handle return
        Optional<String> acceptableType = httpHeaders
          .stream()
          .filter(fhirHttpHeader ->
            fhirHttpHeader.name().contentEquals(HttpHeaderNames.ACCEPT) && fhirHttpHeader.value() != null)
          .map(FhirHttpHeader::value)
          .findFirst();
        Optional<String> preferHeader = httpHeaders
          .stream()
          .filter(fhirHttpHeader ->
            fhirHttpHeader.name().contentEquals(FhirHttpHeaderNames.PREFER) && fhirHttpHeader.value() != null)
          .map(FhirHttpHeader::value)
          .findFirst();
        String prettyDomainResource = JsonObject.mapFrom(jsonObject).encodePrettily();
        if (!acceptableType.isPresent() && !preferHeader.isPresent()) {
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, acceptableType.orElse(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4));
          String length = String.valueOf(prettyDomainResource.getBytes(Charset.defaultCharset()).length);
          serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length);
          serverResponse.write(prettyDomainResource);
        }
        if (acceptableType.isPresent() && !preferHeader.isPresent()) {
          serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, acceptableType.orElse(FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4));
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


}
