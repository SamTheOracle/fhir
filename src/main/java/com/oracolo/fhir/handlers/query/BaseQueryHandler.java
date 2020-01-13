package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.utils.FhirUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * It creates a MongoDb query
 */
public class BaseQueryHandler implements QueryHandler {
  public final static String _id = "_id";
  public final static String _lastUpdated = "_lastUpdated";
  public final static String _text = "_text";
  public final static String _content = "_content";
  public final static String _code = "_code";
  protected MultiMap params;
  private List<String> ifMatchValues, ifModifiedSinceValues, ifNoneMatchValues;

  @Override
  public QueryHandler query(MultiMap params) {
    this.params = params;

    return this;
  }

  @Override
  public QueryHandler ifMatch(List<String> values) {
    this.ifMatchValues = values;
    return this;
  }

  @Override
  public QueryHandler ifModifiedSince(List<String> values) {
    this.ifModifiedSinceValues = values;
    return this;
  }

  @Override
  public QueryHandler ifNoneMatch(List<String> values) {
    this.ifNoneMatchValues = values;
    return this;
  }


  @Override
  public JsonObject createMongoDbQuery() {
    JsonObject query = new JsonObject();
    JsonArray andOperations = new JsonArray();
    params.forEach(entry -> {
      String queryName = entry.getKey();
      switch (queryName) {
        case _id:
          List<String> values = params.getAll(queryName);
          JsonArray queryParametersId = new JsonArray();
          values.forEach(v -> queryParametersId.add(new JsonObject()
            .put(FhirUtils.ID, v)));
          andOperations.add(new JsonObject().put("$or", queryParametersId));
          break;
        case _content:
          String contentFinalValue = String.join(" ", params.getAll(queryName));
          andOperations.add(new JsonObject()
            .put("$text", new JsonObject()
              .put("$search", contentFinalValue)));
        default:
          break;


      }

    });
    query.put("$and", andOperations);
    return query;
  }
}
