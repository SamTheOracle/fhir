package com.oracolo.fhir.utils.querybuilder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class Concat extends JsonObject {
  private final Object[] expression;

  public Concat(Object[] expression) {
    this.expression = expression;
  }

  public JsonObject toJson() {
    JsonArray concatArray = new JsonArray();
    for (Object o : expression) {
      concatArray.add(o);
    }
    return new JsonObject()
      .put("$concat", concatArray);
  }
}
