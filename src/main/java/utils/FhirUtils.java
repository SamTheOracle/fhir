package utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import model.domain.OperationOutcome;
import model.domain.OperationOutcomeIssue;
import model.elements.Metadata;
import model.exceptions.NotValideFhirResourceException;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;

import javax.json.JsonReader;
import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FhirUtils {
  public static final String PRETTY = "_pretty";
  public static final String FORMAT = "_format";
  public static final String SUMMARY = "_summary";
  public static final String ELEMENTS = "_elements";
  public static final String GENERAL_PATH_PARAMETER_ERROR = "Error path parameter";
  public static final String T4CINTERFACE_MAIN_ROOT = "t4cinterface";
  public static final String T4CINTERFACE_EVENTS_MESSAGE_SOURCE_ADDRESS = "t4cevents-address";
  public static final String T4CINTERFACE_EVENTS_MESSAGE_SOURCE_NAME = "t4cevents-message-source";
  public static final String FHIR_EVENTS_PROXY_ADDRESS = "database-fhir-events-service";
  public static final DateTimeFormatter fullDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssZZZZZ");
  public static final String ID = "id";
  public static final String USER_SERVICE_ADDRESS = "user-fhir-service";
  public static final String DELETE_SERVICE_ADDRESS = "delete-fhir-service";
  public static final String HISTORY = "_history";
  public static final String PATH_VERSIONID = "vid";
  public static final String BASE = "fhirAPI";
  public static final String PATIENT_TYPE = "Patient";
  public static final String PATIENTS_COLLECTION = "patients";
  public static final String FHIR_SERVICE = "fhir_service";
  public static final String LOCALHOST = "localhost";
  public static final String T4CSERVICE = "t4cservice";


  public static void createPostResponseBasedOnPreferHeader(String preferHeader, JsonObject dbResult,
                                                           HttpServerResponse serverResponse) {
    if (preferHeader == null) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(dbResult.encodePrettily());
    } else if (preferHeader.equalsIgnoreCase(FhirHttpHeaderValues.RETURN_MINIMAL)) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .end();
    } else if (preferHeader.equalsIgnoreCase(FhirHttpHeaderValues.RETURN_REPRESENTATION)) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
      serverResponse.setStatusCode(HttpResponseStatus.CREATED.code()).
        end(dbResult.encodePrettily());
    } else if (preferHeader.equalsIgnoreCase(FhirHttpHeaderValues.RETURN_OPERATION_OUTCOME)) {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
      OperationOutcome operationOutcome = new OperationOutcome();
      operationOutcome.setId(UUID.randomUUID().toString());
      OperationOutcomeIssue operationOutcomeIssue = new OperationOutcomeIssue();
      operationOutcomeIssue.setCode("informational");
      operationOutcomeIssue.setSeverity("information")
        .setDiagnostics("Resource correctly created");
      operationOutcome.setIssue(operationOutcomeIssue);
      serverResponse.setStatusCode(HttpResponseStatus.CREATED.code()).end(Json.encodePrettily(operationOutcome));
    } else {
      serverResponse.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
      serverResponse.setStatusCode(HttpResponseStatus.CREATED.code())
        .end("Resource created, but prefer header is wrong " + preferHeader);
    }
  }

  public static void createPostRequestErrorResponse(HttpServerResponse serverResponse, String errorMessage) {
    OperationOutcome outcome = new OperationOutcome();
    OperationOutcomeIssue issue = new OperationOutcomeIssue();
    issue.setSeverity("error");
    issue.setCode("error");
    issue.setDiagnostics(errorMessage);
    outcome.setIssue(issue);
    serverResponse.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(Json.encodePrettily(outcome));
  }

  public static MongoClient createFhirMongoDbConnection(Vertx vertx) {
    String connectionString = Optional.ofNullable(System.getenv("MONGODB_USERS_CONNECTION"))
      .orElse("mongodb://localhost:27017");
    String fhirdb = Optional.ofNullable(System.getenv("MONGODB_NAME")).orElse("fhir_db");
    JsonObject configs = new JsonObject().put("db_name", fhirdb)
      .put("connection_string", connectionString);
    return MongoClient.createShared(vertx, configs, "fhir-pool");
  }

  public static void validateJsonAgainstSchema(JsonObject clientContent) throws NotValideFhirResourceException {
    JsonValidationService service = JsonValidationService.newInstance();


    ValidationHandler handler = new ValidationHandler();

    InputStream inputStream;
    try {
      inputStream = new BufferedInputStream(new FileInputStream(Objects.requireNonNull(FhirUtils.class.getClassLoader()
        .getResource("fhir.schema.json")).getFile()));

      JsonSchema schema = service.readSchema(inputStream);
      InputStream json = new ByteArrayInputStream(clientContent.encode().getBytes());
      JsonReader reader = service.createReader(json, schema, handler);

      reader.readValue();
      handler.checkProblems();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Metadata createMetadata(String id, String incrementingVersionId) {
    Metadata metadata = new Metadata();
    metadata.setId(id);
    return new Metadata()
      .setVersionId(id + "/" + incrementingVersionId)
      .setLastUpdated(Instant.now());
  }
}
