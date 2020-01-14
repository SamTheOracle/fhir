package com.oracolo.fhir.http;

import com.oracolo.fhir.BaseRestInterface;
import com.oracolo.fhir.utils.FhirUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.logging.Logger;

/**
 * Http endpoint for internal/external interchange
 */
public class Gateway extends BaseRestInterface {
  private static final Logger LOGGER = Logger.getLogger(Gateway.class.getName());

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router gatewayRouter = Router.router(vertx);
    gatewayRouter.route().handler(BodyHandler.create());
    gatewayRouter.route("/" + FhirUtils.BASE + "/*")
      .handler(routingContext -> rerouteToService(routingContext, FhirUtils.FHIR_SERVICE));
    gatewayRouter.route("/" + FhirUtils.TRAUMACARE_BASE + "/*")
      .handler(routingContext -> rerouteToService(routingContext, FhirUtils.T4CSERVICE));


    createAPIServer(8000, gatewayRouter)
      .setHandler(httpServerAsyncResult -> {
        if (httpServerAsyncResult.succeeded()) {
          LOGGER.info("Gateway started at port " + httpServerAsyncResult.result().actualPort());
          startPromise.complete();
        } else {
          startPromise.fail(httpServerAsyncResult.cause());
        }
      });


  }

  private void rerouteToService(RoutingContext routingContext, String serviceName) {
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

    HttpEndpoint.getWebClient(discovery, record -> record.getName().equalsIgnoreCase(serviceName), webClientAsyncResult -> {
      WebClient webClient = webClientAsyncResult.result();

      Buffer body = routingContext.getBody();
      HttpServerRequest httpServerRequest = routingContext.request();
      HttpServerResponse httpServerResponse = routingContext.response();
      HttpMethod method = httpServerRequest.method();
      String uri = httpServerRequest.uri();

      HttpRequest<Buffer> request = webClient.request(method, uri);

      request.putHeaders(httpServerRequest.headers());

      if (body != null) {

        //send json object does not work...
        request.sendBuffer(body, httpResponseAsyncResult -> handleHttpResponseFromFhir(httpServerResponse, httpResponseAsyncResult));
      } else {
        request.send(httpResponseAsyncResult -> handleHttpResponseFromFhir(httpServerResponse, httpResponseAsyncResult));
      }
      discovery.close();

    });
  }

  private void handleHttpResponseFromFhir(HttpServerResponse serverResponse, AsyncResult<HttpResponse<Buffer>> httpResponseAsyncResult) {
    if (httpResponseAsyncResult.succeeded()) {
      HttpResponse<Buffer> response = httpResponseAsyncResult.result();
      Buffer body = response.bodyAsBuffer();
      response.headers().forEach(header -> serverResponse.putHeader(header.getKey(), header.getValue()));
      if (body == null) {
        serverResponse
          .setStatusCode(response.statusCode())
          .end();
      } else {
        serverResponse
          .setStatusCode(response.statusCode())
          .end(body);
      }


    } else {
      serverResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end();
    }

  }


}
