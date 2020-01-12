package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.DomainResource;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.backboneelements.*;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.datatypes.HumanName;
import com.oracolo.fhir.model.datatypes.Identifier;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.model.elements.Annotation;
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
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterAll.getId()));
    Encounter encounterShock = new Encounter()
      //patient initial condition, vital signs
      .setId(UUID.randomUUID().toString())
      .setPartOf(new Reference()
        .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterAll.getId()));

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


    encounterShock.setServiceType(new CodeableConcept()
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


    Condition traumaCondition = new Condition()
      .setId(UUID.randomUUID().toString())
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("417163006")
          .setDisplay("Traumatic AND/OR non-traumatic injury (disorder)")
          .setSystem("http://www.snomed.org/"))
        .setText("Trauma"));

    domainResources.add(traumaCondition);

    encounterPreh.addNewDiagnosis(
      new EncounterDiagnosis()
        .setUse(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("https://www.hl7.org/fhir/codesystem-diagnosis-role.html")
            .setDisplay("Admission diagnosis")
            .setCode("AD")))
        .setCondition(new Reference()
          .setType(ResourceType.CONDITION.typeName())
          .setReference("/" + ResourceType.CONDITION.typeName() + "/" + traumaCondition.getId()))
    );

    Condition patientInitialCondition = new Condition()
      .setId(UUID.randomUUID().toString())
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("417163006")
          .setDisplay("Traumatic AND/OR non-traumatic injury (disorder)")
          .setSystem("http://www.snomed.org/"))
        .setText("Trauma"))
      .setEncounter(new Reference()
        .setDisplay("Encounter shock room")
        .setType(ResourceType.ENCOUNTER.typeName())
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()));

    domainResources.add(patientInitialCondition);

    encounterShock.addNewDiagnosis(
      new EncounterDiagnosis()
        .setUse(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("http://terminology.hl7.org/CodeSystem/diagnosis-role")
            .setDisplay("pre-op diagnosis")
            .setCode("pre-op")))
        .setCondition(new Reference()
          .setType(ResourceType.CONDITION.typeName())
          .setDisplay("Patient Initial Condition")
          .setReference("/" + ResourceType.CONDITION.typeName() + "/" + patientInitialCondition.getId()))
    );


    JsonObject traumaInfo = reportJson.getJsonObject("traumaInfo");
    JsonObject iss = reportJson.getJsonObject("iss");


    addParticipant(reportJson, encounterPreh);
    addParticipant(reportJson, encounterAll);
    addParticipant(reportJson, encounterShock);

    String startDate = reportJson.getString("startDate");
    String startTime = reportJson.getString("startTime");
    String endDate = reportJson.getString("endDate");
    String endTime = reportJson.getString("endTime");

    addPeriodToEncounter(encounterAll, startDate, startTime, endDate, endTime);


    //iss
    Condition issCondition = new Condition()
      .setId(UUID.randomUUID().toString());
    if (iss != null) {
      addIssToEncounterShock(iss, encounterShock, issCondition, domainResources);
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
      addAnamnesi(anamnesisJsonObject, encounterPreh, traumaCondition, domainResources);
    }


    JsonObject preh = reportJson.getJsonObject("preh");
    if (preh != null) {
      addPrehToEncounter(preh, encounterPreh, traumaCondition, domainResources);

    }
    JsonObject patientInitialConditionJsonObject = reportJson.getJsonObject("patientInitialCondition");
    if (patientInitialConditionJsonObject != null) {
      addPatientInitialCondition(patientInitialConditionJsonObject, encounterPreh, patientInitialCondition, domainResources);
    }

    JsonArray events = reportJson.getJsonArray("events");
    if (events != null && events.size() > 0) {
      addEventsToEncounters(events, encounterShock, encounterPreh, domainResources);
    }
    JsonArray vitalSignsObservations = reportJson.getJsonArray("vitalSignsObservations");
    if (vitalSignsObservations != null && vitalSignsObservations.size() > 0) {
      addVitalSignsObservations(vitalSignsObservations, encounterShock, domainResources);
    }


    encounterAll
      .addNewReasonCode(traumaCondition.getCode())
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
        .setDisplay("Ospedale Bufalini"))
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_id")))
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_version")));


    if (encounterPreh.getLocation() != null) {
      encounterPreh.getLocation().forEach(encounterAll::addNewLocation);
    }
    if (encounterShock.getLocation() != null) {
      encounterShock.getLocation().forEach(encounterAll::addNewLocation);
    }
    if (encounterPreh.getDiagnosis() != null) {
      encounterPreh.getDiagnosis().forEach(encounterAll::addNewDiagnosis);
    }
    if (encounterShock.getDiagnosis() != null) {
      encounterShock.getDiagnosis().forEach(encounterAll::addNewDiagnosis);
    }
    traumaCondition.setSubject(encounterAll.getPatient());

    JsonObject ePreh = JsonObject.mapFrom(encounterPreh);
    JsonObject eShock = JsonObject.mapFrom(encounterShock);
    JsonObject eAll = JsonObject.mapFrom(encounterAll);
    JsonObject prehCondition = JsonObject.mapFrom(traumaCondition);
    JsonObject issConditionJsonObject = JsonObject.mapFrom(issCondition);
    JsonObject conditionBeforeShock = JsonObject.mapFrom(patientInitialCondition);
    domainResources.add(encounterAll);
    domainResources.add(encounterShock);
    domainResources.add(encounterPreh);


  }

  private void addVitalSignsObservations(JsonArray vitalSignsObservations, Encounter encounterShock, List<DomainResource> domainResources) {
    vitalSignsObservations.forEach(entry -> {
      JsonObject vitalSignObservation = JsonObject.mapFrom(entry);
    });
  }

  private void addEventsToEncounters(JsonArray events, Encounter encounterShock, Encounter preH, List<DomainResource> domainResources) {

    events.forEach(entry -> {
      JsonObject fullEvent = JsonObject.mapFrom(entry);
      Integer eventId = fullEvent.getInteger("eventId");
      String date = fullEvent.getString("date");
      String time = fullEvent.getString("time");
      String place = fullEvent.getString("place");
      LocalDate startD = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalTime startT = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
      ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(startD.getYear(), startD.getMonthValue(), startD.getDayOfMonth(), startT.getHour(),
        startT.getMinute(), startT.getSecond(), startT.getNano(), ZoneId.systemDefault());
      final String fhirDate = FhirUtils.fullDateTime.format(finalZonedStartDateTime);
      if (place != null) {
        if (place.equalsIgnoreCase("PRE-H")) {
          preH.addNewLocation(new EncounterLocation()
            .setLocation(new Reference()
              .setDisplay(place))
            .setPhysicalType(new CodeableConcept()
              .addNewCoding(new Coding()
                .setCode("ro")
                .setDisplay("Room")
                .setSystem("https://www.hl7.org/fhir/codesystem-location-physical-type.html")))
            .setPeriod(new Period()
              .setStart(fhirDate)));
        } else {
          encounterShock.addNewLocation(new EncounterLocation()
            .setLocation(new Reference()
              .setDisplay(place))
            .setPhysicalType(new CodeableConcept()
              .addNewCoding(new Coding()
                .setCode("ro")
                .setDisplay("Room")
                .setSystem("https://www.hl7.org/fhir/codesystem-location-physical-type.html")))
            .setPeriod(new Period()
              .setStart(fhirDate)));
        }
      }
      String type = fullEvent.getString("type");
      JsonObject content = fullEvent.getJsonObject("response");
      switch (type) {
        case "procedure":
          String procedureId = content.getString("procedureId");
          String procedureDescription = content.getString("procedureDescription");
          String procedureType = content.getString("procedureType");
          String procedureEvent = content.getString("event");
          JsonObject intubation = content.getJsonObject("intubation");
          JsonObject drainage = content.getJsonObject("drainage");
          JsonObject chestTube = content.getJsonObject("chest-tube");

          Procedure procedure = new Procedure().setId(String.valueOf(eventId));
          procedure.setPerformedDateTime(fhirDate);
          if (place != null) {
            procedure
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H")) {
              preH.addNewContained(procedure);

            } else {
              encounterShock.addNewContained(procedure);
            }
          }
          if (procedureId != null) {
            procedure.setCode(new CodeableConcept()
              .setText(procedureId));
          }
          if (procedureDescription != null) {
            procedure.addNewNote(new Annotation());
          }
          if (procedureType != null) {
            procedure.setCategory(new CodeableConcept()
              .setText(procedureType));
          }
          if (procedureEvent != null) {
            if (procedureEvent.equalsIgnoreCase("start")) {
              procedure.setStatus("in-progress");
            } else {
              procedure.setStatus("completed");
            }
          }
          if (intubation != null) {
            intubation.forEach(intubationEntry -> {
              Object value = intubationEntry.getValue();
              String name = intubationEntry.getKey();
              Observation intubationObservation = new Observation()
                .setId(UUID.randomUUID().toString())
                .addNewPartOfReference(new Reference()
                  .setType(ResourceType.PROCEDURE.typeName())
                  .setDisplay("Intubation procedure")
                  .setReference("#" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              procedure
                .addNewContained(intubationObservation);
            });
          }

          if (drainage != null) {
            drainage.forEach(drainageEntry -> {
              Object value = drainageEntry.getValue();
              String name = drainageEntry.getKey();
              Observation intubationObservation = new Observation()
                .setId(UUID.randomUUID().toString())
                .addNewPartOfReference(new Reference()
                  .setType(ResourceType.PROCEDURE.typeName())
                  .setDisplay("Drainage procedure")
                  .setReference("#" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              procedure
                .addNewContained(intubationObservation);
            });
          }

          if (chestTube != null) {
            chestTube.forEach(chestTubeEntry -> {
              Object value = chestTubeEntry.getValue();
              String name = chestTubeEntry.getKey();
              Observation chestTubeObservation = new Observation()
                .setId(UUID.randomUUID().toString())
                .addNewPartOfReference(new Reference()
                  .setType(ResourceType.PROCEDURE.typeName())
                  .setDisplay("Chest Tube procedure")
                  .setReference("#" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              procedure
                .addNewContained(chestTubeObservation);
            });
          }


          break;
        case "diagnostic":

          break;
        case "drug":
          break;
        case "blood-product":
          break;
        case "vital-signs-mon":
          Observation vitalSignObservationContainer = new Observation()
            .setId(String.valueOf(eventId))
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setSystem("http://loinc.org")
                .setDisplay("Vital Signs")
                .setCode("85353-1")));

          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H")) {
              vitalSignObservationContainer
                .setEncounter(new Reference()
                  .setDisplay("Encounter pre-hospitalization")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("#" + preH.getId()));
              preH.addNewContained(vitalSignObservationContainer);

            } else {
              vitalSignObservationContainer
                .setEncounter(new Reference()
                  .setDisplay("Encounter shock room")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("#" + encounterShock.getId()));
              encounterShock.addNewContained(vitalSignObservationContainer);
            }
          }
          content.forEach(vitalSignEntry -> {
            Object value = vitalSignEntry.getValue();
            String name = vitalSignEntry.getKey();
            Observation observation = new Observation()
              .setSubject(encounterShock.getPatient())
              .setId(UUID.randomUUID().toString())
              .setValueQuantity(new Quantity()
                .setValue((Double) value))
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText(name))
              .setValueQuantity(new Quantity()
                .setValue((Double) value));
            observation.setEffectiveDateTime(fhirDate);

            observation
              .addNewPartOfReference(new Reference()
                .setDisplay("Vital Signs Panel Observations")
                .setType(ResourceType.OBSERVATION.typeName())
                .setReference("#" + vitalSignObservationContainer.getId()));
            vitalSignObservationContainer.addNewContained(observation);
          });
          break;
        case "clinical-variation":
          break;
        case "trauma-leader":
          break;
        case "room-in":
        case "room-out":
          Procedure procedureRoomIn = new Procedure().setId(String.valueOf(eventId));
          procedureRoomIn.setPerformedDateTime(fhirDate);
          if (place != null) {
            procedureRoomIn
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H")) {
              procedureRoomIn.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre-hospitalization"));
              preH.addNewContained(procedureRoomIn);

            } else {
              procedureRoomIn.setEncounter(new Reference()
                .setReference("#" + encounterShock.getId())
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter shock room"));
              encounterShock.addNewContained(procedureRoomIn);
            }
          }
          procedureRoomIn
            .setId(String.valueOf(eventId))
            .setCode(new CodeableConcept()
              .setText(type))
            .setStatus("completed");
          break;
        case "patient-accepted":

          Procedure procedureAcceptance = new Procedure().setId(String.valueOf(eventId));
          procedureAcceptance.setPerformedDateTime(fhirDate);
          if (place != null) {
            procedureAcceptance
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H")) {
              preH.addNewContained(procedureAcceptance);

            } else {
              encounterShock.addNewContained(procedureAcceptance);
            }
          }
          procedureAcceptance
            .setId(String.valueOf(eventId))
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setCode("32485007")
                .setDisplay("Hospital admission (procedure)")
                .setSystem("http://www.snomed.org/"))
              .setText(type))
            .setStatus("completed");

          break;
        case "report-reactivation":
          //non fare nulla
          break;
        default:
          //handle media item
          break;
      }
    });
  }

  private void addPatientInitialCondition(JsonObject patientInitialConditionJsonObject, Encounter encounterPreh, Condition patientInitialCondition, List<DomainResource> domainResources) {
    JsonObject clinicalPicture = patientInitialConditionJsonObject.getJsonObject("clinicalPicture");
    JsonObject vitalSigns = patientInitialConditionJsonObject.getJsonObject("vitalSigns");


    if (vitalSigns != null) {

      String temp = vitalSigns.getString("temp");

      if (temp != null) {
        //codice loinc 8310-5, display Body temperature
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(temp)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Body temperature")
              .setCode("8310-5")
              .setSystem("https://loinc.org/"))
            .setText("Oxygen Percentage"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Temperature"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      //frequenza cardiaca
      String hr = vitalSigns.getString("hr");
      if (hr != null) {
        //codice loinc 8867-4, display Heart rate
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(temp)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Heart rate")
              .setCode("8867-4")
              .setSystem("https://loinc.org/"))
            .setText("Heart Rate"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Heart rate"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      //Pressione arteriosa sistolica
      String bp = vitalSigns.getString("bp");
      if (bp != null) {
        //codice loinc 8480-6, display Systolic blood pressure
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(temp)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Systolic blood pressure")
              .setCode("8480-6")
              .setSystem("https://loinc.org/"))
            .setText("Blood Pressure"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Blood pressure"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      //saturazione ossigeno
      String spo2 = vitalSigns.getString("spo2");
      if (spo2 != null) {
        //codice 20564-1, display Oxygen saturation in Blood
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(temp)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Oxygen saturation in Blood")
              .setCode("20564-1")
              .setSystem("https://loinc.org/"))
            .setText("Oxigen Saturation"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Oxygen Saturation"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
      //concetrazione anidride carbonica espirazione (end tidal carbon dioxide)
      String etco2 = vitalSigns.getString("etco2");
      if (etco2 != null) {
        //codice loinc 19889-5, display Carbon dioxide/Gas.total.at end expiration in Exhaled gas
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(temp)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Oxygen saturation in Blood")
              .setCode("20564-1")
              .setSystem("https://loinc.org/"))
            .setText("Oxigen Saturation"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Oxygen Saturation"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
      }
    }
    if (clinicalPicture != null) {
      Integer gcsTotal = clinicalPicture.getInteger("gcsTotal");
      if (gcsTotal != null) {
        Observation observation = new Observation()
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueInteger(gcsTotal)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow Coma Scale")
              .setCode("35088-4")
              .setSystem("https://loinc.org/"))
            .setText("Glasgow Coma Scale Totale"))

          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))

          .setSubject(encounterPreh.getPatient());
        patientInitialCondition
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
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow coma score motor")
              .setCode("9268-4")
              .setSystem("https://loinc.org/"))
            .setText("Glasgow Coma Scale Motor"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        patientInitialCondition
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
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow coma score verbal")
              .setCode("9270-0")
              .setSystem("https://loinc.org/"))
            .setText("Glasgow Coma Scale Verbal"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(observation);

        patientInitialCondition
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
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Glasgow coma score eye opening")
              .setCode("9267-6")
              .setSystem("https://loinc.org/"))
            .setText("Glasgow Coma Scale Eyes"))
          .setEncounter(new Reference()
            .setType(ResourceType.ENCOUNTER.typeName())
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        patientInitialCondition
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
          .setStatus("final")
          .setValueBoolean(sedated)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Sedated (finding)")
              .setCode("17971005 ")
              .setSystem("http://www.snomed.org/"))
            .setText("Sedated Patient"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);
        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(pupils)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Eye Physical findings panel")
              .setCode("79897-5")
              .setSystem("https://loinc.org/"))
            .setText("Pupils"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(airway)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Respiration rhythm")
              .setCode("9304-7")
              .setSystem("https://loinc.org/"))
            .setText("Airways"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .setText("Positive Inhalation"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(observation);

        patientInitialCondition
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
          .setEncounter(new Reference()
            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(intubationFailedProcedure);

        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(chestTube)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("264957007")
              .setDisplay("Insertion of pleural tube drain (procedure)")
              .setSystem("http://www.snomed.org/"))
            .setText("Chest Tube"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(chestTubeObservation);

        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueString(chestTube)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setDisplay("Inhaled oxygen concentration")
              .setCode("3150-0")
              .setSystem("https://loinc.org/"))
            .setText("Oxygen Percentage"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());

        domainResources.add(oxygenPercentageObservation);
        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("131148009")
              .setDisplay("Bleeding (finding)")
              .setSystem("http://www.snomed.org/"))
            .setText("External Hemorrhage"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(hemorrhageObservation);
        patientInitialCondition
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
        patientInitialCondition
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
          .setId(UUID.randomUUID().toString())
          .setStatus("final")
          .setValueBoolean(positiveInhalation)
          .setCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setCode("52329006")
              .setDisplay("Fracture, open (morphologic abnormality)")
              .setSystem("http://www.snomed.org/"))
            .setText("Fracture Exposition"))
          .setEncounter(new Reference()
            .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
          .setSubject(encounterPreh.getPatient());


        domainResources.add(fractureExpositionObservation);
        patientInitialCondition
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
        patientInitialCondition
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
        .setPhysicalType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("si")
            .setDisplay("Site")
            .setSystem("https://www.hl7.org/fhir/codesystem-location-physical-type.html"))
          .setText("Territorial Area"))
        .setLocation(new Reference()
          .setDisplay(territorialArea)));
    }

    if (isCarAccident != null && isCarAccident) {
      CodeableConcept codeableConcept = new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("418399005")
          .setDisplay("Motor vehicle accident")
          .setSystem("http://www.snomed.org/"))
        .setText("Car accident");
      CodeableConcept traumaCode = traumaCondition.getCode();
      if (traumaCode != null) {
        traumaCode
          .addNewCoding(new Coding()
            .setCode("418399005")
            .setDisplay("Motor vehicle accident")
            .setSystem("http://www.snomed.org/"));
      } else {
        traumaCondition.setCode(codeableConcept);
      }
    }
    //procedure
    if (bPleuralDecompression != null && bPleuralDecompression) {
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("91602002")
            .setDisplay("Thoracentesis (procedure)")
            .setSystem("http://www.snomed.org/"))
          .setText("Pleural Decompression"))
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(new Reference()
          .setReference(encounterPreh.getPatient().getReference()));

      domainResources.add(procedure);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId())));
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
            .setSystem("http://www.snomed.org/"))
          .setText("Blood Protocol"))
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());

      domainResources.add(procedure);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId())));

    }
    if (cTpod != null && cTpod) {
      Procedure procedure = new Procedure();
      procedure.setStatus("completed")
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("771392003")
            .setDisplay("Stability of joint structure of pelvic girdle")
            .setSystem("http://www.snomed.org/"))
          .setText("Tpod Responder"))
        .addNewUsedCode(new CodeableConcept()
          .setText("T-Pod Responder"))
        .setEncounter(new Reference()
          .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());

      domainResources.add(procedure);

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId())));

    }
    //osservazioni
    if (dAnisocoria != null && dAnisocoria) {
      Observation observationAnisocoria = new Observation();
      observationAnisocoria
        .setId(UUID.randomUUID().toString())
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("13045009")
            .setSystem("http://www.snomed.org/")
            .setDisplay("Anisocoria (disorder)"))
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
          .addNewCoding(new Coding()
            .setCode("404649002")
            .setSystem("http://www.snomed.org/")
            .setDisplay("Traumatic mydriasis (disorder)"))
          .setText("Mydriasis"))
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
          .addNewCoding(new Coding()
            .setCode("398598008")
            .setSystem("http://www.snomed.org/")
            .setDisplay("Motility (observable entity)"))
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
            .setText("Motility Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationMotility.getId())));
    }
    if (dGcsTotal != null) {
      Observation dGcs = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setValueInteger(dGcsTotal)
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setDisplay("Glasgow Coma Scale")
            .setCode("35088-4")
            .setSystem("https://loinc.org/"))
          .setText("Glasgow Coma Scale Total"))
        .setEncounter(new Reference()
          .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient());
      domainResources.add(dGcs);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Glasgow Coma Scale Total"))
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
          .addNewCoding(new Coding()
            .setDisplay("Systolic blood pressure")
            .setCode("8480-6")
            .setSystem("https://loinc.org/"))
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
          .addNewCoding(new Coding()
            .setDisplay("Respiratory rate")
            .setCode("9279-1")
            .setSystem("https://loinc.org/"))
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

  private void addAnamnesi(JsonObject anamnesisJsonObject, Encounter encounter, Condition traumaCondition, List<DomainResource> domainResources) {

    Boolean antiplatelets = anamnesisJsonObject.getBoolean("antiplatelets");
    Boolean anticoagulants = anamnesisJsonObject.getBoolean("anticoagulants");
    Boolean nao = anamnesisJsonObject.getBoolean("nao");
    Procedure anamnesisProcedure = new Procedure()
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setSystem("http://www.snomed.org/")
          .setDisplay("Information gathering (procedure)")
          .setCode("311791003")
        )
        .setText("Anamnesis")
      ).setStatus("completed")
      .setId(UUID.randomUUID().toString())
      .setSubject(encounter.getPatient())
      .setEncounter(new Reference()
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()));
    if (anticoagulants != null || nao != null || antiplatelets != null) {
      traumaCondition.addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .setText("Anamnesis evidence"))
        .addNewDetail(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + anamnesisProcedure.getId())));
      domainResources.add(anamnesisProcedure);
    }
    if (antiplatelets != null) {
      Observation observation = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("29497-5")
            .setDisplay("Platelet associated IgG Ab [Presence] in Blood by Flow cytometry (FC)")
            .setSystem("https://loinc.org/"))
          .setText("Use of anti-platelets"))
        .setValueBoolean(antiplatelets)
        .setEncounter(new Reference()
          .setType("Encounter")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + anamnesisProcedure.getId()));
      domainResources.add(observation);
