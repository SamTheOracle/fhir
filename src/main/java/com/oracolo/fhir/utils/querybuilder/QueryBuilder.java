package com.oracolo.fhir.utils.querybuilder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class QueryBuilder {


  public enum MongoDbRegexOptions {
    i, m, x, s
  }

  private JsonObject query = new JsonObject();
  private JsonArray ors = new JsonArray();
  private JsonArray ands = new JsonArray();


  public static QueryBuilder builder() {
    return new QueryBuilder();
  }

  public QueryBuilder createOrCondition() {
    query
      .put("$or", ors);
    return this;
  }

  public QueryBuilder createAndCondition() {
    query
      .put("$and", ands);
    return this;
  }


  public QueryBuilder addNewOrElement(JsonObject orElement) {
    ors.add(orElement);
    return this;
  }


  public QueryBuilder addNewAndElement(JsonObject andElement) {
    ands.add(andElement);
    return this;
  }

  public JsonObject createRegexMatch(String input, String regex, MongoDbRegexOptions mongoDbRegexOptions) {
    return new RegexMatch(regex,mongoDbRegexOptions).toJson(input);
  }
  public JsonObject createRegexMatch(JsonObject expression, String regex, MongoDbRegexOptions mongoDbRegexOptions) {
         return new RegexMatch(regex,mongoDbRegexOptions).toJson(expression);
  }

  public JsonObject createReduce(String input, String initialValue, JsonObject inExpression) {
    return new Reduce(initialValue,inExpression).toJson(input);
  }
  public JsonObject createReduce(JsonObject inputExpression, String initialValue, JsonObject inExpression) {
    return new Reduce(initialValue,inExpression).toJson(inputExpression);
  }
  public JsonObject createConcat(Object...expression){
    return new Concat(expression).toJson();
  }
  public QueryBuilder createMatchStage(){
    return this;
  }
  public QueryBuilder addExprOperator(JsonObject expr){
    return this;
  }
  public JsonObject query(){
    return query;
  }

}
