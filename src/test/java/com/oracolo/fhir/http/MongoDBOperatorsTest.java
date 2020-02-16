package com.oracolo.fhir.http;

import com.oracolo.fhir.ApplicationBootstrap;
import com.oracolo.fhir.model.aggregations.AggregationEncounter;
import com.oracolo.fhir.utils.FhirUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * Test operators mongo db for numbers and dates
 */
@ExtendWith(VertxExtension.class)
class MongoDBOperatorsTest {

  @BeforeAll
  static void setUpTestEnvironment(Vertx vertx, VertxTestContext vertxTestContext) {

    //use local db test
    System.setProperty("db", "fhir_db_test");
    String host = "localhost";
    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    InputStream inputStream = MongoDBOperatorsTest.class.getClassLoader().getResourceAsStream("full_rep.json");
    StringBuilder stringBuilder = new StringBuilder();
    if (inputStream != null) {
      Scanner scanner = new Scanner(inputStream);
      while (scanner.hasNext()) {
        stringBuilder.append(scanner.nextLine());
      }
      JsonObject jsonObject = new JsonObject(stringBuilder.toString());
      vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.succeeding((deploymentId) -> {
        WebClient.create(vertx)
          .post(port, host, "/" + FhirUtils.TRAUMATRACKER_BASE + "/reports")
          .sendJsonObject(jsonObject, vertxTestContext.succeeding((response) -> {
            Assertions.assertEquals("AggregationEncounter", response.bodyAsJson(AggregationEncounter.class)
              .getResourceType());
            vertxTestContext.completeNow();
          }));
      }));
    } else {
      vertxTestContext.failNow(new NullPointerException());
    }


  }


  @Test
  public void testGreaterEqual(Vertx vertx, VertxTestContext vertxTestContext) {
    vertxTestContext.completeNow();
  }
}
