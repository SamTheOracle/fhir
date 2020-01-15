package com.oracolo.fhir.model.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PatientTest {

  @Test
  void testMapDecodeEmptyPatient() {
    Patient patient = new Patient();
    Assertions.assertNull(patient.getMultipleBirthInteger());
    JsonObject j = JsonObject.mapFrom(patient);
    Assertions.assertNull(j.getString("multipleBirthInteger"));
  }
}
