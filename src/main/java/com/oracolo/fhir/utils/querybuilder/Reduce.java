package com.oracolo.fhir.utils.querybuilder;

import io.vertx.core.json.JsonObject;

class Reduce {
  private String initialValue;
  private JsonObject inExpression;

  public Reduce(String initialValue, JsonObject inExpression) {
    this.initialValue = initialValue;
    this.inExpression = inExpression;
  }


  public JsonObject toJson(JsonObject mongoDbExpression) {
    return new JsonObject()
      .put("$reduce", new JsonObject()
        .put("input", mongoDbExpression)
        .put("initialValue", initialValue)
        .put("in", inExpression));

  }

  public JsonObject toJson(String input) {
    return new JsonObject()
      .put("$reduce", new JsonObject()
        .put("input", input)
        .put("initialValue", initialValue)
        .put("in", inExpression));
  }


}
