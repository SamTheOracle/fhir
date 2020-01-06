package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.model.ResourceType;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface QueryHandler {

  static QueryHandler createBaseQueryHandler() {
    return new BaseQueryHandler();
  }

  static QueryHandler fromResourceType(String resourceType) {
    ResourceType rT = ResourceType.valueOf(resourceType.toUpperCase());
    switch (rT) {
      case Patient:
        return new PatientQueryHandler();
      case Condition:
        return new ConditionQueryHandler();
      case Observation:
        return new ObservationQueryHandler();
      default:
        return new BaseQueryHandler();
    }
  }


  QueryHandler query(MultiMap params);

  //to be supported
  QueryHandler ifMatch(List<String> values);

  QueryHandler ifModifiedSince(List<String> values);

  QueryHandler ifNoneMatch(List<String> values);

  JsonObject createMongoDbQuery();
}