//      String documentReferenceUUid = UUID.randomUUID().toString();
//      documentReference
//        .setId(documentReferenceUUid)
//        .addNewDocumentContent(new DocumentReferenceContent()
//          .setAttachment(new Attachment()
//            .setContentType("text/plain")
//            .setLanguage("it")
//            .setData("Anti-aggreganti utilizzati")
//            .setTitle("Anamnesis")
//            .setCreation(LocalDate.now().toString())));
    }
    if (anticoagulants != null) {
      Observation observation = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("89063-2")
            .setDisplay("Type of anticoagulant medication used")
            .setSystem("https://loinc.org/"))
          .setText("Use of anti-platelets"))
        .setValueBoolean(anticoagulants)
        .setEncounter(new Reference()
          .setType("Encounter")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE + "/" + anamnesisProcedure.getId()));
      domainResources.add(observation);
    }
    if (nao != null) {
      Observation observation = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("89063-2")
            .setDisplay("Type of anticoagulant medication used")
            .setSystem("https://loinc.org/"))
          .setText("Use of new oral anti-coagulants"))
        .setValueBoolean(nao)
        .setEncounter(new Reference()
          .setType("Encounter")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE + "/" + anamnesisProcedure.getId()));
      domainResources.add(observation);
    }


  }

  private void addMajorTraumaCriteria(JsonObject majorTraumaCriteria, Condition condition) {
    Boolean dynamic = majorTraumaCriteria.getBoolean("dynamic");
    Boolean physiological = majorTraumaCriteria.getBoolean("physiological");
    Boolean anatomical = majorTraumaCriteria.getBoolean("anatomical");

    if (dynamic != null && dynamic) {


      condition.addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setDisplay("Dynamic")
            .setCode("229027002")
            .setSystem("http://www.snomed.org/"))
          .setText("Major Trauma Criteria - Dynamic")));
    }
    if (anatomical != null && anatomical) {

      condition.addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setDisplay("Anatomical ")
            .setCode("36298004")
            .setSystem("http://www.snomed.org/"))
          .setText("Major Trauma Criteria - Anatomical")));
    }
    if (physiological != null && physiological) {
      condition.addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setDisplay("Physiological")
            .setCode("1360005")
            .setSystem("http://www.snomed.org/"))
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

  private void addIssToEncounterShock(JsonObject iss, Encounter encounter, Condition conditionIssAssessment, List<DomainResource> resources) {
    //Condition for iss, referenced in encounter resource
    conditionIssAssessment
      .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("417746004")
            .setSystem("https://www.hl7.org/fhir/codesystem-snomedct.html")
            .setDisplay("Traumatic injury"))
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
      .setSubject(encounter.getPatient());

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
              .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounter.getId()));

          resources.add(observation);

          if (!entryGroup.getKey().equalsIgnoreCase("groupTotalIss")) {
            observation.setStatus("final")
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Injury severity score Calculated")
                  .setCode("74471-4")
                  .setUserSelected(true))
                .setText("Physical observation for injury severity score"))
              .setId(uuid)
              //set iss value
              .setValueInteger((Integer) entryGroup.getValue())
              //set body site name
              .setBodySite(new CodeableConcept()
                .setText(entryGroup.getKey()));

            conditionIssAssessment
              .addNewConditionStage(new ConditionStage()
                .setType(new CodeableConcept()
                  .addNewCoding(new Coding()
                    .setCode("273533008")
                    .setDisplay("Injury severity score (assessment scale)")
                    .setSystem("http://www.snomed.org/")))
                .addNewAssessment(new Reference()
                  .setDisplay(entryGroup.getKey() + " score: " + entryGroup.getValue())
                  .setType(ResourceType.OBSERVATION.typeName())
                  .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));

          } else {
            observation.setStatus("final")
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Injury severity score Calculated")
                  .setCode("74471-4")
                  .setUserSelected(true))
                .setText("Group Total Iss  " + entry.getKey() + " Score"))
              .setId(uuid)
              //set iss value
              .setValueInteger((Integer) entryGroup.getValue())
              //set body site name
              .setBodySite(new CodeableConcept()
                .setText(entry.getKey()));

            conditionIssAssessment
              .addNewConditionStage(new ConditionStage()
                .setType(new CodeableConcept()
                  .addNewCoding(new Coding()
                    .setCode("273533008")
                    .setDisplay("Injury severity score (assessment scale)")
                    .setSystem("http://www.snomed.org/")))
                .addNewAssessment(new Reference()
                  .setDisplay("Group Total Iss  " + entry.getKey() + " score: " + entryGroup.getValue())
                  .setType(ResourceType.OBSERVATION.typeName())
                  .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));

          }
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
              .setDisplay("Total Iss"))
            .setText("Physical observation for injury severity score"))
          .setId(UUID.randomUUID().toString())

          .setValueInteger((Integer) entry.getValue())
          //set body site name
          .setBodySite(new CodeableConcept()
            .setText(entry.getKey()))
          .setEncounter(new Reference()
            .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounter.getId()));
        resources.add(totalIssObservation);

        conditionIssAssessment
          .addNewConditionStage(new ConditionStage()
            .setType(new CodeableConcept()
              .addNewCoding(new Coding()
                .setCode("273533008")
                .setDisplay("Injury severity score (assessment scale)")
                .setSystem("http://www.snomed.org/")))
            .addNewAssessment(new Reference()
              .setDisplay("Total Iss Score: " + entry.getValue())
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + totalIssObservation.getId())));

      }
    });


    resources.add(conditionIssAssessment);


    encounter.addNewDiagnosis(new EncounterDiagnosis()
      .setUse(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("post-op diagnosis")
          .setCode("post-op")))
      .setCondition(new Reference()
        .setType(ResourceType.CONDITION.typeName())
        .setDisplay("Injury Severity Score")
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

    String code = traumaInfo.getString("code");
    //dimissione ospedaliera
    String sdo = traumaInfo.getString("sdo");
    String admissionCode = traumaInfo.getString("admissionCode");
    Boolean erDeceased = traumaInfo.getBoolean("erDeceased");

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
        .setStatus("completed")
        .setPhysicalType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("si")
            .setDisplay("Site")
            .setSystem("https://www.hl7.org/fhir/codesystem-location-physical-type.html"))
          .setText("Other Emergency")));
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
      );
    }
    if (code != null) {
      encounterHospitalization
        .setPreAdmissionIdentifier(new Identifier()
          .setValue(code));
    }
    if (sdo != null) {
      encounterHospitalization
        .setDischargeDisposition(new CodeableConcept()
          .setText(sdo));
    }
    if (accidentType != null) {
      Observation accidentTypeObservation = new Observation()
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("67494-5")
            .setDisplay("General mechanism of the forces which caused the injury (observation)")
            .setSystem("https://loinc.org/"))
          .setText("Accident Type Observation"))
        .setId(UUID.randomUUID().toString())
        .setEncounter(new Reference()
          .setReference("/" + FhirUtils.ENCOUNTER_TYPE + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getPatient())
        .setStatus("final")
        .setValueString(accidentType);

      CodeableConcept typeOfAccidentCodePenetr = new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("Penetrating wound")
          .setCode("262560006")
          .setSystem("http://www.snomed.org/"))
        .setText("Type of accident " + accidentType);
      CodeableConcept typeOfAccidentCodeClose = new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("Closed wound")
          .setCode("416886008")
          .setSystem("http://www.snomed.org/"))
        .setText("Type of accident " + accidentType);
      domainResources.add(accidentTypeObservation);
      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(accidentType.equalsIgnoreCase("chiuso") ? typeOfAccidentCodeClose : typeOfAccidentCodePenetr)
          .addNewDetail(new Reference()
            .setType(ResourceType.OBSERVATION.typeName())
            .setDisplay("Type of accident " + accidentType)
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + accidentTypeObservation.getId())));
    }
    if (accidentDate != null && accidentTime != null) {
      LocalDate startD = LocalDate.parse(accidentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalTime startT = LocalTime.parse(accidentTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
      ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(startD.getYear(), startD.getMonthValue(), startD.getDayOfMonth(), startT.getHour(),
        startT.getMinute(), startT.getSecond(), startT.getNano(), ZoneId.systemDefault());
      traumaCondition.setOnsetDateTime(FhirUtils.fullDateTime.format(finalZonedStartDateTime));
    }

    encounterPreh.setHospitalization(encounterHospitalization);


  }

  private void createPatient(String name, String surname, String gender, int age, String dob, Boolean erDeceased, Encounter encounter,
                             List<DomainResource> resources) {


    surname = surname == null ? "" : surname;
    name = name == null ? "" : name;

    Patient patient = new Patient()
      .setId(UUID.randomUUID().toString())
      .addNewHumanName(
        new HumanName()
          .setFamily(surname)
          .addNewGiven(name)
      );
    //parsing date problems
    if (dob != null) {
//      DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
//        .appendPattern("dd/MM/yy")
//        //.appendValueReduced(ChronoField.YEAR,2,2,1930)
//        .toFormatter();
//      LocalDate date = LocalDate.parse(dob, dateTimeFormatter);
//      LocalDate fhirDob = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
//      String d = fhirDob.toString();
//      patient.setBirthDate(fhirDob.toString());

    }

    if (erDeceased != null) {
      patient.setDeceasedBoolean(erDeceased);
    }
    if (gender != null) {
      patient
        .setGender(gender);
    }
    encounter.setPatient(new Reference()
      .setType(ResourceType.PATIENT.typeName())
      .setReference(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + FhirUtils.PATIENT_TYPE + "/" + patient.getId())
      .setDisplay(name + " " + surname));
    resources.add(patient);
  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
