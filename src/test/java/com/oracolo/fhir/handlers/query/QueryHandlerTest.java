package com.oracolo.fhir.handlers.query;

import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.utils.FhirUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(VertxExtension.class)
class QueryHandlerTest {

  @BeforeAll
  static void createWebRequest() {

  }

  @Test
  void query() {
  }

  @Test
  void ifMatch() {

  }

  @Test
  void ifModifiedSince() {
  }

  @Test
  void ifNoneMatch() {
  }

  @Test
  void createPatientComplexQuery(Vertx vertx, VertxTestContext vertxTestContext) {
    vertx.createHttpServer()
      .requestHandler(httpServerRequest -> {
        MultiMap queryParams = httpServerRequest.params();

        JsonObject query = QueryHandler
          .fromResourceType(ResourceType.PATIENT)
          .query(queryParams)
          .createMongoDbQuery();
        Assertions.assertNotNull(query);
        MongoClient client = FhirUtils.createFhirMongoDbConnection(vertx);
        client.find(FhirUtils.PATIENTS_COLLECTION, query, listAsyncResult -> {
          if (listAsyncResult.succeeded()) {
            List<Patient> patients = listAsyncResult.result()
              .stream()
              .peek(jsonObject -> jsonObject.remove("_id"))
              .map(jsonObject -> Json.decodeValue(jsonObject.encode(), Patient.class))
              .collect(Collectors.toList());
            Assertions.assertNotNull(patients);
            vertxTestContext.completeNow();

          }
        });

      })
      .listen(8080, handler -> {
        WebClient.create(vertx)
          .getAbs("http://localhost:8080")
          .addQueryParam("_id", "12345670")
          .addQueryParam("_id", "9ad5cc85-73a1-46d2-bbfd-a93f48155797")
          .addQueryParam("family", "Zan")
          .addQueryParam("given", "Giac")
          .addQueryParam("_text", "Giacomo")
          .send(response -> handler.result().close());
      });
  }

  @Test
  void createMongoDbQuery(Vertx vertx, VertxTestContext vertxTestContext) {
    vertx.createHttpServer()
      .requestHandler(httpServerRequest -> {
        MultiMap queryParams = httpServerRequest.params();

        JsonObject query = QueryHandler
          .createBaseQueryHandler()
          .query(queryParams)
          .createMongoDbQuery();
        Assertions.assertNotNull(query);
        MongoClient client = FhirUtils.createFhirMongoDbConnection(vertx);
        client.find(FhirUtils.PATIENTS_COLLECTION, query, listAsyncResult -> {
          if (listAsyncResult.succeeded()) {
            List<Patient> patients = listAsyncResult.result()
              .stream()
              .peek(jsonObject -> jsonObject.remove("_id"))
              .map(jsonObject -> Json.decodeValue(jsonObject.encode(), Patient.class))
              .collect(Collectors.toList());
            Assertions.assertNotNull(patients);
            vertxTestContext.completeNow();

          }
        });

      })
      .listen(8080, handler -> {
        WebClient.create(vertx)
          .getAbs("http://localhost:8080")
          .addQueryParam("_id", "12345670")
          .addQueryParam("_text", "Giacomo")
          .addQueryParam("_text", "Zanotti")
          .addQueryParam("_id", "9ad5cc85-73a1-46d2-bbfd-a93f48155797")
          .send(response -> handler.result().close());
      });
  }
}
