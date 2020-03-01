package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.mongo.MongoDbQueryHandler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * might add query handler for sql queries in the future?
 */
public interface QueryHandler {


  static JsonObject createMongoDbQuery(MultiMap params) throws Exception{
    return new MongoDbQueryHandler(params).createMongoDbQuery();
  }


}
