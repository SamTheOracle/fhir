package com.oracolo.fhir.utils.querybuilder;

import io.vertx.core.json.JsonObject;

class RegexMatch {
  private String regex;
  private QueryBuilder.MongoDbRegexOptions option;

  public RegexMatch(String regex, QueryBuilder.MongoDbRegexOptions option) {
    this.regex = regex;
    this.option = option;
  }

  public JsonObject toJson(String input) {
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", input)
        .put("regex", regex)
        .put("options", option.toString()));
  }

  public JsonObject toJson(JsonObject inputExpression) {
    return new JsonObject()
      .put("$regexMatch", new JsonObject()
        .put("input", inputExpression)
        .put("regex", regex)
        .put("options", option.toString()));
  }
}
