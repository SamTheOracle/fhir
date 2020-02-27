package com.oracolo.fhir.handlers.response;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.database.UpdateResult;
import com.oracolo.fhir.handlers.response.format.Format;
import com.oracolo.fhir.model.datatypes.Metadata;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public class UpdateResponseHandler extends BaseResponseHandler implements ResponseHandler {


  public UpdateResponseHandler() {
  }


  @Override
  public ResponseHandler createResponseAsync(HttpServerResponse serverResponse, BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {

    Promise<JsonObject> jsonObjectPromise = Promise.promise();
    databaseServiceConsumer.accept(service, jsonObjectPromise);
    jsonObjectPromise
      .future()
      .onSuccess(jsonObject -> {
        UpdateResult updateResult = Json.decodeValue(jsonObject.encode(), UpdateResult.class);
        JsonObject body = new JsonObject(updateResult.getBody());
        Integer statusCode = updateResult.getStatus();
        Metadata metadata = Json.decodeValue(body.getJsonObject("meta").encode(), Metadata.class);
        String lastModified = metadata.getLastUpdated().toString();
        String versionId = metadata.getVersionId();
        String id = body.getString("id");
        String resourceType = body.getString("resourceType");
        Format format = super.responseFormat.createFormat(body);
        String response = format.getResponse();
        String contentType = format.getContentType();
        String length = String.valueOf(response.getBytes(Charset.defaultCharset()).length);
        serverResponse
          .putHeader(HttpHeaderNames.LOCATION, "/" + resourceType + "/" + id + "/_history/" + versionId)
          .putHeader(HttpHeaderNames.ETAG, versionId)
          .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
          .setStatusCode(statusCode)
          .putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .putHeader(HttpHeaderNames.CONTENT_TYPE, contentType)
          .write(response);


        httpServerResponsePromise.complete(serverResponse);

      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));
    return this;
  }


}
