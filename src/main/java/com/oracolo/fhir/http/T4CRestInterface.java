package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.backboneelements.*;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Quantity;
import com.oracolo.fhir.model.elements.Reference;
import com.oracolo.fhir.utils.FhirUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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
    List<DomainResource> domainResources = new ArrayList<>();
    //Main json
    JsonObject reportJson = routingContext.getBodyAsJson();

    Encounter encounterAll = new Encounter()
      .setId(UUID.randomUUID().toString());
    Encounter encounterPreh = new Encounter()
      .setId(UUID.randomUUID().toString())
      .setPartOf(new Reference()
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.ENCOUNTER.getCollection() + "/" + encounterAll.getId()));
    //traumaInfo, majorTraumaCriteria,preh,anamnesi
    Encounter encounterShock = new Encounter()
      //patient initial condition, vital signs
      .setId(UUID.randomUUID().toString())
      .setPartOf(new Reference()
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.ENCOUNTER.getCollection() + "/" + encounterAll.getId()));


    Condition traumaCondition = new Condition()
      .setId(UUID.randomUUID().toString())
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("417163006")
          .setDisplay("Traumatic AND/OR non-traumatic injury (disorder)")
          .setSystem("http://www.snomed.org/")));

    domainResources.add(traumaCondition);

    encounterPreh.addNewReasonReference(new Reference()
      .setReference("/" + ResourceType.CONDITION + "/" + traumaCondition.getId()));


    //modellazione eventi???

    JsonObject traumaInfo = reportJson.getJsonObject("traumaInfo");
    JsonObject iss = reportJson.getJsonObject("iss");

    encounterPreh
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
        .setDisplay("Ospedale Bufalini"));

    addParticipant(reportJson, encounterPreh);
    addParticipant(reportJson, encounterAll);
    addParticipant(reportJson, encounterShock);

    String startDate = reportJson.getString("startDate");
    String startTime = reportJson.getString("startTime");
    String endDate = reportJson.getString("endDate");
    String endTime = reportJson.getString("endTime");

    addPeriodToEncounter(encounterAll, startDate, startTime, endDate, endTime);


    //iss
    if (iss != null) {
      addIssToEncounter(iss, encounterShock, domainResources);
    }

    //aggiunta della final destination del encounter all
    String finalDestination = reportJson.getString("finalDestination");
    encounterAll.setHospitalization(new EncounterHospitalization()
      .setDestination(new Reference()
        .setDisplay(finalDestination)));

    if (traumaInfo != null) {
      addTraumaInformation(traumaInfo, encounterPreh, traumaCondition, domainResources);
    }

    JsonObject majorTraumaCriteria = reportJson.getJsonObject("majorTraumaCriteria");
    if (majorTraumaCriteria != null) {
      //mapped as condition
      addMajorTraumaCriteria(majorTraumaCriteria, traumaCondition);
    }
    JsonObject anamnesisJsonObject = reportJson.getJsonObject("anamnesi");
    if (anamnesisJsonObject != null) {
      //mapped as procedure (e.g. a general action performed on a patient)
      addAnamnesi(anamnesisJsonObject, encounterPreh, domainResources);
    }


    JsonObject preh = reportJson.getJsonObject("preh");
    if (preh != null) {
      addPrehToEncounter(preh, encounterPreh, traumaCondition, domainResources);

    }
    JsonObject patientInitialCondition = reportJson.getJsonObject("patientInitialCondition");
    if (patientInitialCondition != null) {
      addPatientInitialCondition(patientInitialCondition, encounterPreh, traumaCondition, domainResources);
    }
    JsonObject ePreh = JsonObject.mapFrom(encounterPreh);
    JsonObject eShock = JsonObject.mapFrom(encounterShock);
    JsonObject eAll = JsonObject.mapFrom(encounterAll);
    JsonObject prehCondition = JsonObject.mapFrom(traumaCondition);

    domainResources.add(encounterAll);
    domainResources.add(encounterShock);
    domainResources.add(encounterPreh);
  }

  private void addPatientInitialCondition(JsonObject patientInitialCondition, Encounter encounterPreh, Condition traumaCondition, List<DomainResource> domainResources) {
    JsonObject clinicalPicture = patientInitialCondition.getJsonObject("clinicalPicture");
    JsonObject vitalSigns = patientInitialCondition.getJsonObject("vitalSigns");

    if (vitalSigns != null) {

      String temp = vitalSigns.getString("temp");

      if (temp != null) {

      }
    }
    if (clinicalPicture != null) {
      Integer gcsTotal = clinicalPicture.getInteger("gcsTotal");
//      Procedure clinicalPictureProcedure = new Procedure()
//        .setId(UUID.randomUUID().toString())
//        .setStatus("completed")
//        .setSubject(encounterPreh.getPatient())
//        .setCode(new CodeableConcept()
//          .setText("Clinical Picture"))
//        .setEncounter(new Reference()
//          .setType(ResourceType.ENCOUNTER.typeName())
//          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()));
//      domainResources.add(clinicalPictureProcedure);
      if (gcsTotal != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueInteger(gcsTotal)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow Coma Scale")
              .setCode("35088-4")
              .setSystem("https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code=35088-4"))
            .setText("Glasgow Coma Scale Totale"))
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))

          .setSubject(encounterPreh.getPatient());
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Glasgow Coma Scale Total"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
        domainResources.add(observation);
      }
      String gcsMotor = clinicalPicture.getString("gcsMotor");
      if (gcsMotor != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(gcsMotor)
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow Coma Scale")
              .setCode("35088-4")
              .setSystem("https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code=35088-4"))
            .setText("Glasgow Coma Scale Motor"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Glasgow Coma Scale Motor"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
        domainResources.add(observation);
      }
      String gcsVerbal = clinicalPicture.getString("gcsVerbal");
      if (gcsVerbal != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(gcsVerbal)
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow Coma Scale")
              .setCode("35088-4")
              .setSystem("https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code=35088-4"))
            .setText("Glasgow Coma Scale Verbal"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Glasgow Coma Scale Verbal"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));

      }
      String gcsEyes = clinicalPicture.getString("gcsEyes");
      if (gcsEyes != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(gcsEyes)
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow Coma Scale")
              .setCode("35088-4")
              .setSystem("https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code=35088-4"))
            .setText("Glasgow Coma Scale Eyes"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Glasgow Coma Scale Eyes"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      Boolean sedated = clinicalPicture.getBoolean("sedated");
      if (sedated != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setStatus("final")
          .setValueBoolean(sedated)
          .setCode(new CodeableConcept()
            .setText("Sedated Patient"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Patient Sedated"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      String pupils = clinicalPicture.getString("pupils");
      if (pupils != null) {
        Observation observation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(pupils)
          .setCode(new CodeableConcept()
            .setText("Pupils"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Pupils"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      String airway = clinicalPicture.getString("airway");
      if (airway != null) {
        Observation observation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(airway)
          .setCode(new CodeableConcept()
            .setText("Airways"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Airways"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      Boolean positiveInhalation = clinicalPicture.getBoolean("positiveInhalation");
      if (positiveInhalation != null) {
        Observation observation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .setText("Positive Inhalation"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("positiveInhalation"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      Boolean intubationFailed = clinicalPicture.getBoolean("intubationFailed");
      if (intubationFailed != null) {

        CodeableConcept outcome = intubationFailed ?
          new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("385671000")
              .setDisplay("Unsuccessful")
              .setSystem("http://www.snomed.org/")
            ) : new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("385669000")
            .setDisplay("Successful")
          );
        Procedure intubationFailedProcedure = new Procedure();
        intubationFailedProcedure.setStatus("completed")
          .setId(UUID.randomUUID().toString())
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("112798008")
              .setDisplay("Insertion of endotracheal tube (procedure)")
              .setSystem("http://www.snomed.org/"))
            .setText("Intubation"))
          .setOutcome(outcome)
//          .addNewPartOf(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setEncounter(new Reference()
            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(intubationFailedProcedure);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Intubation"))
            .addNewDetail(new Reference()
              .setType(ResourceType.PROCEDURE.typeName())
              .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + intubationFailedProcedure.getId())));

      }
      String chestTube = clinicalPicture.getString("chestTube");
      if (chestTube != null) {
        Observation chestTubeObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(chestTube)
          .setCode(new CodeableConcept()
            .setText("Chest Tube"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(chestTubeObservation);

        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Chest Tube"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + chestTubeObservation.getId())));
      }
      String oxygenPercentage = clinicalPicture.getString("oxygenPercentage");
      if (oxygenPercentage != null) {
        Observation oxygenPercentageObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(chestTube)
          .setCode(new CodeableConcept()
            .setText("Oxygen Percentage"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(oxygenPercentageObservation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Oxygen Percentage"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + oxygenPercentageObservation.getId())));
      }
      Boolean hemorrhage = clinicalPicture.getBoolean("hemorrhage");
      if (hemorrhage != null) {
        Observation hemorrhageObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("131148009")
              .setDisplay("Bleeding (finding)")
              .setSystem("http://www.snomed.org/"))
            .setText("Hemorrhage"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(hemorrhageObservation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Hemorrhage"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + hemorrhageObservation.getId())));
      }
      Boolean limbsFracture = clinicalPicture.getBoolean("limbsFracture");
      if (limbsFracture != null) {
        Observation limbsFractureObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("125605004")
              .setDisplay("Fracture of bone (disorder)")
              .setSystem("http://www.snomed.org/"))
            .setText("Limbs Fracture"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(limbsFractureObservation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Limbs Fracture"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + limbsFractureObservation.getId())));
      }

      Boolean fractureExposition = clinicalPicture.getBoolean("fractureExposition");
      if (fractureExposition != null) {
        Observation fractureExpositionObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("125605004")
              .setDisplay("Fracture of bone (disorder)")
              .setSystem("http://www.snomed.org/"))
            .setText("Fracture Exposition"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(fractureExpositionObservation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Fracture Exposition"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + fractureExpositionObservation.getId())));
      }

      String burn = clinicalPicture.getString("burn");
      if (burn != null) {
        Observation burnObservation = new Observation()
//          .addNewPartOfReference(new Reference()
//            .setType(ResourceType.PROCEDURE.typeName())
//            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + clinicalPictureProcedure.getId()))
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(chestTube)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("48333001")
              .setDisplay("Burn injury (morphologic abnormality)"))
            .setText("Burn"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(burnObservation);
        traumaCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Burn"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + burnObservation.getId())));
      }


    }


  }

  private void addPrehToEncounter(JsonObject preh, Encounter encounterPreh, Condition traumaCondition, List<DomainResource> domainResources) {
    String territorialArea = preh.getString("territorialArea");
    Boolean isCarAccident = preh.getBoolean("isCarAccident");
    Boolean bPleuralDecompression = preh.getBoolean("bPleuralDecompression");
    Boolean cBloodProtocol = preh.getBoolean("cBloodProtocol");
    Boolean cTpod = preh.getBoolean("cTpod");
    Integer dGcsTotal = preh.getInteger("dGcsTotal");
    Boolean dAnisocoria = preh.getBoolean("dAnisocoria");
    Boolean dMidriasi = preh.getBoolean("dMidriasi");
    Boolean eMotility = preh.getBoolean("eMotility");
    Double worstBloodPressure = preh.getDouble("worstBloodPressure");
    Double worstRespiratoryRate = preh.getDouble("worstRespiratoryRate");

    if (territorialArea != null) {
      encounterPreh.addNewLocation(new EncounterLocation()
        .setLocation(new Reference()
          .setDisplay(territorialArea)));
    }
    if (isCarAccident != null && isCarAccident) {
      traumaCondition
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("418399005")
            .setDisplay("Motor vehicle accident")
            .setSystem("http://www.snomed.org/")));
    }
    if (bPleuralDecompression != null && bPleuralDecompression) {
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
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(new Reference()
          .setReference(encounterPreh.getPatient().getReference()));
    }
    if (cBloodProtocol != null && cBloodProtocol) {
      //create blood protocol procedure and link to encounterPreh
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("5447007")
            .setDisplay("Transfusion")
            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
          .setText("Blood Protocol"))
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(procedure);

    }
    if (cTpod != null && cTpod) {
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("771392003")
            .setDisplay("Stability of joint structure of pelvic girdle")
            .setSystem("http://browser.ihtsdotools.org/?perspective=full&conceptId1=5447007"))
          .setText("Tpod Responder"))
        .addNewUsedCode(new CodeableConcept()
          .setText("T-Pod Responder"))
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(procedure);

    }
    if (dAnisocoria != null && dAnisocoria) {
      Observation observationAnisocoria = new Observation();
      observationAnisocoria
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .setText("Anisocoria"))
        .setStatus("final")
        .setValueBoolean(true)
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(observationAnisocoria);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Anisocoria Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationAnisocoria.getId())));
    }
    if (dMidriasi != null && dMidriasi) {
      Observation observationMidriasi = new Observation();
      observationMidriasi
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .setText("Midriasi"))
        .setStatus("final")
        .setValueBoolean(true)
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(observationMidriasi);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Midriasi Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationMidriasi.getId())));
    }
    if (eMotility != null && eMotility) {
      Observation observationMotility = new Observation();
      observationMotility
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .setText("Motility"))
        .setStatus("final")
        .setValueBoolean(true)
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(observationMotility);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Anisocoria Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationMotility.getId())));
    }
    //GCS Total obvservation
    if (dGcsTotal != null) {
      Observation dGcs = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setValueInteger(dGcsTotal)
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setDisplay("Glasgow Coma Scale")
            .setCode("35088-4")
            .setSystem("https://fhir.loinc.org/CodeSystem/$lookup?system=http://loinc.org&code=35088-4"))
          .setText("Gcs"))
        .setEncounter(new Reference()
          .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(dGcs);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("GCS TOTAL"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + dGcs.getId())));
    }


    if (worstBloodPressure != null) {
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
          .setText("Worst Blood Pressure Observation"))
        .setEncounter(new Reference()
          .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Worst Blood Pressure Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + worstBloodPressureObservation.getId())));
      domainResources.add(worstBloodPressureObservation);

    }
    if (worstRespiratoryRate != null) {
      Observation worstRespiratoryRateObservation = new Observation();
      worstRespiratoryRateObservation
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setValueQuantity(new Quantity()
          .setValue(worstRespiratoryRate)
          .setUnit("Lungs Volume per minute"))
        .setCode(new CodeableConcept()
          .setText("Worst Respiratory Rate Observation"))
        .setEncounter(new Reference()
          .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(worstRespiratoryRateObservation);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Worst Respiratory Rate Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + worstRespiratoryRateObservation.getId())));
    }
  }

  private void addAnamnesi(JsonObject anamnesisJsonObject, Encounter encounter, List<DomainResource> domainResources) {

    Boolean antiplatelets = anamnesisJsonObject.getBoolean("antiplatelets");
    Boolean anticoagulants = anamnesisJsonObject.getBoolean("anticoagulant");
    Boolean nao = anamnesisJsonObject.getBoolean("nao");
    Procedure anamnesisProcedure = new Procedure()
      .setId(UUID.randomUUID().toString())
      .setSubject(encounter.getPatient())
      .setEncounter(new Reference()
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()));
    DocumentReference documentReference = new DocumentReference();
    if (antiplatelets != null && antiplatelets) {

      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("Anti-aggreganti utilizzati")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    } else if (antiplatelets != null) {
      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("Anti-aggreganti non utilizzati")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    }
    if (anticoagulants != null && anticoagulants) {
      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("Sono utilizzati gli anticoagulanti")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    } else if (anticoagulants != null) {
      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("Non sono utilizzati gli anticoagulanti")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    }
    if (nao != null && nao) {
      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("I nuovi anticoagulanti orali sono usati")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    } else if (nao != null) {
      String documentReferenceUUid = UUID.randomUUID().toString();
      documentReference
        .setId(documentReferenceUUid)
        .addNewDocumentContent(new DocumentReferenceContent()
          .setAttachment(new Attachment()
            .setContentType("text/plain")
            .setLanguage("it")
            .setData("I nuovi anticoagulanti orali non sono usati")
            .setTitle("Anamnesis")
            .setCreation(LocalDate.now().toString())));
    }
    anamnesisProcedure
      .addNewContained(documentReference)
      .addNewReport(new Reference()
        .setDisplay("Anamnesis report")
        .setReference("#" + documentReference.getId()));

    domainResources.add(anamnesisProcedure);

  }

  private void addMajorTraumaCriteria(JsonObject majorTraumaCriteria, Condition condition) {
    Boolean dynamic = majorTraumaCriteria.getBoolean("dynamic");
    Boolean physiological = majorTraumaCriteria.getBoolean("physiological");
    Boolean anatomical = majorTraumaCriteria.getBoolean("anatomical");

    if (dynamic != null && dynamic) {

      condition.addNewConditionStage(new ConditionStage()
        .setType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("Dynamic"))
          .setText("Major Trauma Criteria - Dynamic")));
    }
    if (anatomical != null && anatomical) {

      condition.addNewConditionStage(new ConditionStage()
        .setType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("Anatomical"))
          .setText("Major Trauma Criteria - Anatomical")));
    }
    if (physiological != null && physiological) {


      condition.addNewConditionStage(new ConditionStage()
        .setType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("Physiological"))
          .setText("Major Trauma Criteria - Physiological")));
    }


  }

  private void addPeriodToEncounter(Encounter encounterEmergency, String startDate, String startTime, String endDate, String endTime) {
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

  private void addParticipant(JsonObject reportJson, Encounter encounter) {

    String startOperatorId = reportJson.getString("startOperatorId");
    if (startOperatorId != null) {
      EncounterParticipant encounterParticipant = new EncounterParticipant();
      String startOperatorDescription = reportJson.getString("startOperatorDescription");
      encounterParticipant.addNewType(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("PPRF")
          .setDisplay("primary performer")
          .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType"))
        .addNewCoding(new Coding()
          .setDisplay("startOperatorId"))
        .addNewCoding(new Coding()
          .setDisplay("startOperatorDescription"))
        .setText("Trauma Leader"))
        .setIndividual(new Reference()
          //need to create a fhir resource
          .setDisplay(startOperatorDescription));
      encounter.addNewEncounterParticipant(encounterParticipant);
    }

    JsonArray traumaTeamMembers = reportJson.getJsonArray("traumaTeamMembers");
    if (traumaTeamMembers != null && !traumaTeamMembers.isEmpty()) {
      traumaTeamMembers.stream().map(traumaMemberObject -> (String) traumaMemberObject)
        .forEach(traumaMemberString -> {
          EncounterParticipant encounterParticipant = new EncounterParticipant();
          encounterParticipant.addNewType(new CodeableConcept()
            .addNewCoding(new Coding()
              .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
              .setCode("SPRF")
              .setDisplay("secondary performer"))
            .addNewCoding(new Coding()
              .setDisplay("traumaTeamMembers")))
            .setIndividual(new Reference()
              .setDisplay(traumaMemberString));
          encounter.addNewEncounterParticipant(encounterParticipant);
        });
    }
  }

  private void addIssToEncounter(JsonObject iss, Encounter encounter, List<DomainResource> resources) {
    //Condition for iss, referenced in encounter resource
    Condition conditionIssAssessment = new Condition();


    Procedure procedureInjurySeverityScore = new Procedure()
      .setId(UUID.randomUUID().toString())
      .setStatus("completed")
      .setSubject(encounter.getPatient())
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("273533008")
          .setDisplay("Injury Severity Score")
          .setSystem("http://snomed.org")))
      .setOutcome(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("385669000")
          .setSystem("http://www.snomed.org/")));
    resources.add(procedureInjurySeverityScore);
    iss.forEach(entry -> {
      String key = entry.getKey();
      //create a new condition for each body group in the object
      if (!key.equalsIgnoreCase("totalIss")) {
        JsonObject value = (JsonObject) entry.getValue();
        //create a new reference of observation about each of the body part, then create and persist the observation
        value.forEach(entryGroup -> {
          String uuid = UUID.randomUUID().toString();

          Observation observation = new Observation()
            .setEncounter(new Reference()
              .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounter.getId()))
            .addNewPartOfReference(new Reference()
              .setType(ResourceType.PROCEDURE.typeName())
              .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedureInjurySeverityScore.getId()));

          if (!entryGroup.getKey().equalsIgnoreCase("groupTotalIss")) {
            observation.setStatus("final")
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Injury severity score Calculated")
                  .setCode("74471-4")
                  .setUserSelected(true))
//                .addNewCoding(new Coding()
//                  .setDisplay(entryGroup.getKey()))
                .setText("Physical observation for injury severity score"))
              .setId(uuid)
              //set iss value
              .setValueInteger((Integer) entryGroup.getValue())
              //set body site name
              .setBodySite(new CodeableConcept()
                .setText(entryGroup.getKey()));

          } else {
            observation.setStatus("final")
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Injury severity score Calculated")
                  .setCode("74471-4")
                  .setUserSelected(true))
//                .addNewCoding(new Coding()
//                  .setDisplay(entryGroup.getKey()))
                .setText("Physical observation for injury severity score"))
              .setId(uuid)
              //set iss value
              .setValueInteger((Integer) entryGroup.getValue())
              //set body site name
              .setBodySite(new CodeableConcept()
                .setText(entry.getKey()));

          }
          resources.add(observation);
        });
      } else {
        Observation totalIssObservation = new Observation();
        totalIssObservation.setStatus("final")
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Injury severity score Calculated")
              .setCode("74471-4")
              .setUserSelected(true))
            .addNewCoding(new Coding()
              .setDisplay("Group Total Iss"))
