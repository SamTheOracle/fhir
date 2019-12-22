package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.database.delete.DeleteDatabaseService;
import com.oracolo.fhir.database.user.UserDatabaseService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import utils.FhirUtils;

import java.util.logging.Logger;

public class T4CRestInterface extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(FhirServer.class.getName());
  private UserDatabaseService userService;
  private DeleteDatabaseService deleteService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router t4cRouter = Router.router(vertx);
    t4cRouter.route().handler(BodyHandler.create());
    t4cRouter.get("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/welcome").handler(this::handleWelcome);
    t4cRouter.post("/" + FhirUtils.T4CINTERFACE_MAIN_ROOT + "/intervention").handler(this::handleIntervention);

    createAPIServer(0, t4cRouter)
      .compose(httpServer -> {
        int port = httpServer.actualPort();
        LOGGER.info("T4C interface listening at " + port);
        return publishHTTPEndPoint(port, FhirUtils.T4CSERVICE, FhirUtils.LOCALHOST, "/" + FhirUtils.T4CINTERFACE_MAIN_ROOT);
      }).setHandler(publishSuccessful -> {
      if (publishSuccessful.succeeded()) {
        this.userService = UserDatabaseService.createProxy(vertx, FhirUtils.USER_SERVICE_ADDRESS);
        this.deleteService = DeleteDatabaseService.createProxy(vertx, FhirUtils.DELETE_SERVICE_ADDRESS);
        startPromise.complete();
      } else {
        startPromise.fail(publishSuccessful.cause());
      }
    });
  }

  private void handleIntervention(RoutingContext routingContext) {
    JsonObject jsonObject = routingContext.getBodyAsJson();
    routingContext.response()
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(jsonObject.encodePrettily());

  }

  private void handleWelcome(RoutingContext routingContext) {
    routingContext.response().setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      .end("Welcome");
  }
}
