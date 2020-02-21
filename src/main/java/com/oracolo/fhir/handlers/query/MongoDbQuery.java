package com.oracolo.fhir.handlers.query;

enum MongoDbQuery {
  _id("_id", new IdQuery()),
  _content("_content", new ContentQuery()),
  _lastUpdated("_lastUpdated", new LastUpdatedQuery()),
  code("code", new CodeQuery()),
  subject("subject", new CodeQuery()),
  encounter("encounter", new EncounterReferenceQuery()),
  family("family", new FamilyQuery()),
  name("name", new NameQuery()),
  given("given", new GivenQuery()),
  valueInteger("valueInteger", new ValueIntegerQuery()),
  identifier("identifier", new IdentfierQuery());

  private String queryName;
  private FhirQuery query;

  MongoDbQuery(String name, FhirQuery query) {
    this.queryName = name;
    this.query = query;
  }

  public String getQueryName() {
    return queryName;
  }

  public FhirQuery getQuery() {
    return query;
  }
}
