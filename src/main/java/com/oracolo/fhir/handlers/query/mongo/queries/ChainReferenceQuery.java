package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.queries.reference.DiagnosisReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.EncounterReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.SubjectReferenceQuery;

public enum ChainReferenceQuery {
  subject("subject", new SubjectReferenceQuery()),
  encounter("encounter", new EncounterReferenceQuery()),
  diagnosis("diagnosis", new DiagnosisReferenceQuery());

  private String name;
  private ChainReference chainReference;

  ChainReferenceQuery(String name, ChainReference chainReference) {
    this.name = name;
    this.chainReference = chainReference;
  }

  public String getName() {
    return name;
  }

  public ChainReference getChainReference() {
    return chainReference;
  }
}
