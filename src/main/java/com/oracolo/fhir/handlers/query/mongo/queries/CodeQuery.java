package com.oracolo.fhir.handlers.query.mongo.queries;

import com.oracolo.fhir.handlers.query.mongo.BaseMongoDbQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CodeQuery extends BaseMongoDbQuery {


  @Override
  public String name() {
    return "_id";
  }

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
  public JsonObject mongoDbPipelineStageQuery() {
    String[] splitResult = value.split("\\|");
    if (splitResult.length == 1) {
      return new JsonObject()
        .put("$or", new JsonArray()
          .add(new JsonObject()
            .put("$regexMatch", new JsonObject()
              .put("input", "$code.text")
              .put("regex", value)
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
              .put("regex", value)
              .put("options", "i")
            ))
        );
    } else {
      String system = value.split("\\|")[0];
      String code = value.split("\\|")[1];

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

  @Override
  public JsonObject mongoDbPipelineStageQuery(String paramName) {
    return null;
  }


}
