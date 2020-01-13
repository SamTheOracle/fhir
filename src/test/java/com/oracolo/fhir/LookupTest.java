package com.oracolo.fhir;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class LookupTest {
  @BeforeAll
  static void boot(Vertx vertx, VertxTestContext vertxTestContext) {
    vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.completing());
  }

  @Test
  public void testLookup(Vertx vertx, VertxTestContext vertxTestContext) {

  }
}
