package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import model.backboneelements.*;
import model.datatypes.Coding;
import model.datatypes.HumanName;
import model.datatypes.Period;
import model.domain.*;
import model.elements.CodeableConcept;
import model.elements.Reference;
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
  private DatabaseService databaseService;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router t4cRouter = Router.router(vertx);
    t4cRouter.route().handler(BodyHandler.create());
    t4cRouter.get("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/welcome").handler(this::handleWelcome);
    t4cRouter.post("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/reports").handler(this::handleReports);

    createAPIServer(0, t4cRouter)
      .compose(httpServer -> {
        int port = httpServer.actualPort();
        LOGGER.info("T4C interface listening at " + port);
        return publishHTTPEndPoint(port, FhirUtils.T4CSERVICE, FhirUtils.LOCALHOST, FhirUtils.T4CINTERFACE_MAIN_ROOT);
      }).setHandler(publishSuccessful -> {
      if (publishSuccessful.succeeded()) {
        this.databaseService = DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS);
        startPromise.complete();
      } else {
        startPromise.fail(publishSuccessful.cause());
      }
    });
  }

  private void handleReports(RoutingContext routingContext) {
    //Main json
    String encounterUUID = UUID.randomUUID().toString();
    JsonObject reportJson = routingContext.getBodyAsJson();


    JsonObject traumaInfo = reportJson.getJsonObject("traumaInfo");
    JsonObject iss = reportJson.getJsonObject("iss");

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

    addParticipant(reportJson);

    String startDate = reportJson.getString("startDate");
    String startTime = reportJson.getString("startTime");
    String endDate = reportJson.getString("endDate");
    String endTime = reportJson.getString("endTime");

    addPeriodOfEncounter(encounterEmergency, startDate, startTime, endDate, endTime);


    //iss
    if (iss != null) {
      addIssToEncounter(iss, encounterEmergency);
    }

    String finalDestination = reportJson.getString("finalDestination");
    encounterEmergency.addNewLocation(new EncounterLocation()
      .setLocation(new Reference()
        .setDisplay(finalDestination))
      .setStatus("active"));

    if (traumaInfo != null) {
      addTraumaInformation(traumaInfo, encounterEmergency);
    }

    JsonObject majorTraumaCriteria = reportJson.getJsonObject("majorTraumaCriteria");
    if (majorTraumaCriteria != null) {
      //mapped as condition
      addMajorTraumaCriteria(majorTraumaCriteria, encounterEmergency);
    }
    JsonObject anamnesisJsonObject = reportJson.getJsonObject("anamnesi");
    if (anamnesisJsonObject != null) {
      //mapped as procedure (e.g. a general action performed on a patient)
      addAnamnesi(anamnesisJsonObject, encounterEmergency);
    }


    //preh
    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();

//    JsonObject preh = reportJson.getJsonObject("preh");
//    String territorialArea = preh.getString("territorialArea");
//    boolean isCarAccident = preh.getBoolean("isCarAccident");
//    boolean bPleuralDecompression = preh.getBoolean("bPleuralDecompression");
//    boolean cBloodProtocol = preh.getBoolean("cBloodProtocol");
//    boolean cTpod = preh.getBoolean("cTpod");
//    int dGcsTotal = preh.getInteger("dGcsTotal");
//    boolean dAnisocoria = preh.getBoolean("dAnisocoria");
//    boolean dMidriasi = preh.getBoolean("dMidriasi");
//    boolean eMotility = preh.getBoolean("eMotility");
//    double worstBloodPressure = preh.getDouble("worstBloodPressure");
//    double worstRespiratoryRate = preh.getDouble("worstRespiratoryRate");
//    if (isCarAccident) {
//      encounterEmergency.addNewReasonCode(new CodeableConcept()
//        .addNewCoding(new Coding()
//          .setDisplay("Car accident")));
//    }
//    encounterEmergency.addNewLocation(new EncounterLocation()
//      .setLocation(new Reference()
//        .setReference(territorialArea)));
//    //major truama criteria diventa un'osservazione
//    JsonObject majorTraumaCriteria = reportJson.getJsonObject("majorTraumaCriteria");
//    majorTraumaCriteria.forEach(entryTraumaCriteria -> {
//      Object traumaCriteriaValue = entryTraumaCriteria.getValue();
//      Observation majorTraumaObservation = new Observation();
//      majorTraumaObservation
//        .setId(UUID.randomUUID().toString())
//        .setStatus("final")
//        .setCode(new CodeableConcept()
//          .setText(entryTraumaCriteria.getKey()))
//        .setValueBoolean((Boolean) traumaCriteriaValue)
//        .setEncounter(new Reference()
//          .setType("Encounter"));
//    });
//
//
//    if (cBloodProtocol) {
//      //create blood protocol procedure and link to encounterPreh
//      Procedure procedure = new Procedure();
//      procedure.setStatus("completed")
//        .setId(UUID.randomUUID().toString())
//        .setCode(new CodeableConcept()
//          .addNewCoding(new Coding()
//            .setCode("5447007")
//            .setDisplay("Transfusion")
//            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
//          .setText("BloodProtocol"))
//        .setEncounter(new Reference()
//          .setType("Encounter"))
//        .setSubject(new Reference()
//          .setReference("Patient bello"));
//    }
//    if (bPleuralDecompression) {
//      Procedure procedure = new Procedure();
//      procedure.setStatus("completed")
//        .setId(UUID.randomUUID().toString())
//        .setCode(new CodeableConcept()
//          .addNewCoding(new Coding()
//            .setCode("281613004")
//            .setDisplay("Decompression action")
//            .setSystem("https://browser.ihtsdotools.org/?perspective=full&conceptId1=281613004&edition=MAIN/2019-07-31&release=&languages=en"))
//          .setText("PleuralDecompression"))
//        .setEncounter(new Reference()
//          .setType("Encounter"))
//        .setSubject(new Reference()
//          .setReference("Patient bello"));
//    }
//    if (cTpod) {
//      Procedure procedure = new Procedure();
//      procedure.setStatus("completed")
//        .setId(UUID.randomUUID().toString())
//        .setCode(new CodeableConcept()
//          .addNewCoding(new Coding()
//            .setCode("771392003")
//            .setDisplay("Stability of joint structure of pelvic girdle")
//            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
//          .setText("TpodResponder"))
//        .setEncounter(new Reference()
//          .setType("Encounter"))
//        .setSubject(new Reference()
//          .setReference("Patient bello"));
//    }
//    Observation observationAnisocoria = new Observation();
//    observationAnisocoria
//      .setId(UUID.randomUUID().toString())
//      .setCode(new CodeableConcept()
//        .setText("Anisocoria"))
//      .setStatus("final")
//      .setValueBoolean(dAnisocoria)
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    Observation dGcs = new Observation();
//    dGcs
//      .setId(UUID.randomUUID().toString())
//      .setStatus("final")
//      .setValueInteger(dGcsTotal)
//      .setCode(new CodeableConcept()
//        .setText("Gcs"))
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    Observation midriasi = new Observation()
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    midriasi
//      .setId(UUID.randomUUID().toString())
//      .setStatus("final")
//      .setValueBoolean(dMidriasi)
//      .setCode(new CodeableConcept()
//        .setText("Midriasi"))
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    Observation eMotilityObservation = new Observation();
//    eMotilityObservation
//      .setId(UUID.randomUUID().toString())
//      .setStatus("final")
//      .setValueBoolean(eMotility)
//      .setCode(new CodeableConcept()
//        .setText("Motility"))
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    Observation worstBloodPressureObservation = new Observation();
//    worstBloodPressureObservation
//      .setId(UUID.randomUUID().toString())
//      .setStatus("final")
//      .setValueQuantity(new Quantity()
//        .setValue(worstBloodPressure)
//        .setUnit("mmHg")
//        .setCode("Torr")
//        .setSystem("https://www.britannica.com/science/torr"))
//      .setCode(new CodeableConcept()
//        .setText("WorstBloodPressureObservation"))
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    Observation worstRespiratoryRateObservation = new Observation();
//    worstRespiratoryRateObservation
//      .setId(UUID.randomUUID().toString())
//      .setStatus("final")
//      .setValueQuantity(new Quantity()
//        .setValue(worstRespiratoryRate)
//        .setUnit("Lungs Volume per minute"))
//      .setCode(new CodeableConcept()
//        .setText("WorstBloodPressureObservation"))
//      .setEncounter(new Reference()
//        .setType("Encounter"));
//    JsonObject patientInitialCondition = reportJson.getJsonObject("patientInitialCondition");
//    JsonObject vitalSigns = patientInitialCondition.getJsonObject("vitalSigns");

  }

  private void addAnamnesi(JsonObject anamnesisJsonObject, Encounter encounter) {

    String antiplatelets = anamnesisJsonObject.getString("antiplatelets");
    String anticoagulants = anamnesisJsonObject.getString("anticoagulant");
    String nao = anamnesisJsonObject.getString("nao");
    Procedure anamnesisProcedure = new Procedure();
    DocumentReference documentReference = new DocumentReference();
    if (Boolean.parseBoolean(antiplatelets)) {

    } else {

    }
  }

  private void addMajorTraumaCriteria(JsonObject majorTraumaCriteria, Encounter encounter) {
    String dynamic = majorTraumaCriteria.getString("dynamic");
    String physiological = majorTraumaCriteria.getString("physiological");
    String anatomical = majorTraumaCriteria.getString("anatomical");
    Observation majorTraumaCriteriaDynamicObservation = new Observation();
    Observation majorTraumaCriteriaAnatomicalObservation = new Observation();
    Observation majorTraumaCriteriaPhysiologicalObservation = new Observation();


    if (Boolean.parseBoolean(dynamic)) {
      majorTraumaCriteriaDynamicObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Dynamic"))
        .setValueBoolean(true);
    } else {
      majorTraumaCriteriaDynamicObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Dynamic"))
        .setValueBoolean(false);
    }
    if (Boolean.parseBoolean(anatomical)) {
      majorTraumaCriteriaAnatomicalObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Anatomical"))
        .setValueBoolean(true);
    } else {
      majorTraumaCriteriaAnatomicalObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Dynamic"))
        .setValueBoolean(false);
    }
    if (Boolean.parseBoolean(physiological)) {
      majorTraumaCriteriaPhysiologicalObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Physiological"))
        .setValueBoolean(true);
    } else {
      majorTraumaCriteriaPhysiologicalObservation.setCode(new CodeableConcept()
        .setText("Major Trauma Criteria - Dynamic"))
        .setValueBoolean(false);
    }

    Condition majorTraumaCriteriaCondition = new Condition()
      .setCode(new CodeableConcept()
        .setText("Major Trauma Criteria"))
      .addNewConditionStage(new ConditionStage()
        .addNewAssessment(new Reference()
          .setReference(""))
        .addNewAssessment(new Reference()
          .setReference(""))
        .addNewAssessment(new Reference()
          .setReference("")));
    encounter.addNewReasonReference(new Reference()
      .setType("Condition")
      .setReference("" + majorTraumaCriteriaCondition.getId()));


  }

  private void addPeriodOfEncounter(Encounter encounterEmergency, String startDate, String startTime, String endDate, String endTime) {
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
  }

  private void addParticipant(JsonObject reportJson) {
    EncounterParticipant encounterParticipant = new EncounterParticipant();

    String startOperatorId = reportJson.getString("startOperatorId");
    if (startOperatorId != null) {
      String startOperatorDescription = reportJson.getString("startOperatorDescription");
      encounterParticipant.addNewType(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("PPRF")
          .setDisplay("primary performer")
          .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType"))
        .setText("Trauma Leader"))
        .setIndividual(new Reference()
          //need to create a fhir resource
          .setDisplay(startOperatorDescription));
    }

    JsonArray traumaTeamMembers = reportJson.getJsonArray("traumaTeamMembers");
    if (traumaTeamMembers != null && !traumaTeamMembers.isEmpty()) {
      traumaTeamMembers.stream().map(traumaMemberObject -> (String) traumaMemberObject)
        .forEach(traumaMemberString -> {
          encounterParticipant.addNewType(new CodeableConcept()
            .addNewCoding(new Coding()
              .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
              .setCode("SPRF")
              .setDisplay("secondary performer")))
            .setIndividual(new Reference()
              .setDisplay(traumaMemberString));
        });
    }
  }

  private void addIssToEncounter(JsonObject iss, Encounter encounter) {
    //Condition for iss, referenced in encounter resource
    Condition conditionIssAssessment = new Condition();
    //Stage of a condition
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
                .setText(entryGroup.getKey()));
            //add a reference to the encounter
