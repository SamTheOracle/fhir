package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.ChainReference;
import io.vertx.core.json.JsonObject;

public class EvidenceDetailReferenceQuery extends BaseMongoDbQuery implements ChainReference {


  @Override
  public String name() {
    return "evidence-detail";
  }

  @Override
  public JsonObject mongoDbQuery() {
    return new JsonObject()
      .put("evidence.detail.reference", value);
  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    return new JsonObject();
  }

  @Override
  public JsonObject mongoDbMatchQuery(String mongoDbStageVariable) {
    return null;
  }
}
