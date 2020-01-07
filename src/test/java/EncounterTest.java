import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.domain.Encounter;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EncounterTest {
  @Test
  public void decodeEncounterFalse() {
    Encounter encounter = new Encounter();
    Assertions.assertFalse(ValidationHandler.createValidator().validateAgainstJsonSchema(JsonObject.mapFrom(encounter)));
  }

  @Test
  public void decodeEncounterTrue() {
    Encounter encounter = new Encounter()
      .setStatus("planned")
      .setClazz(new Coding()
        .setCode("AMB"));
    Assertions.assertTrue(ValidationHandler.createValidator().validateAgainstJsonSchema(JsonObject.mapFrom(encounter)));
  }
}
