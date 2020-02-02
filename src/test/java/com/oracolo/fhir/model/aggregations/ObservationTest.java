package com.oracolo.fhir.model.aggregations;

import com.oracolo.fhir.model.domain.Observation;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObservationTest {
  @Test
  public void testJsonMapping() {
    Observation observation = new Observation();
    Assertions.assertDoesNotThrow(() -> JsonObject.mapFrom(observation));
    Assertions.assertDoesNotThrow(() -> Json.decodeValue(JsonObject.mapFrom(observation).encode()));
  }
}
