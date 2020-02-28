package com.oracolo.fhir.handlers.query.mongo.queries.reference;

public enum ChainReferenceQuery {
  subject("subject", new SubjectReferenceQuery(), "subject"),
  encounter("encounter", new EncounterReferenceQuery(), "encounter"),
  diagnosis("diagnosis", new DiagnosisReferenceQuery(), "diagnosis"),
  evidence_detail("evidence-detail", new EvidenceDetailReferenceQuery(), "evidence");

  private String name;
  private ReferenceQuery chainReference;
  private String fhirResourceField;

  ChainReferenceQuery(String name, ReferenceQuery chainReference, String fhirResourceField) {
    this.name = name;
    this.chainReference = chainReference;
    this.fhirResourceField = fhirResourceField;
  }


  public String getName() {
    return name;
  }

  public ReferenceQuery getChainReference() {
    return chainReference;
  }

  public String getFhirResourceField() {
    return fhirResourceField;
  }
}