//            .addNewCoding(new Coding()
//              .setDisplay("groupTotalIss"))
            .setText("Physical observation for injury severity score"))
          .setId(UUID.randomUUID().toString())
          .addNewPartOfReference(new Reference()
            .setType(ResourceType.PROCEDURE.typeName())
            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedureInjurySeverityScore.getId()))
          //set iss value
          .setValueInteger((Integer) entry.getValue())
          //set body site name
          .setBodySite(new CodeableConcept()
            .setText(entry.getKey()))
          .setEncounter(new Reference()
            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounter.getId()));
        resources.add(totalIssObservation);

      }
    });
    conditionIssAssessment
      .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("118222006")
            .setSystem("https://www.hl7.org/fhir/codesystem-snomedct.html")
            .setDisplay("General finding of observation of patient (finding)"))
//        .addNewCoding(new Coding()
//          .setDisplay("iss"))
      )
      .addNewCategory(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("Encounter Diagnosis")
          .setSystem("https://www.hl7.org/fhir/codesystem-condition-category.html")
          .setCode("encounter-diagnosis")))
      .setClinicalStatus(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("active")
          .setDisplay("Active")
          .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinicalversion4.0.1")))
      .setSubject(encounter.getPatient())
      .addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setUserSelected(true)
            .setDisplay("AIS - Abbreviated injury scale")
            .setCode("273255001")
          ))
        .addNewDetail(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setDisplay("Injury Severity Score Observation")
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedureInjurySeverityScore.getId())));

    resources.add(conditionIssAssessment);


    encounter.addNewDiagnosis(new EncounterDiagnosis()
      .setUse(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("post-op diagnosis")
          .setCode("post-op")))
      .setCondition(new Reference()
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.CONDITION_TYPE + "/"
          + conditionIssAssessment.getId())));


  }

  private void addTraumaInformation(JsonObject traumaInfo, Encounter encounterPreh, Condition traumaCondition, List<DomainResource> domainResources) {

    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();
    String vehicle = traumaInfo.getString("vehicle");
    if (vehicle != null) {
      //It is possible to add a list of all the locations (building, roads etc.) the patient has been
      encounterPreh.addNewLocation(new EncounterLocation()
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
    Integer age = traumaInfo.getInteger("age");

    createPatient(name, surname, gender, age, dob, erDeceased, encounterPreh, domainResources);

    String accidentDate = traumaInfo.getString("accidentDate");
    String accidentTime = traumaInfo.getString("accidentTime");
    String accidentType = traumaInfo.getString("accidentType");
    //creare una condition


    //
    String otherEmergency = traumaInfo.getString("otherEmergency");
    if (otherEmergency != null) {
      encounterPreh.addNewLocation(new EncounterLocation()
        .setLocation(new Reference()
          .setReference(otherEmergency))
        .setStatus("completed"));
      encounterHospitalization.setAdmitSource(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("hosp-trans")
          .setSystem("https://www.hl7.org/fhir/codesystem-encounter-admit-source.html")
          .setDisplay("Transferred from other hospital"))
        .setText(otherEmergency));
    }

    if (admissionCode != null) {
      //set admission code
      encounterPreh.setPriority(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("http://www.eena.it/")
            .setCode(admissionCode)
            .setDisplay("Codice ammissione pronto soccorso"))
//        .addNewCoding(new Coding()
//          .setDisplay("admissionCode"))
      );
    }
    if (code != null) {
      encounterPreh
        .setHospitalization(new EncounterHospitalization()
          .setPreAdmissionIdentifier(new Identifier()
            .setValue(code)));
    }
    if (accidentType != null) {
      traumaCondition
        .addNewConditionStage(new ConditionStage()
          .setType(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay(accidentType))
//            .addNewCoding(new Coding()
//              .setDisplay("accidentType"))
            .setText("Tipo di Incidente")));
    }
    if (accidentDate != null && accidentTime != null) {
      LocalDate startD = LocalDate.parse(accidentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalTime startT = LocalTime.parse(accidentTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
      ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(startD.getYear(), startD.getMonthValue(), startD.getDayOfMonth(), startT.getHour(),
        startT.getMinute(), startT.getSecond(), startT.getNano(), ZoneId.systemDefault());
      traumaCondition.setOnsetDateTime(FhirUtils.fullDateTime.format(finalZonedStartDateTime));
    }

  }

  private void createPatient(String name, String surname, String gender, int age, String dob, String erDeceased, Encounter encounter,
                             List<DomainResource> resources) {


    Patient patient = new Patient()
      .setId(UUID.randomUUID().toString())
      .addNewHumanName(
        new HumanName()
          .setFamily(surname)
          .addNewGiven(name)
      );
    if (dob != null) {
      DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("dd/MM/yy")
        //.appendValueReduced(ChronoField.YEAR,2,2,1930)
        .toFormatter();
      LocalDate date = LocalDate.parse(dob, dateTimeFormatter);
      LocalDate fhirDob = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
      String d = fhirDob.toString();
      patient.setBirthDate(fhirDob.toString());

    }

    if (erDeceased != null) {
      patient.setDeceasedBoolean(Boolean.parseBoolean(erDeceased));
    }
    encounter.setPatient(new Reference()
      .setType("http://hl7.org/fhir/StructureDefinition/Patient")
      .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + patient.getId()));
    resources.add(patient);
  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
