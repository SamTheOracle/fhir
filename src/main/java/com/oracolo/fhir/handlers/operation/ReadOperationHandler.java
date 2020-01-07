package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResponseFormat;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public class ReadOperationHandler extends BaseOperationHandler implements OperationHandler {


  public ReadOperationHandler() {

  }


  @Override
  public OperationHandler createResponse(HttpServerResponse serverResponse, JsonObject domainResource) {
    Metadata metadata = Json.decodeValue(domainResource.getJsonObject("meta").encode(), Metadata.class);
    String lastModified = metadata.getLastUpdated().toString();
    String versionId = metadata.getVersionId();
    String id = domainResource.getString("id");
    String resourceType = domainResource.getString("resourceType");
    serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
      .putHeader(HttpHeaderNames.ETAG, versionId)
      .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
      .setStatusCode(HttpResponseStatus.CREATED.code());

    ResponseFormat responseFormat = super.responseFormat.format(domainResource);
    String length = String.valueOf(responseFormat.response().getBytes(Charset.defaultCharset()).length);
    FhirHttpHeader contentType = responseFormat.contentType();
    serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
      .putHeader(contentType.name(), contentType.value())
      .write(responseFormat.response());

    return this;
  }

  @Override
  public OperationHandler createResponse(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {

    Promise<JsonObject> jsonObjectPromise = Promise.promise();
    databaseServiceConsumer.accept(service, jsonObjectPromise);
    jsonObjectPromise
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
          .setStatusCode(HttpResponseStatus.OK.code());

        ResponseFormat responseFormat = super.responseFormat.format(jsonObject);
        String length = String.valueOf(responseFormat.response().getBytes(Charset.defaultCharset()).length);
        FhirHttpHeader contentType = responseFormat.contentType();
        serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .putHeader(contentType.name(), contentType.value())
          .write(responseFormat.response());
        httpServerResponsePromise.complete(serverResponse);

      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;

  }


}
