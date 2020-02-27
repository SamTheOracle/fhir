package com.oracolo.fhir.http;

import com.oracolo.fhir.ApplicationBootstrap;
import com.oracolo.fhir.model.aggregations.AggregationEncounter;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.FhirUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.Scanner;

@ExtendWith(VertxExtension.class)
public class QuerySearchTest {
  @BeforeAll
  static void bootstrapServer(Vertx vertx, VertxTestContext vertxTestContext) {
    Checkpoint checkpoint = vertxTestContext.checkpoint(3);
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
      InputStream inputStream1 = MongoDBOperatorsTest.class.getClassLoader()
        .getResourceAsStream("rep-20191013-164404.json");
      StringBuilder stringBuilder1 = new StringBuilder();
      if (inputStream1 != null) {
        Scanner scanner1 = new Scanner(inputStream1);
        while (scanner1.hasNext()) {
          stringBuilder1.append(scanner1.nextLine());
        }
      }
      InputStream inputStream2 = MongoDBOperatorsTest.class.getClassLoader()
        .getResourceAsStream("rep-20191012-215226.json");
      StringBuilder stringBuilder2 = new StringBuilder();
      if (inputStream2 != null) {
        Scanner scanner2 = new Scanner(inputStream2);
        while (scanner2.hasNext()) {
          stringBuilder2.append(scanner2.nextLine());
        }
      }
      JsonObject rep2 = new JsonObject(stringBuilder2.toString());
      JsonObject rep = new JsonObject(stringBuilder1.toString());
      JsonObject jsonObject = new JsonObject(stringBuilder.toString());
      vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.succeeding((deploymentId) -> {
        WebClient.create(vertx)
          .post(port, host, "/" + FhirUtils.TRAUMATRACKER_BASE + "/reports")
          .sendJsonObject(jsonObject, vertxTestContext.succeeding((response) -> {
            Assertions.assertEquals("AggregationEncounter", response.bodyAsJson(AggregationEncounter.class)
              .getResourceType());
            checkpoint.flag();
          }));
        WebClient.create(vertx)
          .post(port, host, "/" + FhirUtils.TRAUMATRACKER_BASE + "/reports")
          .sendJsonObject(rep, vertxTestContext.succeeding((response) -> {
            Assertions.assertEquals("AggregationEncounter", response.bodyAsJson(AggregationEncounter.class)
              .getResourceType());
            checkpoint.flag();
          }));
        WebClient.create(vertx)
          .post(port, host, "/" + FhirUtils.TRAUMATRACKER_BASE + "/reports")
          .sendJsonObject(rep2, vertxTestContext.succeeding((response) -> {
            Assertions.assertEquals("AggregationEncounter", response.bodyAsJson(AggregationEncounter.class)
              .getResourceType());
            checkpoint.flag();
          }));
      }));
    } else {
      vertxTestContext.failNow(new NullPointerException());
    }
  }

  @AfterAll
  static void dropDatabase() {

  }

  @Test
  public void searchValueInteger(Vertx vertx, VertxTestContext vertxTestContext) {
    String host = "localhost";
    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    WebClient.create(vertx)
      .get(port, host, "/" + FhirUtils.BASE + "/Observation")
      .addQueryParam("valueInteger", "gt4")
      .send(vertxTestContext.succeeding((response) -> {
        JsonObject r = response.bodyAsJsonObject();
        Assertions.assertDoesNotThrow(() -> Json.decodeValue(response.body(), Bundle.class));
        Bundle bundle = Json.decodeValue(response.body(), Bundle.class);
        Assertions.assertTrue(bundle.getEntry().size() > 0);
        vertxTestContext.completeNow();
      }));

  }

  @Test
  public void searchContent(Vertx vertx, VertxTestContext vertxTestContext) {
    String host = "localhost";
    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    WebClient.create(vertx)
      .get(port, host, "/" + FhirUtils.BASE + "/Observation")
      .addQueryParam("_content", "injury")
      .send(vertxTestContext.succeeding((response) -> {
        JsonObject r = response.bodyAsJsonObject();
        Assertions.assertDoesNotThrow(() -> Json.decodeValue(response.body(), Bundle.class));
        Bundle bundle = Json.decodeValue(response.body(), Bundle.class);
        Assertions.assertTrue(bundle.getEntry().size() > 0);
        vertxTestContext.completeNow();
      }));

  }

  @Test
  public void searchChainParameter(Vertx vertx, VertxTestContext vertxTestContext) {
    String host = "localhost";
    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    WebClient.create(vertx)
      .get(port, host, "/" + FhirUtils.BASE + "/Condition")
      .addQueryParam("evidence-detail:Observation.code", "67494-5")
      .addQueryParam("_lastUpdated", "le" + Instant.now())
      .send(vertxTestContext.succeeding((response) -> {
        JsonObject jsonObject = response.bodyAsJsonObject();
        Assertions.assertDoesNotThrow(() -> Json.decodeValue(response.body(), Bundle.class));
        Bundle bundle = Json.decodeValue(response.body(), Bundle.class);
        Assertions.assertTrue(bundle.getEntry().size() > 0);
        vertxTestContext.completeNow();
      }));

  }
}
