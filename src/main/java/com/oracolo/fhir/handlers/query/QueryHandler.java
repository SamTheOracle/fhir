package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface QueryHandler {

  static QueryHandler createBaseQueryHandler() {
    return new BaseQueryHandler();
  }

  static QueryHandler fromResourceType(ResourceType resourceType) {
    switch (resourceType) {
      case PATIENT:
        return new PatientQueryHandler();
      case CONDITION:
        return new ConditionQueryHandler();
      case OBSERVATION:
        return new ObservationQueryHandler();
      case ENCOUNTER:
        return new EncounterQueryHandler();
      case PROCEDURE:
        return new ProcedureQueryHandler();
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
