package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ValueIntegerQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "valueInteger";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("valueInteger", new JsonObject().put(prefix.operator(), Integer.parseInt(value)));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    BigDecimal bigDecimal = BigDecimal.valueOf(Integer.parseInt(value));
    int precision = bigDecimal.precision() - 2;
    double scaleFactor = 5.0 / (Math.pow(10, precision));
    double firstRangeMember = Integer.parseInt(value) - scaleFactor;
    double secondRangeMember = Integer.parseInt(value) + scaleFactor;
    JsonObject rangeQuery = new JsonObject()
      .put("$and", new JsonArray()
        .add(new JsonObject()
          .put("$gte", new JsonArray()
            .add("$valueInteger")
            .add(firstRangeMember)))
        .add(new JsonObject()
          .put("$lt", new JsonArray()
            .add("$valueInteger")
            .add(secondRangeMember))));

    return new JsonObject()
      .put(prefix.operator(), new JsonArray()
        .add("$valueInteger")
        .add(Integer.valueOf(value)));
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName) {
    return null;
  }

}
