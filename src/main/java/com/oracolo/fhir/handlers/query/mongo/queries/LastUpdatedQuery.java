package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LastUpdatedQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_lastUpdated";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$expr", new JsonObject()
//        .put(prefix.operator(), new JsonArray()
//          .add(new JsonObject()
//            .put("$dateFromString", new JsonObject()
//              .put("dateString", "$meta.lastUpdated")))
//          .add(new JsonObject()
//            .put("$dateFromString", new JsonObject()
//              .put("dateString", value))
//          )));
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject()
      .put(prefix.operator(), new JsonArray()
        .add(new JsonObject()
          .put("$dateFromString", new JsonObject()
            .put("dateString", "$meta.lastUpdated")))
        .add(new JsonObject()
          .put("$dateFromString", new JsonObject()
            .put("dateString", value))
        ));
  }


}
