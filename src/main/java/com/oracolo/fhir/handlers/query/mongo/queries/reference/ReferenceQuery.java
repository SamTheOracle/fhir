package com.oracolo.fhir.handlers.query.mongo.queries.reference;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.MongoDbQuery;
import io.vertx.core.json.JsonObject;

public interface ReferenceQuery extends FhirQuery {

  JsonObject createMongoDbLookUpStage(String paramName, String paramValue);

  static ReferenceQuery createReferenceQuery(MongoDbQuery mongoDbQuery) {
    switch (mongoDbQuery) {
      case diagnosis:
        return new DiagnosisReferenceQuery();
      case evidence_detail:
        return new EvidenceDetailReferenceQuery();
      case encounter:
        return new EncounterReferenceQuery();
      case subject:
        return new SubjectReferenceQuery();
      default:
        return null;
    }
  }
}
