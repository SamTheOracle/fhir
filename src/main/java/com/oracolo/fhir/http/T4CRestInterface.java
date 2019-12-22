package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.delete.DeleteDatabaseService;
import com.oracolo.fhir.database.user.UserDatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import model.backboneelements.ConditionStage;
import model.backboneelements.EncounterDiagnosis;
import model.backboneelements.EncounterHospitalization;
import model.backboneelements.EncounterLocation;
import model.datatypes.Coding;
import model.datatypes.HumanName;
import model.datatypes.Period;
import model.domain.*;
import model.elements.CodeableConcept;
import model.elements.Quantity;
import model.elements.Reference;
import model.exceptions.NotValideFhirResourceException;
import utils.FhirHttpHeaderNames;
import utils.FhirHttpHeaderValues;
import utils.FhirUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class T4CRestInterface extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(FhirServer.class.getName());
  private UserDatabaseService userService;
  private DeleteDatabaseService deleteService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router t4cRouter = Router.router(vertx);
    t4cRouter.route().handler(BodyHandler.create());
    t4cRouter.get("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/welcome").handler(this::handleWelcome);
    t4cRouter.post("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/intervention").handler(this::handleIntervention);

    createAPIServer(0, t4cRouter)
      .compose(httpServer -> {
        int port = httpServer.actualPort();
        LOGGER.info("T4C interface listening at " + port);
        return publishHTTPEndPoint(port, FhirUtils.T4CSERVICE, FhirUtils.LOCALHOST, FhirUtils.T4CINTERFACE_MAIN_ROOT);
      }).setHandler(publishSuccessful -> {
      if (publishSuccessful.succeeded()) {
        this.userService = UserDatabaseService.createProxy(vertx, FhirUtils.USER_SERVICE_ADDRESS);
        this.deleteService = DeleteDatabaseService.createProxy(vertx, FhirUtils.DELETE_SERVICE_ADDRESS);
        startPromise.complete();
      } else {
        startPromise.fail(publishSuccessful.cause());
      }
    });
  }

  private void handleIntervention(RoutingContext routingContext) {
    JsonObject interventionJson = routingContext.getBodyAsJson();
    JsonObject traumaInfo = interventionJson.getJsonObject("traumaInfo");
    JsonObject iss = interventionJson.getJsonObject("iss");

    Encounter encounterEmergency = new Encounter();
    encounterEmergency
      .setId(UUID.randomUUID().toString())
      .setServiceType(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("117")
          .setDisplay("Emergency Medical"))
        .setText("Emergency intervention"))
      .setClazz(new Coding()
        .setCode("EMER")
        .setDisplay("Emergency"))
      .setStatus("finished")
      .setServiceProvider(new Reference()
        .setReference("Ospedale Bufalini"));
    String startOperatorId = interventionJson.getString("startOperatorId");
    String startOperatorDescription = interventionJson.getString("startOperatorDescription");
    JsonArray traumaTeamMembers = interventionJson.getJsonArray("traumaTeamMembers");
    String startDate = interventionJson.getString("startDate");
    String startTime = interventionJson.getString("startTime");
    String endDate = interventionJson.getString("endDate");
    String endTime = interventionJson.getString("endTime");

    LocalDate startD = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalTime startT = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
    ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(startD.getYear(), startD.getMonthValue(), startD.getDayOfMonth(), startT.getHour(),
      startT.getMinute(), startT.getSecond(), startT.getNano(), ZoneId.systemDefault());
    LocalDate endD = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalTime endT = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
    ZonedDateTime finalZonedEndDateTime = ZonedDateTime.of(endD.getYear(), endD.getMonthValue(), endD.getDayOfMonth(), endT.getHour(),
      endT.getMinute(), endT.getSecond(), endT.getNano(), ZoneId.systemDefault());

    encounterEmergency.setPeriod(new Period()
      .setStart(FhirUtils.fullDateTime.format(finalZonedStartDateTime))
      .setEnd(FhirUtils.fullDateTime.format(finalZonedEndDateTime)));
    //iss
    addIssToEncounter(iss, encounterEmergency);

    addTraumaInformation(traumaInfo, encounterEmergency);


    //preh
    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();


    String otherEmergency = traumaInfo.getString("otherEmergency");
    if (otherEmergency != null) {
      encounterEmergency.addNewLocation(new EncounterLocation()
        .setLocation(new Reference()
          .setReference(traumaInfo.getString("otherEmergency")))
        .setStatus("completed"));
      encounterHospitalization.setAdmitSource(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("hosp-trans")
          .setSystem("https://www.hl7.org/fhir/codesystem-encounter-admit-source.html")
          .setDisplay("Transferred from other hospital"))
        .setText(otherEmergency));
    }
    encounterEmergency.addNewLocation(new EncounterLocation()
      .setLocation(new Reference().setReference(traumaInfo.getString("vehicle")))
      .setPhysicalType(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("ve")
          .setDisplay("Vehicle")
          .setSystem("https://www.hl7.org/fhir/valueset-location-physical-type.html"))
        .setText(traumaInfo.getString("vehicle"))));
    //set admission code
    encounterEmergency.setPriority(new CodeableConcept()
      .addNewCoding(new Coding()
        .setSystem("http://www.eena.it/")
        .setCode(traumaInfo.getString("admissionCode"))
        .setDisplay("Codice ammissione pronto soccorso")));
    encounterEmergency.setHospitalization(encounterHospitalization.setDestination(new Reference()
      //creare oggetto ospedale
      .setReference("Bufalini Cesena")));
    JsonObject preh = interventionJson.getJsonObject("preh");
    String territorialArea = preh.getString("territorialArea");
    boolean isCarAccident = preh.getBoolean("isCarAccident");
    boolean bPleuralDecompression = preh.getBoolean("bPleuralDecompression");
    boolean cBloodProtocol = preh.getBoolean("cBloodProtocol");
    boolean cTpod = preh.getBoolean("cTpod");
    int dGcsTotal = preh.getInteger("dGcsTotal");
    boolean dAnisocoria = preh.getBoolean("dAnisocoria");
    boolean dMidriasi = preh.getBoolean("dMidriasi");
    boolean eMotility = preh.getBoolean("eMotility");
    double worstBloodPressure = preh.getDouble("worstBloodPressure");
    double worstRespiratoryRate = preh.getDouble("worstRespiratoryRate");
    if (isCarAccident) {
      encounterEmergency.addNewReasonCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("Car accident")));
    }
    encounterEmergency.addNewLocation(new EncounterLocation()
      .setLocation(new Reference()
        .setReference(territorialArea)));
    //major truama criteria diventa un'osservazione
    JsonObject majorTraumaCriteria = interventionJson.getJsonObject("majorTraumaCriteria");
    majorTraumaCriteria.forEach(entryTraumaCriteria -> {
      Object traumaCriteriaValue = entryTraumaCriteria.getValue();
      Observation majorTraumaObservation = new Observation();
      majorTraumaObservation
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setCode(new CodeableConcept()
          .setText(entryTraumaCriteria.getKey()))
        .setValueBoolean((Boolean) traumaCriteriaValue)
        .setEncounter(new Reference()
          .setType("Encounter"));
    });

    //preh: creare osservazioni e procedure linkandole all'encounter. Sia procedure che observation hanno la proprietà: .encounter(Reference encounter)

    if (cBloodProtocol) {
      //create blood protocol procedure and link to encounterPreh
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("5447007")
            .setDisplay("Transfusion")
            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
          .setText("BloodProtocol"))
        .setEncounter(new Reference()
          .setType("Encounter"))
        .setSubject(new Reference()
          .setReference("Patient bello"));
    }
    if (bPleuralDecompression) {
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("281613004")
            .setDisplay("Decompression action")
            .setSystem("https://browser.ihtsdotools.org/?perspective=full&conceptId1=281613004&edition=MAIN/2019-07-31&release=&languages=en"))
          .setText("PleuralDecompression"))
        .setEncounter(new Reference()
          .setType("Encounter"))
        .setSubject(new Reference()
          .setReference("Patient bello"));
    }
    if (cTpod) {
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("771392003")
            .setDisplay("Stability of joint structure of pelvic girdle")
            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
          .setText("TpodResponder"))
        .setEncounter(new Reference()
          .setType("Encounter"))
        .setSubject(new Reference()
          .setReference("Patient bello"));
    }
    Observation observationAnisocoria = new Observation();
    observationAnisocoria
      .setId(UUID.randomUUID().toString())
      .setCode(new CodeableConcept()
        .setText("Anisocoria"))
      .setStatus("final")
      .setValueBoolean(dAnisocoria)
      .setEncounter(new Reference()
        .setType("Encounter"));
    Observation dGcs = new Observation();
    dGcs
      .setId(UUID.randomUUID().toString())
      .setStatus("final")
      .setValueInteger(dGcsTotal)
      .setCode(new CodeableConcept()
        .setText("Gcs"))
      .setEncounter(new Reference()
        .setType("Encounter"));
    Observation midriasi = new Observation()
      .setEncounter(new Reference()
        .setType("Encounter"));
    midriasi
      .setId(UUID.randomUUID().toString())
      .setStatus("final")
      .setValueBoolean(dMidriasi)
      .setCode(new CodeableConcept()
        .setText("Midriasi"))
      .setEncounter(new Reference()
        .setType("Encounter"));
    Observation eMotilityObservation = new Observation();
    eMotilityObservation
      .setId(UUID.randomUUID().toString())
      .setStatus("final")
      .setValueBoolean(eMotility)
      .setCode(new CodeableConcept()
        .setText("Motility"))
      .setEncounter(new Reference()
        .setType("Encounter"));
    Observation worstBloodPressureObservation = new Observation();
    worstBloodPressureObservation
      .setId(UUID.randomUUID().toString())
      .setStatus("final")
      .setValueQuantity(new Quantity()
        .setValue(worstBloodPressure)
        .setUnit("mmHg")
        .setCode("Torr")
        .setSystem("https://www.britannica.com/science/torr"))
      .setCode(new CodeableConcept()
        .setText("WorstBloodPressureObservation"))
      .setEncounter(new Reference()
        .setType("Encounter"));
    Observation worstRespiratoryRateObservation = new Observation();
    worstRespiratoryRateObservation
      .setId(UUID.randomUUID().toString())
      .setStatus("final")
      .setValueQuantity(new Quantity()
        .setValue(worstRespiratoryRate)
        .setUnit("Lungs Volume per minute"))
      .setCode(new CodeableConcept()
        .setText("WorstBloodPressureObservation"))
      .setEncounter(new Reference()
        .setType("Encounter"));
    JsonObject patientInitialCondition = interventionJson.getJsonObject("patientInitialCondition");
    JsonObject vitalSigns = patientInitialCondition.getJsonObject("vitalSigns");

  }

  private void addIssToEncounter(JsonObject iss, Encounter encounter) {
    Condition conditionIssAssessment = new Condition();
    List<ConditionStage> conditionStages = new ArrayList<>();
    iss.forEach(entry -> {
      String key = entry.getKey();
      //create a new condition for each body group in the object
      if (!key.equalsIgnoreCase("totalIss")) {
        JsonObject value = (JsonObject) entry.getValue();
        //create a new reference of observation about each of the body part, then create and persist the observation

        value.forEach(entryGroup -> {
          String uuid = UUID.randomUUID().toString();
          if (!entryGroup.getKey().equalsIgnoreCase("groupTotalIss")) {
            Observation observation = new Observation();
            observation.setStatus("final")
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Physical findings of General status Narrative")
                  .setCode("10210-3")
                  .setUserSelected(true))
                .setText("Physical observation for injury severity score"))
              .setId(uuid)
              //set iss value
              .setValueInteger((Integer) entryGroup.getValue())
              //set body site name
              .setBodySite(new CodeableConcept()
                .setText(entryGroup.getKey()))
              //add a reference to the encounter
              .setEncounter(new Reference()
                .setType("Encounter"));
            WebClient client = WebClient.create(vertx);

            String absUri = FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE;
            HttpRequest<Buffer> request = client
              .postAbs(absUri);
            request.putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON);
            request.putHeader(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.RETURN_REPRESENTATION);
            request.sendBuffer(Buffer.buffer(JsonObject.mapFrom(observation).encode()), httpResponseAsyncResult -> {

            });
            //add the observation as a reference to the condition stage
            conditionStages.add(new ConditionStage()
              .setType(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setUserSelected(true)
                  .setDisplay("AIS")
                  .setCode("273255001")))
              .addNewAssessment(new Reference()
                .setReference("")));
          }
        });
      }
    });
    conditionIssAssessment.setStage(conditionStages);
    List<EncounterDiagnosis> diagnoses = new ArrayList<>();
    diagnoses.add(new EncounterDiagnosis()
      .setCondition(new Reference()
        .setReference("http:localhost:8000/fhirAPI/Condition")));
    encounter.setDiagnosis(diagnoses);
  }

  private void addTraumaInformation(JsonObject traumaInfo, Encounter encounterEmergency) {

    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();
    String vehicle = traumaInfo.getString("vehicle");
    if (vehicle != null) {
      //It is possible to add a list of all the locations (building, roads etc.) the patient has been
      encounterEmergency.addNewLocation(new EncounterLocation()
        .setLocation(new Reference().setReference(traumaInfo.getString("vehicle")))
        .setPhysicalType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("ve")
            .setDisplay("Vehicle")
            .setSystem("https://www.hl7.org/fhir/valueset-location-physical-type.html"))
          .setText(vehicle)));
    }
    //how to handle?
    String code = traumaInfo.getString("code");
    String sdo = traumaInfo.getString("sdo");
    String admissionCode = traumaInfo.getString("admissionCode");
    boolean erDeceased = traumaInfo.getBoolean("erDeceased");
    String name = traumaInfo.getString("name");
    String surname = traumaInfo.getString("surname");
    String gender = traumaInfo.getString("gender");
    String dob = traumaInfo.getString("dob");
    int age = traumaInfo.getInteger("age");

    createPatient(name, surname, gender, age, dob, erDeceased);

    String accidentDate = traumaInfo.getString("accidentDate");
    String accidentTime = traumaInfo.getString("accidentTime");
    String accidentType = traumaInfo.getString("accidentType");


    //
    String otherEmergency = traumaInfo.getString("otherEmergency");
    if (otherEmergency != null) {
      encounterEmergency.addNewLocation(new EncounterLocation()
        .setLocation(new Reference()
          .setReference(traumaInfo.getString("otherEmergency")))
        .setStatus("completed"));
      encounterHospitalization.setAdmitSource(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("hosp-trans")
          .setSystem("https://www.hl7.org/fhir/codesystem-encounter-admit-source.html")
          .setDisplay("Transferred from other hospital"))
        .setText(otherEmergency));
    }

    //set admission code
    encounterEmergency.setPriority(new CodeableConcept()
      .addNewCoding(new Coding()
        .setSystem("http://www.eena.it/")
        .setCode(admissionCode)
        .setDisplay("Codice ammissione pronto soccorso")));
    encounterEmergency.setHospitalization(encounterHospitalization.setDestination(new Reference()
      //creare oggetto ospedale
      .setReference("Bufalini Cesena"))
      .setDischargeDisposition(new CodeableConcept()
        .setText(sdo)));

  }

  private void createPatient(String name, String surname, String gender, int age, String dob, boolean erDeceased) {
    Patient patient = new Patient();
    List<HumanName> humanNames = new ArrayList<>();
    List<String> givens = new ArrayList<>();
    givens.add(name);
    humanNames.add(new HumanName()
      .setFamily(surname)
      .setGiven(givens));

    patient.setBirthDate(dob)
      .setName(humanNames)
      .setDeceasedBoolean(erDeceased)
      .setGender(gender);
    JsonObject patientJson = JsonObject.mapFrom(patient);
    try {
      FhirUtils.validateJsonAgainstSchema(patientJson);
    } catch (NotValideFhirResourceException e) {
      e.printStackTrace();
    }
    this.userService.createOrUpdatePatientResource(patientJson, Promise.promise());
  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
