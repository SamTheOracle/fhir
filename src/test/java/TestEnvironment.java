import com.oracolo.fhir.model.datatypes.HumanName;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.model.elements.Metadata;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestEnvironment {


  private List<Patient> patients;
  private List<Patient> patientsWithOnlyOneId;

  @BeforeEach
  public void testGroupingBy() {
    patients = new ArrayList<>();
    patientsWithOnlyOneId = new ArrayList<>();
    String uuid1 = UUID.randomUUID().toString();
    String uuid2 = UUID.randomUUID().toString();
    String uuid3 = UUID.randomUUID().toString();
    Instant now = Instant.now();
    Instant epoch = Instant.EPOCH;
    Instant epoch2 = Instant.MAX;
    List<String> given = new ArrayList<>();
    given.add("Giacomo");
    patients.add(new Patient()
      .setId(uuid1)
      .setMeta(new Metadata()
        .setLastUpdated(now))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid1)
      .setMeta(new Metadata()
        .setLastUpdated(now.plusSeconds(12345)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid1)
      .setMeta(new Metadata()
        .setLastUpdated(now.minusSeconds(23456)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid1)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid2)
      .setMeta(new Metadata()
        .setLastUpdated(now.plusSeconds(12345)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid2)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid2)
      .setMeta(new Metadata()
        .setLastUpdated(now.plusSeconds(15)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid2)
      .setMeta(new Metadata()
        .setLastUpdated(now.minusSeconds(123)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patients.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patientsWithOnlyOneId.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(Instant.now()))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patientsWithOnlyOneId.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(now.plusSeconds(1234)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
    patientsWithOnlyOneId.add(new Patient()
      .setId(uuid3)
      .setMeta(new Metadata()
        .setLastUpdated(now.minusSeconds(123455432)))
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .setGiven(given)));
  }

  @Test
  public void mustBeOnlyOneForId() {
    List<Patient> m = patients.stream().collect(Collectors.groupingBy(Patient::getId))
      .values().stream().map(patientList -> patientList.stream().max(Comparator.comparing(o -> o.getMeta().getLastUpdated()))
        .get()).collect(Collectors.toList());
    Assertions.assertEquals(3, m.size());
  }

  @Test
  public void onlyOneItem() {
    List<Patient> m = patientsWithOnlyOneId.stream().collect(Collectors.groupingBy(Patient::getId))
      .values().stream().map(patientList -> patientList.stream().max(Comparator.comparing(o -> o.getMeta().getLastUpdated()))
        .get()).collect(Collectors.toList());
    Assertions.assertEquals(1, m.size());
  }

  @Test
  public void testOrder() {
    Patient patient = patientsWithOnlyOneId.stream().map(patientObject -> {
      JsonObject patientJson = JsonObject.mapFrom(patientObject);
      return Json.decodeValue(patientJson.encode(), Patient.class);
    }).max(Comparator.comparing(o -> o.getMeta().getLastUpdated()))
      .get();
  }


}
