package com.oracolo.fhir.handlers.query.mongo;

import com.oracolo.fhir.handlers.query.FhirQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.*;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.EncounterReferenceQuery;
import com.oracolo.fhir.handlers.query.mongo.queries.reference.SubjectReferenceQuery;

public enum MongoDbQuery {
  _id("_id", new IdQuery(), "String"),
  _content("_content", new ContentQuery(), "String"),
  _lastUpdated("_lastUpdated", new LastUpdatedQuery(), "Date/DateTime"),
  code("code", new CodeQuery(), "Token"),
  encounter("encounter", new EncounterReferenceQuery(), "Reference"),
  family("family", new FamilyQuery(), "String"),
  name("name", new NameQuery(), "String"),
  given("given", new GivenQuery(), "String"),
  valueInteger("valueInteger", new ValueIntegerQuery(), "Number"),
  identifier("identifier", new IdentifierQuery(), "Token"),
  subject("subject", new SubjectReferenceQuery(), "Reference");

  private String queryName;
  private FhirQuery query;
  private String type;

  MongoDbQuery(String name, FhirQuery query, String type) {
    this.queryName = name;
    this.query = query;
    this.type = type;
  }

  public String getQueryName() {
    return queryName;
  }

  public FhirQuery getFhirQuery() {
    return query;
  }

  public String getType() {
    return type;
  }
}
