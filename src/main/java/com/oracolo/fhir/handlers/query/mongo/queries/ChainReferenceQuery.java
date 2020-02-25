package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.queries.reference.DiagnosisReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.EncounterReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.EvidenceDetailReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.SubjectReferenceQuery;

public enum ChainReferenceQuery {
  subject("subject", new SubjectReferenceQuery(), "subject"),
  encounter("encounter", new EncounterReferenceQuery(), "encounter"),
  diagnosis("diagnosis", new DiagnosisReferenceQuery(), "diagnosis"),
  evidence_detail("evidence-detail", new EvidenceDetailReferenceQuery(), "evidence");

  private String name;
  private ChainReference chainReference;
  private String fhirResourceField;

  ChainReferenceQuery(String name, ChainReference chainReference, String fhirResourceField) {
    this.name = name;
    this.chainReference = chainReference;
    this.fhirResourceField = fhirResourceField;
  }


  public String getName() {
    return name;
  }

  public ChainReference getChainReference() {
    return chainReference;
  }

  public String getFhirResourceField() {
    return fhirResourceField;
  }
}
