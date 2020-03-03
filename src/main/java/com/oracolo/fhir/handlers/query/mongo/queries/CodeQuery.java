package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.FhirQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CodeQuery implements FhirQuery{

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("$or", new JsonArray()
//        .add(new JsonObject()
//          .put("code.text", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i")))
//        .add(new JsonObject()
//          .put("code.coding.display", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i")))
//        .add(new JsonObject()
//          .put("code.coding.code", new JsonObject()
//            .put("$regex", value)
//            .put("$options", "i"))));
//  }


  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName, String paramValue) {

    String[] splitResult = paramValue.split("\\|");
    if (splitResult.length == 1) {
      return new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$code.text")
              .put("regex", paramValue)
              .put("options", "i")
            )
          )
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", new JsonObject()
                .put("$reduce", new JsonObject()
                  .put("input", "$code.coding")
                  .put("initialValue", "")
                  .put("in", new JsonObject()
                    .put("$concat", new JsonArray()
                      .add("$$value")
                      .add("$$this.code")
                      .add(" ")
                      .add("$$this.display")
                      .add(" ")
                    ))
                ))
              .put("regex", paramValue)
              .put("options", "i")
            ))
        );
    } else {
      String system = paramValue.split("\\|")[0];
      String code = paramValue.split("\\|")[1];

      return new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$code.text")
              .put("regex", code)
              .put("options", "i")
            )
          )
          .add(new JsonObject()
            .put("$and", new JsonArray()
              .add(new JsonObject()
                .put("$regexMatch", new JsonObject()
                  .put("input", new JsonObject()
                    .put("$reduce", new JsonObject()
                      .put("input", "$code.coding")
                      .put("initialValue", "")
                      .put("in", new JsonObject()
                        .put("$concat", new JsonArray()
                          .add("$$value")
                          .add("$$this.code")
                          .add(" ")
                          .add("$$this.display")
                          .add(" ")
                        ))
                    ))
                  .put("regex", code)
                  .put("options", "i")
                ))
              .add(new JsonObject()
                .put("$regexMatch", new JsonObject()
                  .put("input", new JsonObject()
                    .put("$reduce", new JsonObject()
                      .put("input", "$code.coding")
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
            ))

        );
    }


  }



}
