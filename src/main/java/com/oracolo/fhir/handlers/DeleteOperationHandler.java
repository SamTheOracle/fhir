package com.oracolo.fhir.handlers;

import com.oracolo.fhir.validator.Validator;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.elements.Metadata;
import utils.FhirUtils;

public class DeleteOperationHandler extends BaseOperationHandler implements OperationHandler {


  public DeleteOperationHandler(Validator validator) {
    super(validator);
  }

  public DeleteOperationHandler() {

  }

  @Override
  public OperationHandler writeResponseBody(JsonObject domainResource) {

    Metadata metadata = Json.decodeValue(domainResource.getJsonObject("meta").encode(), Metadata.class);
    String id = domainResource.getString("id");
    String versionId = metadata.getVersionId();
    String lastUpdated = metadata.getLastUpdated().toString();
    String resourceType = domainResource.getString("resourceType");
    serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
      .putHeader(HttpHeaderNames.ETAG, metadata.getVersionId())
      .putHeader(HttpHeaderNames.LAST_MODIFIED, lastUpdated)
      .setStatusCode(HttpResponseStatus.NO_CONTENT.code());
    return this;
  }


  @Override
  public OperationHandler writeResponseBodyAsync(Promise<JsonObject> domainResourceJsonObjectPromise) {

    domainResourceJsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {


        Metadata metadata = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class);
        String id = jsonObject.getString("id");
        String versionId = metadata.getVersionId();
        String lastUpdated = metadata.getLastUpdated().toString();
        String resourceType = jsonObject.getString("resourceType");
        serverResponse.putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
          .putHeader(HttpHeaderNames.ETAG, metadata.getVersionId())
          .putHeader(HttpHeaderNames.LAST_MODIFIED, lastUpdated)
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        httpServerResponsePromise.complete(serverResponse);
      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }


}
