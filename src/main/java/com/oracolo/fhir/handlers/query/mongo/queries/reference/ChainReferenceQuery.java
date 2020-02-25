package com.oracolo.fhir.handlers.query.mongo.queries.reference;

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
