package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.model.Resource;
import com.oracolo.fhir.model.aggregations.AggregationType;
import com.oracolo.fhir.model.backboneelements.*;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.datatypes.*;
import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResourceType;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TraumaTracker extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(FhirServer.class.getName());


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router t4cRouter = Router.router(vertx);
    t4cRouter.route().handler(BodyHandler.create());
    t4cRouter.get("/" + FhirUtils.TRAUMATRACKER_BASE + "/welcome").handler(this::handleWelcome);
    t4cRouter.post("/" + FhirUtils.TRAUMATRACKER_BASE + "/reports").handler(this::handleReports);

    createAPIServer(0, t4cRouter)
      .compose(httpServer -> {
        int port = httpServer.actualPort();
        LOGGER.info("T4C interface listening at " + port);
        return publishHTTPEndPoint(port, FhirUtils.T4CSERVICE, FhirUtils.LOCALHOST, FhirUtils.TRAUMATRACKER_BASE);
      }).setHandler(publishSuccessful -> {
      if (publishSuccessful.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(publishSuccessful.cause());
      }
    });
  }

  private void handleReports(RoutingContext routingContext) {
    LOGGER.info("handling reports");
    List<Resource> domainResources = new ArrayList<>();
    //Main json
    JsonObject reportJson = routingContext.getBodyAsJson();

    Encounter encounterAll = new Encounter()
      .setId(UUID.randomUUID().toString());
    Encounter encounterPreh = new Encounter()
      .setId(UUID.randomUUID().toString())
      .setPartOf(new Reference()
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterAll.getId()));
    Encounter encounterIntervention = new Encounter()
      //patient initial condition, vital signs
      .setId(UUID.randomUUID().toString())
      .setPartOf(new Reference()
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterAll.getId()));

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


    encounterIntervention.setServiceType(new CodeableConcept()
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
        .setText("Trauma Condition gestione pre ospedaliera"))
      .setEncounter(new Reference()
        .setType(ResourceType.ENCOUNTER.typeName())
        .setDisplay("Encounter pre ospedalizzazione")
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()));

    domainResources.add(traumaCondition);

    encounterPreh.addNewDiagnosis(
      new EncounterDiagnosis()
        .setUse(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("https://www.hl7.org/fhir/codesystem-diagnosis-role.html")
            .setDisplay("Admission diagnosis")
            .setCode("AD"))
          .setText("Trauma Condition"))
        .setCondition(new Reference()
          .setDisplay("Trauma Condition")
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
        .setText("Patient Initial Condition"))
      .setEncounter(new Reference()
        .setDisplay("Encounter intervention")
        .setType(ResourceType.ENCOUNTER.typeName())
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterIntervention.getId()));

    domainResources.add(patientInitialCondition);

    encounterIntervention.addNewDiagnosis(
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
    addParticipant(reportJson, encounterIntervention);

    String startDate = reportJson.getString("startDate");
    String startTime = reportJson.getString("startTime");
    String endDate = reportJson.getString("endDate");
    String endTime = reportJson.getString("endTime");

    addPeriodToEncounter(encounterAll, startDate, startTime, endDate, endTime);


    //iss
    Condition issCondition = new Condition()
      .setId(UUID.randomUUID().toString());
    if (iss != null) {
      addIssToEncounterShock(iss, encounterIntervention, issCondition, domainResources);
    }

    //aggiunta della final destination del encounter all
    String finalDestination = reportJson.getString("finalDestination");
    encounterAll.setHospitalization(new EncounterHospitalization()
      .setDestination(new Reference()
        .setDisplay(finalDestination)));

    Patient patient = null;
    if (traumaInfo != null) {
      patient = addTraumaInformation(traumaInfo, encounterPreh, encounterIntervention, traumaCondition, domainResources);
    }
    Reference patientReference = encounterPreh.getSubject();


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
      addPatientInitialCondition(patientInitialConditionJsonObject, encounterIntervention, patientInitialCondition, domainResources);
    }

    JsonArray events = reportJson.getJsonArray("events");
    if (events != null && events.size() > 0) {
      addEventsToEncounters(events, encounterIntervention, encounterPreh);
    }
    JsonArray vitalSignsObservations = reportJson.getJsonArray("vitalSignsObservations");
    if (vitalSignsObservations != null && vitalSignsObservations.size() > 0) {
      addVitalSignsObservations(vitalSignsObservations, encounterIntervention, patientReference);
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
    encounterPreh
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_id")))
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_version")));
    encounterIntervention
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_id")))
      .addNewIdentifier(new Identifier()
        .setValue(reportJson.getString("_version")));

    if (encounterPreh.getLocation() != null) {
      encounterPreh.getLocation().forEach(encounterAll::addNewLocation);
    }
    if (encounterIntervention.getLocation() != null) {
      encounterIntervention.getLocation().forEach(encounterAll::addNewLocation);
    }
    if (encounterPreh.getDiagnosis() != null) {
      encounterPreh.getDiagnosis().forEach(encounterAll::addNewDiagnosis);
    }
    if (encounterIntervention.getDiagnosis() != null) {
      encounterIntervention.getDiagnosis().forEach(encounterAll::addNewDiagnosis);
    }
    if (encounterIntervention.getContained() != null) {
      encounterIntervention.getContained().forEach(encounterAll::addNewContained);
    }

    if (encounterPreh.getContained() != null) {
      encounterPreh.getContained().forEach(encounterAll::addNewContained);
    }
    encounterAll.setSubject(patientReference);
    encounterAll.setMeta(new Metadata()
      .setVersionId(UUID.randomUUID().toString())
      .setLastUpdated(Instant.now()));

    domainResources.add(encounterIntervention.setSubject(patientReference));
    domainResources.add(encounterPreh);

    Promise<JsonObject> aggregationEncounterPromise = Promise.promise();
    List<JsonObject> resourceToAggregate = domainResources
      .stream()
      .map(JsonObject::mapFrom)
      .peek(json -> json.put("meta", JsonObject.mapFrom(new Metadata()
        .setLastUpdated(Instant.now())
        .setVersionId(UUID.randomUUID().toString()))))
      .collect(Collectors.toList());
    DatabaseService service = DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS);

    service
      .createAggregationResource(AggregationType.ENCOUNTER, JsonObject.mapFrom(encounterAll), resourceToAggregate, aggregationEncounterPromise);

    //add resource to save on db
    resourceToAggregate.add(JsonObject.mapFrom(encounterAll));


    addResourcesOnDatabase(service, resourceToAggregate, patientReference, patient);

    aggregationEncounterPromise
      .future()
      .onSuccess(aggregationEncounterJson -> routingContext.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.CREATED.code()).end(JsonObject.mapFrom(aggregationEncounterJson).encodePrettily()))
      .onFailure(throwable -> routingContext.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(JsonObject.mapFrom(new OperationOutcome()
          .addNewIssue(new OperationOutcomeIssue()
            .setCode("error")
            .setDiagnostics(throwable.getMessage()))).toBuffer()));
  }

  private void addResourcesOnDatabase(DatabaseService databaseService, List<JsonObject> domainResources, Reference patientReference, Patient patient) {
    JsonObject patientJson = JsonObject.mapFrom(patient);
    List<JsonObject> observationsJson = domainResources
      .stream()
      .filter(resource -> resource.getString("resourceType").equals(ResourceType.OBSERVATION.typeName()))
      .peek(json -> json.put("subject", JsonObject.mapFrom(patientReference)))
      .map(JsonObject::mapFrom)
      .collect(Collectors.toList());
    List<JsonObject> proceduresJson = domainResources
      .stream()
      .filter(resource -> resource.getString("resourceType").equals(ResourceType.PROCEDURE.typeName()))
      .map(JsonObject::mapFrom)
      .peek(json -> json.put("subject", JsonObject.mapFrom(patientReference)))
      .collect(Collectors.toList());
    List<JsonObject> encountersJson = domainResources
      .stream()
      .filter(resource -> resource.getString("resourceType").equals(ResourceType.ENCOUNTER.typeName()))
      .map(JsonObject::mapFrom)
      .peek(json -> json.put("subject", JsonObject.mapFrom(patientReference)))
      .collect(Collectors.toList());
    List<JsonObject> conditionsJson = domainResources
      .stream()
      .filter(resource -> resource.getString("resourceType").equals(ResourceType.CONDITION.typeName()))
      .map(JsonObject::mapFrom)
      .peek(json -> json.put("subject", JsonObject.mapFrom(patientReference)))
      .collect(Collectors.toList());


    Promise<JsonObject> encountersBulkOperationsPromise = Promise.promise();
    Promise<JsonObject> procedureBulkOperationsPromise = Promise.promise();
    Promise<JsonObject> observationsBulkOperationsPromise = Promise.promise();
    Promise<JsonObject> conditionsBulkOperationsPromise = Promise.promise();
    databaseService
      .executeWriteBulkOperations(ResourceType.ENCOUNTER.getCollection(), encountersJson, encountersBulkOperationsPromise)
      .executeWriteBulkOperations(ResourceType.OBSERVATION.getCollection(), observationsJson, observationsBulkOperationsPromise)
      .executeWriteBulkOperations(ResourceType.CONDITION.getCollection(), conditionsJson, conditionsBulkOperationsPromise)
      .executeWriteBulkOperations(ResourceType.PROCEDURE.getCollection(), proceduresJson, procedureBulkOperationsPromise);


    //put each resource in every single collection
    CompositeFuture.all(encountersBulkOperationsPromise.future(),
      procedureBulkOperationsPromise.future(),
      observationsBulkOperationsPromise.future(), conditionsBulkOperationsPromise
        .future()).setHandler(handler -> {
      if (handler.failed()) {
        handler.cause().printStackTrace();
      }
    });
  }

  private void addVitalSignsObservations(JsonArray vitalSignsObservations, Encounter encounterShock, Reference patientReference) {
    vitalSignsObservations.forEach(entry -> {
      JsonObject vitalSignObservation = JsonObject.mapFrom(entry);
      String timestamp = vitalSignObservation.getString("timestamp");

      String source = vitalSignObservation.getString("source");
      JsonArray vitalsigns = vitalSignObservation.getJsonArray("vitalsigns");

      LocalDateTime localDateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(localDateTime.getYear(),
        localDateTime.getMonthValue(),
        localDateTime.getDayOfMonth(),
        localDateTime.getHour(),
        localDateTime.getMinute(),
        localDateTime.getSecond(),
        localDateTime.getNano(), ZoneId.systemDefault());
      final String fhirDate = FhirUtils.fullDateTime.format(finalZonedStartDateTime);
      Observation vitalSignObservationContainer = new Observation().setId(UUID.randomUUID().toString())
        .setStatus("final")
        .addNewIdentifier(new Identifier()
          .setValue(source))
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("http://loinc.org")
            .setDisplay("Vital Signs")
            .setCode("85353-1")))
        .setEffectiveDateTime(fhirDate)
        .setEncounter(new Reference()
          .setType(ResourceType.ENCOUNTER.typeName())
          .setDisplay("Encounter intervention")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
        .setSubject(patientReference);

      encounterShock.addNewContained(vitalSignObservationContainer);
      vitalsigns.forEach(vitalSignMeasurementObject -> {
        JsonObject measurementJson = JsonObject.mapFrom(vitalSignMeasurementObject);
        String uom = measurementJson.getString("uom");
        String type = measurementJson.getString("type");
        String value = measurementJson.getString("value");
        vitalSignObservationContainer.addNewObservationComponent(new ObservationComponent()
          .setCode(new CodeableConcept()
            .setText(type))
          .setValueQuantity(new Quantity()
            .setUnit(uom)
            .setValue(Double.parseDouble(value))));

      });
    });
  }

  private void addEventsToEncounters(JsonArray events, Encounter intervention, Encounter preH) {

    events.forEach(entry -> {
      JsonObject fullEvent = JsonObject.mapFrom(entry);
      Integer eventId = fullEvent.getInteger("eventId");
      String date = fullEvent.getString("date");
      String time = fullEvent.getString("time");
      String place = fullEvent.getString("place");
      JsonObject content = fullEvent.getJsonObject("content");
      LocalDate startD = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalTime startT = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
      ZonedDateTime finalZonedStartDateTime = ZonedDateTime.of(startD.getYear(), startD.getMonthValue(), startD.getDayOfMonth(), startT.getHour(),
        startT.getMinute(), startT.getSecond(), startT.getNano(), ZoneId.systemDefault());
      final String fhirDate = FhirUtils.fullDateTime.format(finalZonedStartDateTime);
      if (place != null) {
        if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("Trasporto")) {
          preH.addNewLocation(new EncounterLocation()
            .setLocation(new Reference()
              .setDisplay(place))
            .setPeriod(new Period()
              .setStart(fhirDate)));
        } else {
          intervention.addNewLocation(new EncounterLocation()
            .setLocation(new Reference()
              .setDisplay(place))
            .setPeriod(new Period()
              .setStart(fhirDate)));
        }
      }
      String type = fullEvent.getString("type");
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
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              preH.addNewContained(procedure.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre ospedalizzazione")
                .setReference("#" + preH.getId())));

            } else {
              intervention.addNewContained(procedure.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("#" + intervention.getId())));
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
              if (place != null) {
                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  preH.addNewContained(intubationObservation);

                } else {
                  intervention.addNewContained(intubationObservation);
                }
              }
            });
          }

          if (drainage != null) {
            drainage.forEach(drainageEntry -> {
              Object value = drainageEntry.getValue();
              String name = drainageEntry.getKey();
              Observation drainageObservation = new Observation()
                .setId(UUID.randomUUID().toString())
                .addNewPartOfReference(new Reference()
                  .setType(ResourceType.PROCEDURE.typeName())
                  .setDisplay("Drainage procedure")
                  .setReference("#" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              if (place != null) {
                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  preH.addNewContained(drainageObservation);

                } else {
                  intervention.addNewContained(drainageObservation);
                }
              }
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
              if (place != null) {
                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  preH.addNewContained(chestTubeObservation);

                } else {
                  intervention.addNewContained(chestTubeObservation);
                }
              }
            });
          }


          break;
        case "diagnostic":
          String diagnosticId = content.getString("diagnosticId");
          String diagnosticDescription = content.getString("diagnosticDescription");
          Double lactates = content.getDouble("lactates");
          Double be = content.getDouble("be");
          Double ph = content.getDouble("ph");
          Double hb = content.getDouble("hb");
          String fibtem = content.getString("fibtem");
          String extem = content.getString("extem");
          Boolean hyperfibrinolysis = content.getBoolean("hyperfibrinolysis");
          DiagnosticReport diagnosticReport = new DiagnosticReport()
            .setId(String.valueOf(eventId))
            .setEffectiveDateTime(date + " " + time)
            .setCode(new CodeableConcept()
              .setText(diagnosticId))
            .setStatus("final")
            .setConclusion(diagnosticDescription);
          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              diagnosticReport.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setDisplay("Encounter pre ospedalizzazione")
                .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(diagnosticReport);

            } else {
              diagnosticReport.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("#" + intervention));
              intervention.addNewContained(diagnosticReport);
            }
          }
          if (lactates != null) {
            Observation observationLactates = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueQuantity(new Quantity()
                .setValue(lactates))
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("lactates"))
              .setEffectiveDateTime(fhirDate);

            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + observationLactates.getId())
              .setDisplay("Observation lactates")
              .setType(ResourceType.OBSERVATION.typeName()));

            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                observationLactates.setEncounter(new Reference()
                  .setReference("#" + preH.getId())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setType(ResourceType.ENCOUNTER.typeName()));
                preH.addNewContained(observationLactates);
              } else {
                observationLactates.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention));
                intervention.addNewContained(observationLactates);
              }
            }
          }
          if (be != null) {
            Observation observationBe = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueQuantity(new Quantity()
                .setValue(be))
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("be"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + observationBe.getId())
              .setDisplay("Observation be")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                observationBe.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(observationBe);

              } else {
                observationBe.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(observationBe);
              }
            }
          }
          if (ph != null) {
            Observation phObservation = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueQuantity(new Quantity()
                .setValue(ph))
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("ph"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + phObservation.getId())
              .setDisplay("Observation be")
              .setType(ResourceType.OBSERVATION.typeName()));

            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                phObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(phObservation);

              } else {
                phObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(phObservation);
              }
            }

          }
          if (hb != null) {
            Observation hbObservation = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueQuantity(new Quantity()
                .setValue(hb))
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("hb"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("/" + ResourceType.OBSERVATION.typeName() + hbObservation.getId())
              .setDisplay("Observation hb")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                hbObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(hbObservation);

              } else {
                hbObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(hbObservation);
              }
            }

          }
          if (fibtem != null) {
            Observation fibtemObservation = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueString(fibtem)
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("fibtem"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + fibtemObservation.getId())
              .setDisplay("Observation fibtem")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                fibtemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(fibtemObservation);

              } else {
                fibtemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(fibtemObservation);
              }
            }
          }
          if (extem != null) {
            Observation extemObservation = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueString(extem)
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("extem"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + extemObservation.getId())
              .setDisplay("Observation extem")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                extemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(extemObservation);
              } else {
                extemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(extemObservation);
              }
            }

          }
          if (hyperfibrinolysis != null) {
            Observation hyperfibrinolysisObservation = new Observation()
              .setSubject(intervention.getSubject())
              .setId(UUID.randomUUID().toString())
              .setValueBoolean(hyperfibrinolysis)
              .setStatus("final")
              .setCode(new CodeableConcept()
                .setText("hyperfibrinolysis"))
              .setEffectiveDateTime(fhirDate);
            diagnosticReport.addNewResult(new Reference()
              .setReference("#" + hyperfibrinolysisObservation.getId())
              .setDisplay("Observation hyperfibrinolysis")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                hyperfibrinolysisObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("#" + preH.getId()));
                preH.addNewContained(hyperfibrinolysisObservation);

              } else {
                hyperfibrinolysisObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("#" + intervention.getId()));
                intervention.addNewContained(hyperfibrinolysisObservation);
              }
            }
          }

          break;
        case "drug":
          MedicationAdministration drugAdministration = new MedicationAdministration()
            .setId(String.valueOf(eventId));
          String drugId = content.getString("drugId");
          String drugDescription = content.getString("drugDescription");
          Double drugQty = content.getDouble("qty");
          String drugAdministrationType = content.getString("administrationType");
          String drugUnit = content.getString("unit");
          String event = content.getString("start");
          drugAdministration
            .setMedicationCodeableConcept(new CodeableConcept()
              .setText(drugId))
            .addNewNote(new Annotation()
              .setText(drugDescription));
          if (event != null && event.equalsIgnoreCase("stop")) {
            drugAdministration.setStatus("stopped");
          } else if (event != null) {
            drugAdministration.setStatus("in progress");
          }
          MedicationAdministrationDosage drugDosage = new MedicationAdministrationDosage();
          if (drugQty != null && drugUnit != null) {
            drugDosage
              .setDose(new Quantity()
                .setValue(drugQty)
                .setUnit(drugUnit));
          }
          if (drugAdministrationType != null) {
            drugDosage.setMethod(new CodeableConcept()
              .setText(drugAdministrationType));
          }

          drugAdministration.setDosage(drugDosage);
          if (place != null) {
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              drugAdministration
                .setContext(new Reference()
                  .setReference("#" + preH.getId())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(drugAdministration);

            } else {
              drugAdministration
                .setContext(new Reference()
                  .setReference("#" + intervention.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(drugAdministration);
            }
          }

          break;
        case "blood-product":
          MedicationAdministration medicationAdministration = new MedicationAdministration()
            .setId(String.valueOf(eventId));
          String bloodProductId = content.getString("bloodProductId");
          String bloodProductDescription = content.getString("bloodProductDescription");
          Double qty = content.getDouble("qty");
          String administrationType = content.getString("administrationType");
          String unit = content.getString("unit");
          String bagCode = content.getString("bagCode");
          medicationAdministration
            .setMedicationCodeableConcept(new CodeableConcept()
              .setText(bloodProductId))
            .addNewNote(new Annotation()
              .setText(bloodProductDescription));
          MedicationAdministrationDosage dosage = new MedicationAdministrationDosage();
          if (qty != null && unit != null) {
            dosage
              .setDose(new Quantity()
                .setValue(qty)
                .setUnit(unit));
          }
          if (administrationType != null) {
            dosage.setMethod(new CodeableConcept()
              .setText(administrationType));
          }
          if (bagCode != null) {
            dosage.setText(bagCode);
          }
          medicationAdministration.setDosage(dosage);
          if (place != null) {
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              medicationAdministration
                .setContext(new Reference()
                  .setReference("#" + preH.getId())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(medicationAdministration);

            } else {
              medicationAdministration
                .setContext(new Reference()
                  .setReference("#" + intervention.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(medicationAdministration);
            }
          }
          break;
        case "vital-signs-mon":
          Observation vitalSignObservationContainer = new Observation()
            .setId(String.valueOf(eventId))
            .setEffectiveDateTime(fhirDate)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setSystem("http://loinc.org")
                .setDisplay("Vital Signs")
                .setCode("85353-1"))
              .setText("Vital Signs Monitor"));

          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H")) {
              vitalSignObservationContainer
                .setEncounter(new Reference()
                  .setReference("#" + preH.getId())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(vitalSignObservationContainer);

            } else {
              vitalSignObservationContainer
                .setEncounter(new Reference()
                  .setReference("#" + intervention.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(vitalSignObservationContainer);

            }
          }
          content.forEach(vitalSignEntry -> {
            Object value = vitalSignEntry.getValue();
            String name = vitalSignEntry.getKey();
            vitalSignObservationContainer
              .addNewObservationComponent(new ObservationComponent()
                .setValueQuantity(new Quantity()
                  .setValue((Double) value))
                .setCode(new CodeableConcept()
                  .setText(name)));


          });
          break;
        case "clinical-variation":
          String variationId = content.getString("variationId");
          String variationDescription = content.getString("variationDescription");
          String value = content.getString("value");
          Observation clinicalVariation = new Observation()
            .setId(String.valueOf(eventId))
            .setCode(new CodeableConcept()
              .setText(variationId))
            .addNewNote(new Annotation()
              .setText(variationDescription))
            .setValueString(value);

          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H")) {
              clinicalVariation
                .setEncounter(new Reference()
                  .setDisplay("Encounter pre-hospitalization")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("#" + preH.getId()));
              preH.addNewContained(clinicalVariation);

            } else {
              clinicalVariation
                .setEncounter(new Reference()
                  .setReference("#" + intervention.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(clinicalVariation);
            }
          }
          break;
        case "trauma-leader":
          Procedure traumaLeaderProcedure = new Procedure()
            .setSubject(intervention.getSubject())
            .setStatus("completed")
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setSystem("http://www.snomed.org/")
                .setCode("304562007 ")
                .setDisplay("Informing doctor (procedure)"))
              .setText("Trauma Leader"))
            .setPerformedDateTime(fhirDate);
          if (place != null) {
            traumaLeaderProcedure
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasport")) {
              traumaLeaderProcedure.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setDisplay("Encounter pre ospedalizzazione")
                .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(traumaLeaderProcedure);

            } else {
              traumaLeaderProcedure.setEncounter(new Reference()
                .setReference("#" + intervention.getId())
                .setDisplay("Encounter intervention")
                .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(traumaLeaderProcedure);
            }
          }
//          String
          break;
        case "room-in":
        case "room-out":
          Procedure procedureRoomIn = new Procedure();
          if (place != null) {
            procedureRoomIn
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("TRASPORTO")) {
              procedureRoomIn.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setDisplay("Encounter pre ospedalizzazione")
                .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(procedureRoomIn);


            } else {
              procedureRoomIn.setEncounter(new Reference()
                .setReference("#" + intervention.getId())
                .setDisplay("Encounter intervention")
                .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(procedureRoomIn);
            }
          }
          procedureRoomIn
            .setId(String.valueOf(eventId))
            .setCode(new CodeableConcept()
              .setText(type))
            .setStatus("completed")
            .setPerformedDateTime(fhirDate);
          break;
        case "patient-accepted":

          Procedure procedureAcceptance = new Procedure();
          if (place != null) {
            procedureAcceptance
              .setLocation(new Reference()
                .setDisplay(place));
            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("TRASPORTO")) {
              procedureAcceptance.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setDisplay("Encounter pre ospedalizzazione")
                .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(procedureAcceptance);


            } else {
              procedureAcceptance.setEncounter(new Reference()
                .setReference("#" + intervention.getId())
                .setDisplay("Encounter intervention")
                .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(procedureAcceptance);
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
            .setStatus("completed")
            .setSubject(intervention.getSubject())
            .setPerformedDateTime(fhirDate);


          break;
        case "report-reactivation":
          //non fare nulla
          break;
        case "video":
        case "photo":
        case "vocal-note":
        case "text-note":
          Media media = new Media()
            .setId(String.valueOf(eventId))
            .setStatus("preparation")
            .setType(new CodeableConcept()
              .setText(type))
            .setCreatedDateTime(fhirDate);
          if (content != null) {
            String text = content.getString("text");
            media.setContent(new Attachment()
              .setContentType(type)
              .setData(text));
          }

          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              media.setEncounter(new Reference()
                .setReference("#" + preH.getId())
                .setDisplay("Encounter per ospedalizzazione")
                .setType(ResourceType.ENCOUNTER.typeName()));
              preH.addNewContained(media);

            } else {
              media.setEncounter(new Reference()
                .setReference("#" + intervention.getId())
                .setDisplay("Encounter intervention")
                .setType(ResourceType.ENCOUNTER.typeName()));
              intervention.addNewContained(media);
            }
          }
          break;
      }
    });
  }

  private void addPatientInitialCondition(JsonObject patientInitialConditionJsonObject, Encounter encounterShock,
                                          Condition patientInitialCondition, List<Resource> domainResources) {
    JsonObject clinicalPicture = patientInitialConditionJsonObject.getJsonObject("clinicalPicture");
    JsonObject vitalSigns = patientInitialConditionJsonObject.getJsonObject("vitalSigns");


    if (vitalSigns != null) {

      Observation vitalSignObservationContainer = new Observation()
        .setId(UUID.randomUUID().toString())
        .setStatus("final")
        .setCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("http://loinc.org")
            .setDisplay("Vital Signs")
            .setCode("85353-1"))
          .setText("Vital Signs"))
        .setEncounter(new Reference()
          .setType(ResourceType.ENCOUNTER.typeName())
          .setDisplay("Encounter intervention")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
        .setSubject(encounterShock.getSubject());

      domainResources.add(vitalSignObservationContainer);

      patientInitialCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .addNewCoding(new Coding()
              .setSystem("http://loinc.org")
              .setDisplay("Vital Signs")
              .setCode("85353-1"))
            .setText("Patient Initial Condition Vital Sign"))
          .addNewDetail(new Reference()
            .setType(ResourceType.OBSERVATION.typeName())
            .setDisplay("Initial Vital Signs")
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + vitalSignObservationContainer.getId())));


      String temp = vitalSigns.getString("temp");
      if (temp != null) {
        //codice loinc 8310-5, display Body temperature
        vitalSignObservationContainer
          .addNewObservationComponent(new ObservationComponent()
            .setValueString(temp)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Body temperature")
                .setCode("8310-5")
                .setSystem("https://loinc.org/"))
              .setText("Temperatura")));
      }
      //frequenza cardiaca
      String hr = vitalSigns.getString("hr");
      if (hr != null) {
        //codice loinc 8867-4, display Heart rate

        vitalSignObservationContainer
          .addNewObservationComponent(new ObservationComponent()
            .setValueString(hr)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Heart rate")
                .setCode("8867-4")
                .setSystem("https://loinc.org/"))
              .setText("Frequenza cardiaca (hr)")));
      }
      //Pressione arteriosa sistolica
      String bp = vitalSigns.getString("bp");
      if (bp != null) {
        //codice loinc 8480-6, display Systolic blood pressure
        vitalSignObservationContainer
          .addNewObservationComponent(new ObservationComponent()
            .setValueString(bp)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Systolic blood pressure")
                .setCode("8480-6")
                .setSystem("https://loinc.org/"))
              .setText("Pressione Arteriosa Sistolica (bp)"))
          );
      }
      //saturazione ossigeno
      String spo2 = vitalSigns.getString("spo2");
      if (spo2 != null) {
        //codice 20564-1, display Oxygen saturation in Blood
        vitalSignObservationContainer
          .addNewObservationComponent(new ObservationComponent()
            .setValueString(spo2)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Oxygen saturation in Blood")
                .setCode("20564-1")
                .setSystem("https://loinc.org/"))
              .setText("Saturazione ossigeno (spO2)"))
          );
      }
      //concetrazione anidride carbonica espirazione (end tidal carbon dioxide)
      String etco2 = vitalSigns.getString("etco2");
      if (etco2 != null) {
        //codice loinc 19889-5, display Carbon dioxide/Gas.total.at end expiration in Exhaled gas
        vitalSignObservationContainer
          .addNewObservationComponent(new ObservationComponent()
            .setValueString(etco2)
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Oxygen saturation in Blood")
                .setCode("20564-1")
                .setSystem("https://loinc.org/"))
              .setText("Saturazione ossigeno (spO2)"))
          );
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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))

          .setSubject(encounterShock.getSubject());
        patientInitialCondition
          .addNewConditionEvidence(new ConditionEvidence()
            .addNewCode(new CodeableConcept()
              .setText("Glasgow Coma Scale Total"))
            .addNewDetail(new Reference()
              .setType(ResourceType.OBSERVATION.typeName())
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observation.getId())));
        domainResources.add(observation);
        String gcsMotor = clinicalPicture.getString("gcsMotor");
        if (gcsMotor != null) {
          observation.addNewObservationComponent(new ObservationComponent()
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Glasgow coma score motor")
                .setCode("9268-4")
                .setSystem("https://loinc.org/"))
              .setText("Glasgow Coma Scale Motor"))
            .setValueString(gcsMotor));
        }
        String gcsVerbal = clinicalPicture.getString("gcsVerbal");
        if (gcsVerbal != null) {

          observation.addNewObservationComponent(new ObservationComponent()
            .setCode(new CodeableConcept()
              .addNewCoding(new Coding()
                .setDisplay("Glasgow coma score verbal")
                .setCode("9270-0")
                .setSystem("https://loinc.org/"))
              .setText("Glasgow Coma Scale Verbal"))
            .setValueString(gcsVerbal));

        }
        String gcsEyes = clinicalPicture.getString("gcsEyes");
        if (gcsEyes != null) {
          observation
            .addNewObservationComponent(new ObservationComponent()
              .setCode(new CodeableConcept()
                .addNewCoding(new Coding()
                  .setDisplay("Glasgow coma score eye opening")
                  .setCode("9267-6")
                  .setSystem("https://loinc.org/"))
                .setText("Glasgow Coma Scale Eyes"))
              .setValueString(gcsEyes));
        }
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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intevention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());

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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());

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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());


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
            .setType(ResourceType.ENCOUNTER.typeName())
            .setDisplay("Encounter intervention")
            .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterShock.getId()))
          .setSubject(encounterShock.getSubject());

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

  private void addPrehToEncounter(JsonObject preh, Encounter encounterPreh, Condition traumaCondition, List<Resource> domainResources) {
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
          .setDisplay("Territorial Area: " + territorialArea)));
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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(new Reference()
          .setReference(encounterPreh.getSubject().getReference()));

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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());

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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());

      domainResources.add(procedure);

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId())));

    }
    //osservazioni
    if (dAnisocoria != null) {
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
        .setValueBoolean(dAnisocoria)
        .setEncounter(new Reference()
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());

      domainResources.add(observationAnisocoria);

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Anisocoria Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationAnisocoria.getId())));
    }
    if (dMidriasi != null) {
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
        .setValueBoolean(dMidriasi)
        .setEncounter(new Reference()
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());

      domainResources.add(observationMidriasi);

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Midriasi Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationMidriasi.getId())));
    }
    if (eMotility != null) {
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
        .setValueBoolean(eMotility)
        .setEncounter(new Reference()
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());
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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());
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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());

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
          .setDisplay("Encounter pre ospedalizzazione")
          .setType(ResourceType.ENCOUNTER.typeName())
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject());
      domainResources.add(worstRespiratoryRateObservation);

      traumaCondition
        .addNewConditionEvidence(new ConditionEvidence()
          .addNewCode(new CodeableConcept()
            .setText("Worst Respiratory Rate Observation"))
          .addNewDetail(new Reference()
            .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + worstRespiratoryRateObservation.getId())));
    }
  }

  private void addAnamnesi(JsonObject anamnesisJsonObject, Encounter encounter, Condition traumaCondition, List<Resource> domainResources) {

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
      .setSubject(encounter.getSubject())
      .setEncounter(new Reference()
        .setDisplay("Encounter pre ospedalizzazione")
        .setType(ResourceType.ENCOUNTER.typeName())
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()));
    if (anticoagulants != null || nao != null || antiplatelets != null) {
      traumaCondition.addNewConditionEvidence(new ConditionEvidence()
        .addNewCode(new CodeableConcept()
          .addNewCoding(new Coding()
            .setSystem("http://www.snomed.org/")
            .setDisplay("Information gathering (procedure)")
            .setCode("311791003"))
          .setText("Anamnesis evidence"))
        .addNewDetail(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setDisplay("Anamnesi procedure")
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
          .setType(ResourceType.ENCOUNTER.typeName())
          .setDisplay("Encounter pre ospedalizzazione")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + anamnesisProcedure.getId()));
      domainResources.add(observation);
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
          .setText("Use of anticoagulants"))
        .setValueBoolean(anticoagulants)
        .setEncounter(new Reference()
          .setType(ResourceType.ENCOUNTER.typeName())
          .setDisplay("Encounter pre ospedalizzazione")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + anamnesisProcedure.getId()));
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
          .setType(ResourceType.ENCOUNTER.typeName())
          .setDisplay("Encounter pre ospedalizzazione")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
        .addNewPartOfReference(new Reference()
          .setType(ResourceType.PROCEDURE.typeName())
          .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + anamnesisProcedure.getId()));
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
    String startOperatorDescription = reportJson.getString("startOperatorDescription");

    if (startOperatorId != null & startOperatorDescription != null) {
      Practitioner practitioner = new Practitioner()
        .setId(UUID.randomUUID().toString())
        .addNewIdentifier(new Identifier()

          .setValue(startOperatorId))
        .setActive(true)
        .addNewIdentifier(new Identifier()

          .setValue(startOperatorDescription))
        .setActive(true);
      EncounterParticipant encounterParticipant = new EncounterParticipant();
      encounterParticipant.addNewType(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("PPRF")
          .setDisplay("primary performer")
          .setSystem("http://terminology.hl7.org/CodeSystem/v3-ParticipationType"))
        .setText("Trauma Leader"))
        .setIndividual(new Reference()
          .setReference("#" + practitioner.getId())
          .setType(ResourceType.PRACTITIONER.typeName())
          .setDisplay(startOperatorDescription));
      encounter.addNewEncounterParticipant(encounterParticipant)
        .addNewContained(practitioner);


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
            .setText("Trauma team members"))
            .setIndividual(new Reference()
              .setType(ResourceType.PRACTITIONER.typeName())
              .setDisplay(traumaMemberString));
          encounter.addNewEncounterParticipant(encounterParticipant);
        });
    }
  }

  private void addIssToEncounterShock(JsonObject iss, Encounter encounter, Condition conditionIssAssessment, List<Resource> resources) {
    //Condition for iss, referenced in encounter resource
    conditionIssAssessment
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setCode("417746004")
          .setSystem("https://www.hl7.org/fhir/codesystem-snomedct.html")
          .setDisplay("Traumatic injury"))
        .setText("Physical Condition after intervention")
      ).setEncounter(new Reference()
      .setType(ResourceType.ENCOUNTER.typeName())
      .setDisplay("Encounter intervention")
      .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()))
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
      .setSubject(encounter.getSubject());

    Integer totalIssScore = iss.getInteger("totalIss");
    Observation totalIssObservation = new Observation();

    totalIssObservation.setStatus("final")
      .setCode(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("Injury severity score Calculated")
          .setCode("74471-4")
          .setSystem("https://www.loinc.org"))
        .setText("Total Iss"))
      .setId(UUID.randomUUID().toString())
      .setValueInteger(totalIssScore)
      //set body site name
      .setEncounter(new Reference()
        .setDisplay("Encounter intervention")
        .setType(ResourceType.ENCOUNTER.typeName())
        .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounter.getId()));

    conditionIssAssessment
      .addNewConditionStage(new ConditionStage()
        .setType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("273533008")
            .setDisplay("Injury severity score (assessment scale)")
            .setSystem("http://www.snomed.org/")))
        .addNewAssessment(new Reference()
          .setDisplay("Total Iss Score: " + totalIssScore)
          .setType(ResourceType.OBSERVATION.typeName())
          .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + totalIssObservation.getId())));

    resources.add(totalIssObservation);
    resources.add(conditionIssAssessment);
    encounter.addNewDiagnosis(new EncounterDiagnosis()
      .setUse(new CodeableConcept()
        .addNewCoding(new Coding()
          .setDisplay("post-op diagnosis")
          .setCode("post-op")))
      .setCondition(new Reference()
        .setType(ResourceType.CONDITION.typeName())
        .setDisplay("Injury Severity Score")
        .setReference("/" + FhirUtils.BASE + "/" + ResourceType.CONDITION.typeName() + "/"
          + conditionIssAssessment.getId())));

    iss.forEach(entry -> {
      String key = entry.getKey();
      //create a new condition for each body group in the object
      if (!key.equalsIgnoreCase("totalIss")) {
        JsonObject value = (JsonObject) entry.getValue();
        //create a new reference of observation about each of the body part, then create and persist the observation
        value.forEach(entryGroup -> {


          if (!entryGroup.getKey().equalsIgnoreCase("groupTotalIss")) {


            totalIssObservation
              .addNewObservationComponent(new ObservationComponent()
                .setCode(new CodeableConcept()
                  .addNewCoding(new Coding()
                    .setDisplay("Injury severity score Calculated")
                    .setCode("74471-4")
                    .setSystem("https://www.loinc.org"))
                  .setText(entryGroup.getKey() + " iss score"))
                .setValueInteger((Integer) entryGroup.getValue()));

          } else {
            totalIssObservation
              .addNewObservationComponent(new ObservationComponent()
                .setCode(new CodeableConcept()
                  .addNewCoding(new Coding()
                    .setDisplay("Injury severity score Calculated")
                    .setCode("74471-4")
                    .setSystem("https://www.loinc.org"))
                  .setText("Group Total Iss " + entry.getKey() + " iss score"))
                .setValueInteger((Integer) entryGroup.getValue()));

          }
        });
      }
    });


  }

  private Patient addTraumaInformation(JsonObject traumaInfo, Encounter encounterPreh, Encounter encounterShock, Condition traumaCondition, List<Resource> domainResources) {

    EncounterHospitalization encounterHospitalization = new EncounterHospitalization();
    String vehicle = traumaInfo.getString("vehicle");
    if (vehicle != null) {
      //It is possible to add a list of all the locations (building, roads etc.) the patient has been
      encounterPreh.addNewLocation(new EncounterLocation()
        .setLocation(new Reference()
          .setDisplay(vehicle))
        .setStatus("completed")
        .setPhysicalType(new CodeableConcept()
          .addNewCoding(new Coding()
            .setCode("ve")
            .setDisplay("Vehicle")
            .setSystem("https://www.hl7.org/fhir/valueset-location-physical-type.html"))
          .setText("Trasporto con " + vehicle)));
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
          .setDisplay("Encounter pre ospedalizzazione")
          .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + encounterPreh.getId()))
        .setSubject(encounterPreh.getSubject())
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


    return createPatient(name, surname, gender, age, dob, erDeceased, encounterPreh, domainResources);
  }

  private Patient createPatient(String name, String surname, String gender, int age, String dob, Boolean erDeceased, Encounter encounterPreh,
                                List<Resource> resources) {


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

    resources.add(patient);
    encounterPreh.addNewContained(patient);
    encounterPreh.setSubject(new Reference()
      .setReference("#" + patient.getId())
      .setDisplay(name + " " + surname + " età " + age)
      .setType(ResourceType.PATIENT.typeName()));

    return patient;
  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
