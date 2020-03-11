package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ClassQuery implements FhirQuery {


  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    String[] splitResult = paramValue.split("\\|");
    if (splitResult.length == 1) {
      return new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$class.code")
              .put("regex", paramValue)
              .put("options", "i")
            )
          )
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$class.display")
              .put("regex", paramValue)
              .put("options", "i")

            )));
    } else {
      String system = paramValue.split("\\|")[0];
      String code = paramValue.split("\\|")[1];

      return new JsonObject()
        .put("$and", new JsonArray()
          .add(new JsonObject()
            .put("$or", new JsonArray()
              .add(new JsonObject()
                .put("$regexMatch", new JsonObject()
                  .put("input", "$class.code")
                  .put("regex", code)
                  .put("options", "i")
                )
              )
              .add(new JsonObject()
                .put("$regexMatch", new JsonObject()
                  .put("input", "$class.display")
                  .put("regex", code)
                  .put("options", "i")

                ))))
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$class.system")
              .put("regex", system)
              .put("options", "i"))));

    }


  }
}
