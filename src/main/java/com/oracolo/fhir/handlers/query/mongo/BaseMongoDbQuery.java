package com.oracolo.fhir.handlers.query.mongo;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.parser.prefix.Prefix;

public abstract class BaseMongoDbQuery implements FhirQuery {

  protected Prefix prefix;
  protected String value;

  @Override
  public FhirQuery setPrefix(Prefix prefix) {
    this.prefix = prefix;
    return this;
  }

  @Override
  public FhirQuery setValue(String value) {
    this.value = value;
    return this;
  }

}
