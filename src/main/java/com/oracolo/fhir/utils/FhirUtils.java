package com.oracolo.fhir.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FhirUtils {
  public static final String PRETTY = "_pretty";
  public static final String FORMAT = "_format";
  public static final String SUMMARY = "_summary";
  public static final String ELEMENTS = "_elements";
  public static final String T4CINTERFACE_MAIN_ROOT = "t4cinterface";
  public static final DateTimeFormatter fullDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssZZZZZ");
  public static final String ID = "id";
  public static final String HISTORY = "_history";
  public static final String PATH_VERSIONID = "vid";
  public static final String BASE = "fhirAPI";
  public static final String PATIENT_TYPE = "Patient";
  public static final String PATIENTS_COLLECTION = "patients";
  public static final String FHIR_SERVICE = "fhir_service";
  public static final String LOCALHOST = "localhost";
  public static final String T4CSERVICE = "t4cservice";
  public static final String OBSERVATION_TYPE = "Observation";
  public static final String GATEWAY_ENDPOINT = "http://localhost:8000";
  public static final String CONDITION_TYPE = "Condition";
  public static final String OBSERVATIONS_COLLECTION = "observations";
  public static final String CONDITIONS_COLLECTION = "conditions";
  public static final String DELETE_COLLECTION = "deleted_resources";
  public static final String DATABASE_SERVICE_ADDRESS = "database_service";
  public static final String DELETED = "DELETED";
  public static final int MONGODB_CONNECTION_FAIL = 12;
  public static final String DOCUMENTREFERENCE_TYPE = "DocumentReference";
  public static final String ENCOUNTER_TYPE = "Encounter";
  public static final String ENCOUNTER_COLLECTION = "encounters";
  public static final String PROCEDURE_COLLECTION = "procedures";
  public static final String BUNDLE_COLLECTION = "bundles";
  public static final String DIAGNOSTICREPORTS_COLLECTION = "diagnosticreports";
  public static final String MEDICATIONADMINISTRATIONS_COLLECTIONS = "medicationadministrations";
  public static final String MEDICATIONS_COLLECTION = "medications";
  public static final String PRACTITIONERS_COLLECTION = "practitioners";
  public static final String LOCATIONS_COLLECTIONS = "locations";
  public static final String ORGANIZATIONS_COLLECTION = "organizations";


  public static MongoClient createFhirMongoDbConnection(Vertx vertx) {
    String connectionString = Optional.ofNullable(System.getenv("MONGODB_USERS_CONNECTION"))
      .orElse("mongodb://localhost:27017");
    String fhirdb = Optional.ofNullable(System.getenv("MONGODB_NAME")).orElse("fhir_db");
    JsonObject configs = new JsonObject().put("db_name", fhirdb)
      .put("connection_string", connectionString);
    return MongoClient.createShared(vertx, configs, "fhir-pool");
  }


}
