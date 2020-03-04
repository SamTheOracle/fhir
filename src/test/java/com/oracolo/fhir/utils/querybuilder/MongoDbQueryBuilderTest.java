package com.oracolo.fhir.utils.querybuilder;

import com.oracolo.fhir.handlers.query.mongo.MongoDbQuery;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MongoDbQueryBuilderTest {

  @Test
  public void testSimpleQuery() {
    JsonObject simpleQuery = QueryBuilder.builder()
      .createRegexMatch("inputParam", "regex", QueryBuilder.MongoDbRegexOptions.i);
    Assertions.assertNotNull(simpleQuery.getJsonObject("$regexMatch"));
    Assertions.assertEquals("regex", simpleQuery.getJsonObject("$regexMatch").getString("regex"));
  }

  @Test
  public void testCodeQueryAndCompare() {
    JsonObject codeQuery = MongoDbQuery.code
      .getFhirQuery()
      .mongoDbPipelineStageQuery("code", "1234");
    JsonObject builderCodeQuery = QueryBuilder.builder()
      .createOrCondition()
      .addNewOrElement(QueryBuilder.builder()
        .createRegexMatch("$code.text", "1234", QueryBuilder.MongoDbRegexOptions.i))
      .addNewOrElement(QueryBuilder.builder()
        .createRegexMatch(QueryBuilder.builder()
            .createReduce("$code.coding", "", QueryBuilder
              .builder()
              .createConcat("$$value", "$$this.code", " ", "$$this.display", " ")),
          "1234", QueryBuilder.MongoDbRegexOptions.i))
      .query();
    Assertions.assertEquals(codeQuery.encode(),builderCodeQuery.encode());

  }

}
