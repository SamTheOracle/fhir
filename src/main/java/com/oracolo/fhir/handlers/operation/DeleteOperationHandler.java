package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.utils.FhirUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public class DeleteOperationHandler extends BaseOperationHandler implements OperationHandler {


  public DeleteOperationHandler() {

  }


  @Override
  public OperationHandler createResponseAsync(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {

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
        String length = String.valueOf("".getBytes(Charset.defaultCharset()).length);


        serverResponse
          .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
          .putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + resourceType + "/" + id + "/_history/" + versionId)
          .putHeader(HttpHeaderNames.ETAG, versionId)
          .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .write("");


      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }


}
