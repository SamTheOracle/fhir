package com.oracolo.fhir.handlers;

import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirQueryParameter;
import com.oracolo.fhir.validator.Validator;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface OperationHandler {


  static OperationHandler createUpdateCreateOperationHandler(Validator validator) {
    return new CreateUpdateOperationHandler(validator);
  }

  static OperationHandler createUpdateCreateOperationHandler() {
    return new CreateUpdateOperationHandler();
  }

  static OperationHandler createReadOperationHandler(Validator validator) {
    return new ReadOperationHandler(validator);
  }

  static OperationHandler createReadOperationHandler() {
    return new ReadOperationHandler();
  }

  static OperationHandler createDeleteOperationHandler(Validator validator) {
    return new DeleteOperationHandler(validator);
  }

  static OperationHandler createDeleteOperationHandler() {
    return new DeleteOperationHandler();
  }


  OperationHandler setResponse(HttpServerResponse response);

  OperationHandler validate(JsonObject jsonObject);

  OperationHandler withQueryParameters(List<FhirQueryParameter> queryParameters);

  OperationHandler withQueryParameter(FhirQueryParameter queryParameter);

  OperationHandler withHeader(FhirHttpHeader fhirHttpHeader);

  OperationHandler withHeaders(List<FhirHttpHeader> fhirHttpHeaders);

  OperationHandler writeResponseBody(JsonObject domainResourceJsonObject);

  OperationHandler validateAgainstClass(JsonObject jsonObject);

  OperationHandler writeResponseBodyAsync(Promise<JsonObject> domainResourceJsonObjectPromise);

  void reset();

  Promise<HttpServerResponse> releaseAsync();

  HttpServerResponse release();

}
