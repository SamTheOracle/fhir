package com.oracolo.fhir.handlers.query.parser.chain;

import io.vertx.core.json.JsonObject;

public class ChainParserResult {

  private String collection;
  private JsonObject query;

  public ChainParserResult(String collection, JsonObject query) {
    this.collection = collection;
    this.query = query;
  }

  public String getCollection() {
    return collection;
  }

  public JsonObject getQuery() {
    return query;
  }
}
