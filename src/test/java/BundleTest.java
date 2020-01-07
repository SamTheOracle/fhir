import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.database.DatabaseServiceVerticle;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.backboneelements.BundleRequest;
import com.oracolo.fhir.model.backboneelements.BundleResponse;
import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.OperationOutcomeIssue;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.model.elements.Metadata;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.ErrorFormat;
import com.oracolo.fhir.utils.FhirUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(VertxExtension.class)
class BundleTest {
  @BeforeAll
  static void bootDatabaseVerticle(Vertx vertx, VertxTestContext vertxTestContext) {
    vertx.deployVerticle(new DatabaseServiceVerticle(), vertxTestContext.completing());
  }

  @Test
  void testValidationTrue() {
    Bundle bundle = new Bundle();
    bundle
      .setTimestamp(Instant.now())
      .setType("searchset")
      .setTotal(3)
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()))
      .addNewEntry(new BundleEntry()
        .setResource(new Patient()));
    Assertions.assertTrue(ValidationHandler
      .createValidator()
      .validateAgainstJsonSchema(JsonObject.mapFrom(bundle)));
  }

  @Test
  void testValidationFalse() {
    Bundle bundle = new Bundle();
    JsonObject jsonObject = JsonObject.mapFrom(bundle);
    Assertions.assertFalse(ValidationHandler
      .createValidator()
      .validateAgainstJsonSchema(jsonObject)
    );
  }

  @Test
  public void parseRequests(Vertx vertx, VertxTestContext vertxTestContext) {
    Bundle bundle = new Bundle()
      .addNewEntry(new BundleEntry()
        .setRequest(new BundleRequest()
          .setMethod("GET")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/12345?_family=Zanotti&_given=Giacomo")
        )
      )
      .addNewEntry(new BundleEntry()
        .setRequest(new BundleRequest()
          .setMethod("GET")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/12345?_family=Gay&_given=Giacomo")
        )
      );
    Bundle responseBundle = new Bundle();


    List<Future<BundleEntry>> futures = new ArrayList<>();
    bundle.getEntry().forEach(bundleEntry -> {

      Future<BundleEntry> entryFuture = Future.future(promise -> {
        BundleRequest bundleRequest = bundleEntry.getRequest();
        String method = bundleRequest.getMethod();
        String url = bundleRequest.getUrl();
        try {
          URL urlToExamine = new URL(url);
          String path = urlToExamine.getPath();
          String[] pathValues = path.split("/");
          Assertions.assertDoesNotThrow(() -> ResourceType.valueOf(pathValues[2].toUpperCase()));


          ResourceType type = ResourceType.valueOf(pathValues[2].toUpperCase());
          String queryString = urlToExamine.getQuery();
          if (queryString != null) {
            String[] query = urlToExamine.getQuery().split("&");

            Map<String, String> queryMap = new HashMap<>();
            for (String q : query) {
              String paramName = q.split("=")[0];
              String paramValue = q.split("=")[1];
              queryMap.put(paramName, paramValue);

            }
          }
          switch (method) {
            case "GET":
              String id = pathValues[3];
              String vId;
              JsonObject query = new JsonObject()
                .put("id", id);
              if (path.contains("_history")) {
                vId = pathValues[5];
                query.put("meta.versionId", vId);
              }
              BundleEntry entry = new BundleEntry();
              Promise<JsonObject> jsonObjectPromise = Promise.promise();
              DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS).fetchDomainResourceWithQuery(type.getCollection(), query, new JsonObject(), jsonObjectPromise);
              jsonObjectPromise.future()
                .onSuccess(jsonObject -> {
                  Metadata metadata = Json.decodeValue(jsonObject.getJsonObject("meta").encode(), Metadata.class);
                  String lastModified = metadata.getLastUpdated().toString();
                  String versionId = metadata.getVersionId();
                  String resourceId = jsonObject.getString("id");
                  String resourceType = jsonObject.getString("resourceType");
                  entry.setResource(jsonObject)
                    .setResponse(new BundleResponse()
                      .setEtag(versionId)
                      .setLastModified(lastModified)
                      .setLocation(FhirUtils.BASE + "/" + resourceType + "/" + resourceId + "/_history/" + versionId)
                      .setStatus(String.valueOf(HttpResponseStatus.OK.code()))
                    );
                  responseBundle.addNewEntry(entry);
                  promise.complete(entry);
                }).onFailure(throwable -> {
                OperationOutcome operationOutcome = new OperationOutcome();

                if (throwable instanceof ServiceException) {
                  int code = ((ServiceException) throwable).failureCode();
                  String message = throwable.getMessage();
                  ErrorFormat errorFormat = ErrorFormat.createFormat(code);
                  operationOutcome = new OperationOutcome()
                    .setIssue(new OperationOutcomeIssue()
                      .setSeverity("error")
                      .setCode(errorFormat.getFhirErrorCode())
                      .setDiagnostics(message));
                  entry.setResponse(new BundleResponse()
                    .setStatus(String.valueOf(code))
                  );

                } else {
                  operationOutcome.setIssue(new OperationOutcomeIssue()
                    .setSeverity("error")
                    .setCode("exception")
                    .setDiagnostics(throwable.getMessage()));
                  entry.setResponse(new BundleResponse()
                    .setStatus("500")
                  );
                }
                entry.getResponse().setOutcome(operationOutcome);
                responseBundle.addNewEntry(entry);
                promise.complete(entry);

              });

            default:
              return;
          }


        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      });
      futures.add(entryFuture);


    });
    CompositeFuture.all(new ArrayList<>(futures))
      .setHandler(handler -> {
        responseBundle.setId("ciao");
        vertxTestContext.completeNow();
      });


  }


}
