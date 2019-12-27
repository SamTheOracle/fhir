package com.oracolo.fhir.handlers;

import com.oracolo.fhir.validator.Validator;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

//TO-DO
public class SearchOperationHandler extends BaseOperationHandler implements OperationHandler {


  public SearchOperationHandler(Validator validator) {
    super(validator);
  }

  public SearchOperationHandler() {
  }


  /**
   * Create a HttpServerResponse with the headers previously added (Location,Prefer and Accept)
   *
   * @param domainResource the domainResource to write
   * @return
   */
  @Override
  public OperationHandler writeResponseBody(JsonObject domainResource) {


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

    return this;
  }


}
