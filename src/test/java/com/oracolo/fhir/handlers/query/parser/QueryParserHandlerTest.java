package com.oracolo.fhir.handlers.query.parser;

import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixHandler;
import com.oracolo.fhir.handlers.query.parser.prefix.QueryPrefixResult;
import com.oracolo.fhir.model.backboneelements.EncounterLocation;
import com.oracolo.fhir.model.backboneelements.MedicationAdministrationDosage;
import com.oracolo.fhir.model.backboneelements.ObservationComponent;
import com.oracolo.fhir.model.datatypes.Attachment;
import com.oracolo.fhir.model.datatypes.Coding;
import com.oracolo.fhir.model.datatypes.Period;
import com.oracolo.fhir.model.domain.*;
import com.oracolo.fhir.model.elements.Annotation;
import com.oracolo.fhir.model.elements.CodeableConcept;
import com.oracolo.fhir.model.elements.Quantity;
import com.oracolo.fhir.model.elements.Reference;
import com.oracolo.fhir.utils.FhirUtils;
import com.oracolo.fhir.utils.ResourceType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

class QueryParserHandlerTest {

  @Test
  void parseQueryParameter() {

    String parameterValue = "gt12.3";
    QueryPrefixResult queryParserResult = QueryPrefixHandler.parsePrefix(parameterValue);
    JsonObject query = new JsonObject()
      .put(queryParserResult.prefix().operator(), queryParserResult.parsedValue());

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
              procedure.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre ospedalizzazione")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));

            } else {
              procedure.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                  .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              if (place != null) {

                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  intubationObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter pre ospedalizzazione")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

                } else {
                  intubationObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter intervention")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                  .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              if (place != null) {

                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  drainageObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter pre ospedalizzazione")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

                } else {
                  drainageObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter intervention")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                  .setReference("/" + ResourceType.PROCEDURE.typeName() + "/" + procedure.getId()))
                .setStatus("final")
                .setCode(new CodeableConcept()
                  .setText(name))
                .setValueBoolean((Boolean) value);
              if (place != null) {

                if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                  chestTubeObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter pre ospedalizzazione")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + preH.getId()));

                } else {
                  chestTubeObservation.setEncounter(new Reference()
                    .setType(ResourceType.ENCOUNTER.typeName())
                    .setDisplay("Encounter intervention")
                    .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + intervention.getId()));
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
            .setEffectiveDatetime(date + " " + time)
            .setCode(new CodeableConcept()
              .setText(diagnosticId))
            .setStatus("final")
            .setConclusion(diagnosticDescription);
          if (place != null) {

            if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
              diagnosticReport.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre ospedalizzazione")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + preH.getId()));

            } else {
              diagnosticReport.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationLactates.getId())
              .setDisplay("Observation lactates")
              .setType(ResourceType.OBSERVATION.typeName()));

            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                observationLactates.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

              } else {
                observationLactates.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + "/" + observationBe.getId())
              .setDisplay("Observation be")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                observationBe.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + preH.getId()));

              } else {
                observationBe.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + phObservation.getId())
              .setDisplay("Observation be")
              .setType(ResourceType.OBSERVATION.typeName()));

            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                phObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + preH.getId()));

              } else {
                phObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + "/" + intervention.getId()));
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
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

              } else {
                hbObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + fibtemObservation.getId())
              .setDisplay("Observation fibtem")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                fibtemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

              } else {
                fibtemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + extemObservation.getId())
              .setDisplay("Observation extem")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                extemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

              } else {
                extemObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
              .setReference("/" + ResourceType.OBSERVATION.typeName() + hyperfibrinolysisObservation.getId())
              .setDisplay("Observation hyperfibrinolysis")
              .setType(ResourceType.OBSERVATION.typeName()));
            if (place != null) {

              if (place.equalsIgnoreCase("PRE-H") || place.equalsIgnoreCase("trasporto")) {
                hyperfibrinolysisObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

              } else {
                hyperfibrinolysisObservation.setEncounter(new Reference()
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setDisplay("Encounter intervention")
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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

            } else {
              drugAdministration
                .setContext(new Reference()
                  .setReference("#" + intervention.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName()));
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
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId())
                  .setDisplay("Encounter pre ospedalizzazione")
                  .setType(ResourceType.ENCOUNTER.typeName()));

            } else {
              intervention.addNewContained(medicationAdministration
                .setContext(new Reference()
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId())
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName())));
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
                  .setDisplay("Encounter pre-hospitalization")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

            } else {
              vitalSignObservationContainer
                .setEncounter(new Reference()
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

            } else {
              clinicalVariation
                .setEncounter(new Reference()
                  .setDisplay("Encounter intervention")
                  .setType(ResourceType.ENCOUNTER.typeName())
                  .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre ospedalizzazione")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));

            } else {
              traumaLeaderProcedure.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre-hospitalization")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));


            } else {
              procedureRoomIn.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter pre-hospitalization")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + preH.getId()));


            } else {
              procedureAcceptance.setEncounter(new Reference()
                .setType(ResourceType.ENCOUNTER.typeName())
                .setDisplay("Encounter intervention")
                .setReference("/" + ResourceType.ENCOUNTER.typeName() + intervention.getId()));
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

}
