import com.oracolo.fhir.database.DatabaseService;
import com.oracolo.fhir.database.DatabaseServiceVerticle;
import com.oracolo.fhir.handlers.validator.ValidationHandler;
import com.oracolo.fhir.model.ResourceType;
import com.oracolo.fhir.model.backboneelements.BundleEntry;
import com.oracolo.fhir.model.backboneelements.BundleRequest;
import com.oracolo.fhir.model.domain.Patient;
import com.oracolo.fhir.model.resources.Bundle;
import com.oracolo.fhir.utils.FhirUtils;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
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
        .setResource(new Patient())
        .setRequest(new BundleRequest()
          .setMethod("POST")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName())
        )
      )
      .addNewEntry(new BundleEntry()
        .setResource(new Patient())
        .setRequest(new BundleRequest()
          .setMethod("POST")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName())
        )
      ).addNewEntry(new BundleEntry()
        .setResource(new Patient())
        .setRequest(new BundleRequest()
          .setMethod("PUT")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName())
        ))
      .addNewEntry(new BundleEntry()
        .setRequest(new BundleRequest()
          .setMethod("GET")
          .setUrl(FhirUtils.GATEWAY_ENDPOINT + "/" + FhirUtils.BASE + "/" + ResourceType.PATIENT.typeName() + "/168428ca-9aac-491b-af95-ca5792f58e11")
        ));
    Bundle responseBundle = new Bundle();

    List<JsonObject> rs = new ArrayList<>();
    JsonObject complexQuery = new JsonObject()
      .put("$or", new JsonArray());
    Promise<JsonObject> bulkWriteUpdateOperations = Promise.promise();
    Promise<JsonObject> bulkGetOperations = Promise.promise();


//    Promise<JsonObject> jsonObjectPromise = Promise.promise();
//    DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS)
//      .executeWriteBulkOperations(FhirUtils.PATIENTS_COLLECTION, rs, jsonObjectPromise);
    bundle.getEntry().forEach(bundleEntry -> {

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
            //esaminare tutti i query parameter

            //capire se è history

            //capire se è search

//            complexQuery
//              .getJsonArray("$or")
//              .add(QueryHandler
//                .fromResourceType(type)
//                .createMongoDbQuery());
            break;
          case "POST":
            JsonObject domainResource = JsonObject.mapFrom(bundleEntry.getResource());
            //resolve references??????
            rs.add(domainResource);
            break;
          case "PUT":
            DatabaseService.createProxy(vertx, FhirUtils.DATABASE_SERVICE_ADDRESS);
            break;
          default:
            break;
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }


    });
    Assertions.assertEquals(2, rs.size());
  }


}
