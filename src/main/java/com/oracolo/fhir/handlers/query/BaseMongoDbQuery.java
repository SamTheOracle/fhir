package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.parser.prefix.Prefix;
import io.vertx.core.json.JsonObject;

public abstract class BaseMongoDbQuery implements FhirQuery {

  protected JsonObject query;
  protected Prefix prefix;

  @Override
  public JsonObject query() {
    return query;
  }

  @Override
  public FhirQuery setPrefix(Prefix prefix) {
    this.prefix = prefix;
    return this;
  }

}
