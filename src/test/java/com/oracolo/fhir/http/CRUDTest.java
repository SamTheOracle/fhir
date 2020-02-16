package com.oracolo.fhir.http;

import com.oracolo.fhir.ApplicationBootstrap;
import com.oracolo.fhir.model.datatypes.HumanName;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.utils.FhirHttpHeader;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class CRUDTest {

  final static String id = UUID.randomUUID().toString();

  @BeforeAll
  static void bootstrapServer(Vertx vertx, VertxTestContext vertxTestContext) {
    System.setProperty("db", "fhir_db_test");
    Checkpoint bootstrapCheckpoint = vertxTestContext.checkpoint(2);
    vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.succeeding(deploymentId -> {
      Patient patient = new Patient()
        .setId(id)
        .addNewHumanName(new HumanName()
          .addNewGiven("Giacomo")
          .setFamily("Zanotti"))
        .setBirthDate("1974-12-25");
      bootstrapCheckpoint.flag();
      Integer port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
      String host = "localhost";

      WebClient.create(vertx)
        .put(port, host, "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/" + id)
        .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
        .sendJson(JsonObject.mapFrom(patient), vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
          assertDoesNotThrow(() -> Json.decodeValue(response.body(), ResourceType.PATIENT.getResourceClass()));
          bootstrapCheckpoint.flag();
        })));
    }));
  }

  @Test
  void testRead(Vertx vertx, VertxTestContext vertxTestContext) {


    Integer port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    String host = "localhost";
    WebClient.create(vertx)
      .get(port, host, "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/" + id)
      .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
      .send(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertDoesNotThrow(() -> Json.decodeValue(response.body(), Patient.class));

        vertxTestContext.completeNow();

      })));
  }

  @Test
  void testCreate(Vertx vertx, VertxTestContext vertxTestContext) {

    Patient patient = new Patient()
      .setId(id)
      .addNewHumanName(new HumanName()
        .addNewGiven("Giacomo")
        .setFamily("Zanotti"))
      .setBirthDate("1974-12-25");
    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    String host = "localhost";
    WebClient.create(vertx)
      .post(port, host, "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName())
      .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
      .sendJsonObject(JsonObject.mapFrom(patient), vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertDoesNotThrow(() -> Json.decodeValue(response.body(), Patient.class));

        vertxTestContext.completeNow();

      })));
  }

  @Test
  void testVRead(Vertx vertx, VertxTestContext vertxTestContext) {
    Patient patient = new Patient()
      .setId(id)
      .addNewHumanName(new HumanName()
        .addNewGiven("Giacomo")
        .setFamily("Zanotti"))
      .setBirthDate("1974-12-25");

    int port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    String host = "localhost";
    Checkpoint checkVRead = vertxTestContext.checkpoint(2);

    WebClient.create(vertx)
      .put(port, host, "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/" + id)
      .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
      .sendJson(JsonObject.mapFrom(patient), vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertDoesNotThrow(() -> Json.decodeValue(response.body(), Patient.class));
        Patient p = Json.decodeValue(response.body(), Patient.class);
        checkVRead.flag();
        WebClient.create(vertx)
          .get(port, host, "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/" + id + "/_history/" + p
            .getMeta()
            .getVersionId())
          .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
          .send(vertxTestContext.succeeding(vreadResponse -> vertxTestContext.verify(() -> {
            assertDoesNotThrow(() -> Json.decodeValue(vreadResponse.body(), Patient.class));
            Patient patientFromVRead = Json.decodeValue(vreadResponse.body(), Patient.class);
            assertEquals(p.getId(), patientFromVRead.getId());
            checkVRead.flag();
          })));
      })));

  }


}


