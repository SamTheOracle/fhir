package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.mongo.MongoDbQueryHandler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public interface QueryHandler {


  static JsonObject createMongoDbQuery(MultiMap params) {
    return new MongoDbQueryHandler(params).createMongoDbQuery();
  }


}
