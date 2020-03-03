package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IdentifierQuery implements FhirQuery {


//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("identifier.value", value);
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    String[] splitResult = paramValue.split("\\|");
    if (splitResult.length == 1) {
      return new JsonObject()
        .put("$regexMatch", new JsonObject()
          .put("input", new JsonObject()
            .put("$reduce", new JsonObject()
              .put("input", "$identifier")
              .put("initialValue", "")
              .put("in", new JsonObject()
                .put("$concat", new JsonArray()
                  .add("$$value")
                  .add("$$this.value")
                  .add(" ")
                ))
            ))
          .put("regex", paramValue)
          .put("options", "i")
        );
    } else {
      String system = paramValue.split("\\|")[0];
      String identifierValue = paramValue.split("\\|")[1];

      return new JsonObject()
        .put("$and", new JsonArray()
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", new JsonObject()
                .put("$reduce", new JsonObject()
                  .put("input", "$identifier")
                  .put("initialValue", "")
                  .put("in", new JsonObject()
                    .put("$concat", new JsonArray()
                      .add("$$value")
                      .add("$$this.value")
                      .add(" ")
                    ))
                ))
              .put("regex", identifierValue)
              .put("options", "i")
            ))
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", new JsonObject()
                .put("$reduce", new JsonObject()
                  .put("input", "$identifier")
                  .put("initialValue", "")
                  .put("in", new JsonObject()
                    .put("$concat", new JsonArray()
                      .add("$$value")
                      .add("$$this.system")
                      .add(" ")
                    ))
                ))
              .put("regex", system.equals("") ? null : system)
              .put("options", "i")
            ))

        );
    }


  }




}
