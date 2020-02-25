package com.oracolo.fhir.http;

import com.oracolo.fhir.ApplicationBootstrap;
import com.oracolo.fhir.model.datatypes.Reference;
import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.Procedure;
import com.oracolo.fhir.utils.FhirHttpHeader;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class ValidationReSTEndpointTest {

  @BeforeAll
  static void bootstrapServer(Vertx vertx, VertxTestContext vertxTestContext) {
    System.setProperty("db", "fhir_db_test");
    vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.completing());
  }

  @Test
  void testValidationOfGenericResourceWrong(Vertx vertx, VertxTestContext vertxTestContext) {
    Integer port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    String host = "localhost";
    WebClient.create(vertx)
      .post(port, host, "/fhirAPI/Resource/$validate")
      .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
      .sendJson(new JsonObject(), vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertDoesNotThrow(() -> Json.decodeValue(response.body(), OperationOutcome.class));
        OperationOutcome operationOutcome = Json.decodeValue(response.body(), OperationOutcome.class);
        assertNotNull(operationOutcome.getIssue().get(0));
        assertEquals("error", operationOutcome.getIssue().get(0).getSeverity());
        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testValidationOfGenericResourceCorrect(Vertx vertx, VertxTestContext vertxTestContext) {
    Procedure procedure = new Procedure()
      .setSubject(new Reference()
        .setDisplay("Marco Carta"))
      .setStatus("completed");
    Integer port = Optional.ofNullable(Integer.getInteger("http.port")).orElse(8000);
    String host = "localhost";
    WebClient.create(vertx)
      .post(port, host, "/fhirAPI/Resource/$validate")
      .putHeader(FhirHttpHeader.APPLICATION_JSON.name(), FhirHttpHeader.APPLICATION_JSON.value())
      .sendJson(JsonObject.mapFrom(procedure), vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertDoesNotThrow(() -> Json.decodeValue(response.body(), OperationOutcome.class));
        OperationOutcome operationOutcome = Json.decodeValue(response.body(), OperationOutcome.class);
        assertNotNull(operationOutcome.getIssue().get(0));
        assertEquals("information", operationOutcome.getIssue().get(0).getSeverity());
        vertxTestContext.completeNow();
      })));
  }

}
