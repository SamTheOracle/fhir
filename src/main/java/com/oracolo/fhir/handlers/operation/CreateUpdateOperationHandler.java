package com.oracolo.fhir.handlers.operation;

import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResponseFormat;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public class CreateUpdateOperationHandler extends BaseOperationHandler implements OperationHandler {


  public CreateUpdateOperationHandler(ValidationHandler validator) {
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


    serverResponse.setStatusCode(HttpResponseStatus.CREATED.code())
      .putHeader(HttpHeaderNames.ETAG, versionId)
      .putHeader(HttpHeaderNames.LAST_MODIFIED, lastModified)
      .putHeader(HttpHeaderNames.LOCATION, FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + id + "/_history/" + versionId)
      .setStatusCode(HttpResponseStatus.CREATED.code());

    ResponseFormat responseFormat = super.responseFormat.format(domainResource);
    String length = String.valueOf(responseFormat.response().getBytes(Charset.defaultCharset()).length);
    FhirHttpHeader fhirHttpHeader = responseFormat.contentType();
    serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
      .putHeader(fhirHttpHeader.name(), fhirHttpHeader.value())
      .write(responseFormat.response());


    return this;
  }


  @Override
  public OperationHandler writeResponseBodyAsync(BiConsumer<DatabaseService, Promise<JsonObject>> databaseServiceConsumer) {

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
          .setStatusCode(HttpResponseStatus.CREATED.code());

        ResponseFormat format = super.responseFormat.format(jsonObject);
        String length = String.valueOf(format.response().getBytes(Charset.defaultCharset()).length);
        FhirHttpHeader contentType = format.contentType();
        serverResponse.putHeader(HttpHeaderNames.CONTENT_LENGTH, length)
          .putHeader(contentType.name(), contentType.value())
          .write(format.response());
        httpServerResponsePromise.complete(serverResponse);

      }).onFailure(throwable -> httpServerResponsePromise.fail(throwable));

    return this;
  }


}
