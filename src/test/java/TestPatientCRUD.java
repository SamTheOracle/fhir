import com.oracolo.fhir.ApplicationBootstrap;
import com.oracolo.fhir.model.datatypes.HumanName;
import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.utils.FhirHttpHeader;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestPatientCRUD {

  private static final String PATIENTID = "12345670";
  private static final String BASE_URL = "http://localhost:8000/fhirAPI/Patient";


  @BeforeAll
  static void bootstrap(Vertx vertx, VertxTestContext vertxTestContext) {
    vertx.deployVerticle(new ApplicationBootstrap(), vertxTestContext.completing());

  }


  @Test
  void putPatientResourceTest(Vertx vertx, VertxTestContext testContext) {


    Patient patient = new Patient()
      .setId(PATIENTID)
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .addNewGiven("Giacomo"));

    WebClient.create(vertx)
      .putAbs(BASE_URL + "/" + PATIENTID)
      .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeader.APPLICATION_JSON.value())
      .putHeader(FhirHttpHeader.PREFER_REPRESENTATION.name(), FhirHttpHeader.PREFER_REPRESENTATION.value())
      .sendBuffer(Buffer.buffer(JsonObject.mapFrom(patient).encode()), testContext.succeeding(bufferHttpResponse ->
        testContext.verify(() -> {
          Assertions.assertDoesNotThrow(() -> Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.LOCATION.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.ETAG.toString()));
          Patient pFromServer = Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class);
          Assertions.assertEquals(PATIENTID, pFromServer.getId());
          testContext.completeNow();
        })));


  }

  @Test
  void getPatientResourceTest(Vertx vertx, VertxTestContext testContext) {


    WebClient.create(vertx)
      .getAbs(BASE_URL + "/" + PATIENTID)
      .send(testContext.succeeding(bufferHttpResponse ->
        testContext.verify(() -> {
          Assertions.assertDoesNotThrow(() -> Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class));
          Patient pFromServer = Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class);
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.LOCATION.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.ETAG.toString()));
          Assertions.assertEquals(PATIENTID, pFromServer.getId());
          testContext.completeNow();
        })));
  }

  @Test
  void postResourceTest(Vertx vertx, VertxTestContext testContext) {
    Patient patient = new Patient()
      .setId(PATIENTID)
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .addNewGiven("Giacomo"));

    WebClient.create(vertx)
      .postAbs(BASE_URL)
      .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeader.APPLICATION_JSON.value())
      .putHeader(FhirHttpHeader.PREFER_REPRESENTATION.name(), FhirHttpHeader.PREFER_REPRESENTATION.value())
      .sendBuffer(Buffer.buffer(JsonObject.mapFrom(patient).encode()), testContext.succeeding(bufferHttpResponse ->
        testContext.verify(() -> {
          Assertions.assertDoesNotThrow(() -> Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.LOCATION.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.ETAG.toString()));
          Patient pFromServer = Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class);
          Assertions.assertNotEquals(PATIENTID, pFromServer.getId());
          testContext.completeNow();
        })));

  }

  @Test
  void getPatientWithSearchParameterFamily(Vertx vertx, VertxTestContext testContext) {
    //TO-DO
    testContext.completeNow();
  }

  @Test
  void deletePatientTest(Vertx vertx, VertxTestContext testContext) {
    Checkpoint checkpoint = testContext.checkpoint(4);
    Patient patient = new Patient()
      .setId(PATIENTID)
      .addNewHumanName(new HumanName()
        .setFamily("Zanotti")
        .addNewGiven("Giacomo"));

    WebClient.create(vertx)
      .putAbs(BASE_URL + "/" + PATIENTID)
      .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeader.APPLICATION_JSON.value())
      .putHeader(FhirHttpHeader.PREFER_REPRESENTATION.name(), FhirHttpHeader.PREFER_REPRESENTATION.value())
      .sendBuffer(Buffer.buffer(JsonObject.mapFrom(patient).encode()), testContext.succeeding(bufferHttpResponse ->
        testContext.verify(() -> {
          Assertions.assertDoesNotThrow(() -> Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class));
          Patient pFromServer = Json.decodeValue(bufferHttpResponse.bodyAsBuffer(), Patient.class);
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.LOCATION.toString()));
          Assertions.assertNotNull(bufferHttpResponse.getHeader(HttpHeaderNames.ETAG.toString()));
          Assertions.assertEquals(PATIENTID, pFromServer.getId());
          checkpoint.flag();
          WebClient.create(vertx)
            .deleteAbs(BASE_URL + "/" + PATIENTID)
            .send(testContext.succeeding(bufferHttpResponse1 ->
              testContext.verify(() -> {
                Assertions.assertNotNull(bufferHttpResponse1.getHeader(HttpHeaderNames.LOCATION.toString()));
                Assertions.assertNotNull(bufferHttpResponse1.getHeader(HttpHeaderNames.ETAG.toString()));
                checkpoint.flag();
                WebClient.create(vertx)
                  .getAbs(BASE_URL + "/" + PATIENTID)
                  .send(testContext.succeeding(bufferHttpResponse2 ->
                    testContext.verify(() -> {
                      Assertions.assertDoesNotThrow(() -> Json.decodeValue(bufferHttpResponse2.bodyAsBuffer(), OperationOutcome.class));
                      Assertions.assertNotNull(bufferHttpResponse2.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
                      Assertions.assertEquals(bufferHttpResponse2.statusCode(), HttpResponseStatus.GONE.code());
                      checkpoint.flag();
                      WebClient.create(vertx)
                        .putAbs(BASE_URL + "/" + PATIENTID)
                        .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), FhirHttpHeader.APPLICATION_JSON.value())
                        .putHeader(FhirHttpHeader.PREFER_REPRESENTATION.name(), FhirHttpHeader.PREFER_REPRESENTATION.value())
                        .sendBuffer(Buffer.buffer(JsonObject.mapFrom(patient).encode()), testContext.completing());
                      checkpoint.flag();
                      testContext.completeNow();
                    })));
              })));
        })));

  }
}
