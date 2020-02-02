package com.oracolo.fhir.model.aggregations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationEncounterTest {

  @Test
  void resources() {
    AggregationEncounter aggregationEncounter = new AggregationEncounter();
    assertDoesNotThrow(aggregationEncounter::resources);
    assertEquals(0, aggregationEncounter.resources().size());
  }
}
