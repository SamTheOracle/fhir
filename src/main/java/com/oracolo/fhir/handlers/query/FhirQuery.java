package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.parser.prefix.Prefix;
import io.vertx.core.json.JsonObject;

public interface FhirQuery {

  String name();

  JsonObject query();

  FhirQuery setPrefix(Prefix prefix);

  FhirQuery setValue(String value);


}
