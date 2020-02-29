package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IdentifierQuery extends BaseMongoDbQuery {

  @Override
  public String name() {
    return "identifier";
  }

//  @Override
//  public JsonObject mongoDbQuery() {
//    return new JsonObject()
//      .put("identifier.value", value);
//  }

  @Override
  public JsonObject mongoDbPipelineStageQuery() {
    String[] splitResult = value.split("\\|");
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
          .put("regex", value)
          .put("options", "i")
        );
    } else {
      String system = value.split("\\|")[0];
      String identifierValue = value.split("\\|")[1];

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

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName) {
    return null;
  }


}
