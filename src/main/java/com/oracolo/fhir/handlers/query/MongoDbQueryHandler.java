package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixResult;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * It creates a MongoDb query
 */
public class MongoDbQueryHandler implements QueryHandler {

  private MultiMap params;


  public MongoDbQueryHandler(MultiMap params) {
    this.params = params;
  }


  public JsonObject createMongoDbQuery() {
    JsonObject query = new JsonObject();
    JsonArray andOperations = new JsonArray();
    JsonArray pipeline = new JsonArray();

    params.forEach(entry -> {
      String queryName = entry.getKey();
      for (MongoDbQuery mongoDbQuery : MongoDbQuery.values()) {
        if (mongoDbQuery.getQueryName().equals(queryName)) {
          new QueryPrefixHandler();
          QueryPrefixResult queryPrefixResult = QueryPrefixHandler
            .parsePrefix(params.get(queryName));
          JsonObject fhirQuery = mongoDbQuery.getQuery()
            .setPrefix(queryPrefixResult.prefix())
            .setValue(queryPrefixResult.parsedValue())
            .query();
          andOperations.add(fhirQuery);
        }
      }
    });
   /* params.forEach(entry -> {
      String queryName = entry.getKey();
      if (queryName.contains(".")) {

        JsonObject lookup = new JsonObject()
          .put("$lookup", new JsonObject()
          .put(""));

      }
      if (queryName.equals("_id")) {
        List<String> values = params.getAll(queryName);
        JsonArray queryParametersId = new JsonArray();

        values.forEach(v -> queryParametersId.add(new IdQuery().setValue(v).query()));
        andOperations.add(new JsonObject().put("$or", queryParametersId));
      }
      if (queryName.equals("_content")) {
        String contentFinalValue = String.join(" ", params.getAll(queryName));
        andOperations.add(new ContentQuery().setValue(contentFinalValue).query());
      }
      if (queryName.equals("_lastUpdated")) {
        String lastUpdated = params.get(queryName);
        QueryPrefixResult queryPrefixResult = QueryPrefixHandler.parsePrefix(lastUpdated);
        Prefix prefix = queryPrefixResult.prefix();
        String parsedValue = queryPrefixResult.parsedValue();
        andOperations.add(new LastUpdatedQuery(prefix).setValue(parsedValue).query());
      }
      if (queryName.equals("code")) {
        String code = params.get("code");
        andOperations.add(new CodeQuery().setValue(code).query());
      }
      if (queryName.equals("encounter")) {
        String encounter = params.get("encounter");
        andOperations.add(new EncounterReferenceQuery().setValue(encounter).query());
      }
      if (queryName.equals("subject")) {
        String subject = params.get("subject");
        andOperations.add(new SubjectReferenceQuery().setValue(subject).query());
      }
      if (queryName.equals("valueInteger")) {
        String valueInteger = params.get("valueInteger");
        QueryPrefixResult valueIntegerQueryPrefixResult = QueryPrefixHandler.parsePrefix(valueInteger);
        Prefix valueIntegerPrefix = valueIntegerQueryPrefixResult.prefix();
        String valueIntegerParsedValue = valueIntegerQueryPrefixResult.parsedValue();
        andOperations.add(new ValueIntegerQuery(valueIntegerPrefix).setValue(valueIntegerParsedValue).query());
      }
      if (queryName.equals("name")) {
        String name = params.get("name");
        andOperations.add(new NameQuery().setValue(name).query());
      }
      if (queryName.equals("family")) {
        String family = params.get("family");
        andOperations.add(new FamilyQuery().setValue(family).query());
      }
      if (queryName.equals("given")) {
        String given = params.get("given");
        andOperations.add(new GivenQuery().setValue(given).query());
      }
    });*/
    query.put("$and", andOperations);
    pipeline.add(new JsonObject()
      .put("$match", query));

    return query;

  }
}