//              .setEncounter(new Reference()
//                .setType("Encounter"));
            WebClient client = WebClient.create(vertx);

            String absUri = FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE;
            HttpRequest<Buffer> request = client
              .postAbs(absUri);
            request.putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON);
            request.putHeader(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.RETURN_REPRESENTATION);
            JsonObject observationJsonObject = JsonObject.mapFrom(observation);
            Buffer observationBuffer = Buffer.buffer(observationJsonObject.encode());
            request.sendBuffer(observationBuffer, httpResponseAsyncResult -> {
              if (httpResponseAsyncResult.succeeded()) {
                HttpResponse<Buffer> httpResponse = httpResponseAsyncResult.result();
                Buffer responseBody = httpResponse.body();
                Observation observationFromServer = Json.decodeValue(responseBody, Observation.class);
                String observationId = observationFromServer.getId();
                //add the observation as a reference to the condition stage
                conditionStages.add(new ConditionStage()
                  .setType(new CodeableConcept()
                    .addNewCoding(new Coding()
                      .setUserSelected(true)
                      .setDisplay("AIS")
                      .setCode("273255001")))
                  .addNewAssessment(new Reference()
                    .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.OBSERVATION_TYPE + "/" + observationId)));
              } else {
                //handle
              }

            });

          }
        });
      }
    });
    WebClient client = WebClient.create(vertx);
    String absUri = FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE;


    conditionIssAssessment
      .setStage(conditionStages)
      .setClinicalStatus(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("active")
          .setDisplay("Active")
          .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinicalversion4.0.1")));
    List<EncounterDiagnosis> diagnoses = new ArrayList<>();
    diagnoses.add(new EncounterDiagnosis()
      .setUse(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("post-op diagnosis")
          .setCode("post-op")))
      .setCondition(new Reference()
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/"
          + conditionIssAssessment.getId())));
    encounter.setDiagnosis(diagnoses);

    HttpRequest<Buffer> conditionPostRequest = client
      .postAbs(absUri)
      .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON)
      .putHeader(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.RETURN_REPRESENTATION);
    conditionPostRequest.sendBuffer(Buffer.buffer(JsonObject.mapFrom(conditionIssAssessment).encode()), httpResponseAsyncResult -> {
      if (httpResponseAsyncResult.succeeded()) {
//        HttpResponse<Buffer> response = httpResponseAsyncResult.result();
//        //Condition conditionIssAssessmentFromServer = Json.decodeValue(response.body(), Condition.class);
//        List<EncounterDiagnosis> diagnoses = new ArrayList<>();
//        diagnoses.add(new EncounterDiagnosis()
//          .setCondition(new Reference()
//            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/"
//              + "id")));
      } else {
        //handle...
      }
    });


  }

  private void addTraumaInformation(JsonObject traumaInfo, Encounter encounterEmergency) {

    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();
    String vehicle = traumaInfo.getString("vehicle");
    if (vehicle != null) {
      //It is possible to add a list of all the locations (building, roads etc.) the patient has been
      encounterEmergency.addNewLocation(new EncounterLocation()
        .setLocation(new Reference().setReference(traumaInfo.getString("vehicle")))
        .setStatus("Completed")
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
    String erDeceased = traumaInfo.getString("erDeceased");
    String name = traumaInfo.getString("name");
    String surname = traumaInfo.getString("surname");
    String gender = traumaInfo.getString("gender");
    String dob = traumaInfo.getString("dob");
    int age = traumaInfo.getInteger("age");

    createPatient(name, surname, gender, age, dob, erDeceased, encounterEmergency);

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
      .setReference("Bufalini Cesena")));
  }

  private void createPatient(String name, String surname, String gender, int age, String dob, String erDeceased, Encounter encounter) {
    Patient patient = new Patient();
    List<HumanName> humanNames = new ArrayList<>();
    List<String> givens = new ArrayList<>();
    givens.add(name);
    humanNames.add(new HumanName()
      .setFamily(surname)
      .setGiven(givens));

    patient.setBirthDate(dob)
      .setName(humanNames)
      .setGender(gender);
    if (erDeceased != null) {
      patient.setDeceasedBoolean(Boolean.parseBoolean(erDeceased));
    }
    Buffer patientJson = Buffer.buffer(JsonObject.mapFrom(patient).encode());

    WebClient.create(vertx).postAbs(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE)
      .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeaderValues.APPLICATION_JSON)
      .putHeader(FhirHttpHeaderNames.PREFER, FhirHttpHeaderValues.RETURN_REPRESENTATION)
      .sendBuffer(patientJson, res -> {
        if (res.succeeded()) {
          Patient p = Json.decodeValue(res.result().bodyAsBuffer(), Patient.class);
          encounter.setPatient(new Reference()
            .setType("http://hl7.org/fhir/StructureDefinition/Patient")
            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + p.getId()));
        }
      });
  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
