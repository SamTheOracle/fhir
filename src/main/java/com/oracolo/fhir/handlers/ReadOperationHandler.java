package com.oracolo.fhir.handlers;

import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirHttpHeaderValues;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.validator.Validator;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.Optional;

public class ReadOperationHandler extends BaseOperationHandler implements OperationHandler {


  public ReadOperationHandler(Validator validator) {
    super(validator);
  }

  public ReadOperationHandler() {

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
  public OperationHandler writeResponseBodyAsync(Promise<JsonObject> domainResourceJsonObjectPromise) {

    domainResourceJsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        Metadata metadata = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class);
        String lastModified = metadata.getLastUpdated().toString();
        String versionId = metadata.getVersionId();
        String id = jsonObject.getString("id");
        String resourceType = jsonObject.getString("resourceType");
        String domainResourceString = jsonObject.encodePrettily();
        String length = String.valueOf(domainResourceString.getBytes(Charset.defaultCharset()).length);
        serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
          .putHeader(HttpHeaderNames.ETAG, versionId)
          .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
          .putHeader(HttpHeaderNames.CONTENT_TYPE, FhirHttpHeaderValues.APPLICATION_JSON_VERSION_4)
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .setStatusCode(HttpResponseStatus.OK.code())
          .write(domainResourceString);

        httpServerResponsePromise.complete(serverResponse);
      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }


}
