package com.oracolo.fhir.http;

import com.oracolo.fhir.ApplicationBootstrap;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class FhirServerTest {
  private static final String BASE_URL = "http://localhost:8000/fhirAPI";

  @BeforeAll
  static void bootstrap(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new ApplicationBootstrap(), testContext.succeeding(id -> {
      WebClient
        .create(vertx)
        .getAbs(BASE_URL + "/welcome")
        .send(testContext.succeeding());
    }));

  }

  @Test
  void start() {

    Assertions.assertEquals(2, 3);
  }
}
