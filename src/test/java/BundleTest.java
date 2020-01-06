import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.model.resources.Bundle;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class BundleTest {
  @Test
  void testValidationTrue() {
    Bundle bundle = new Bundle();
    bundle
      .setTimestamp(Instant.now())
      .setType("searchset")
      .setTotal(3)
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()))
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()));
    Assertions.assertTrue(ValidationHandler
      .from(bundle.resourceType)
      .validateAgainstJsonSchema(JsonObject.mapFrom(bundle)));
  }

  @Test
  void testValidationFalse() {
    Bundle bundle = new Bundle();
    bundle
      .setTimestamp(Instant.now())
      .setTotal(3)
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()))
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()));
    Assertions.assertFalse(ValidationHandler
      .from(bundle.resourceType)
      .validateAgainstJsonSchema(JsonObject.mapFrom(bundle)));
  }

}
