package com.oracolo.fhir;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

/**
 * Base Rest Interface for microservices rapid development. All the verticle extending this class
 * must call super.start() and use the abstract class router
 */
public abstract class BaseRestInterface extends AbstractVerticle {
  private ServiceDiscovery discovery;


  @Override
  public void start() {

    discovery = ServiceDiscovery.create(vertx);

//    router.get("/" + getAPIPrettyName() + "/ping").handler(routingContext ->
//      routingContext.response().setStatusCode(200).end("pong"));

  }

  /**
   * Utility method to publish http endpoint and make it available through event bus
   *
   * @param port     the port of the service
   * @param name     the name of the service
   * @param host     the name of the host
   * @param rootName the name of the root ("/" is prepended by default)
   * @return A Future with the result
   */
  protected Future<String> publishHTTPEndPoint(int port, String name, String host, String rootName) {
    Record record = HttpEndpoint.createRecord(name, host, port, "/" + rootName);
    Promise<String> promise = Promise.promise();
    ServiceDiscovery.create(vertx).publish(record, ar -> {
      if (ar.succeeded()) {
        // publication succeeded
        Record publishedRecord = ar.result();
        System.out.println("record is: " + publishedRecord.toJson().encodePrettily());
        promise.complete(record.getRegistration());
      } else {
        promise.fail(ar.cause().getMessage());
        System.out.println("patients.service failure");
        discovery.close();

      }
    });
    return promise.future();
  }

//  protected Future<String> publishMessageSource(String messageSourceName, String address, Class payloadTypeClass) {
//    Promise<String> messageSourcePromise = Promise.promise();
//    Record record = MessageSource.createRecord(messageSourceName, address, payloadTypeClass);
//    record.getMetadata().put("prettyName", getAPIPrettyName());
//    discovery.publish(record, resultHandler -> {
//      if (resultHandler.succeeded()) {
//        messageSourcePromise.complete(resultHandler.result().getRegistration());
//      } else {
//        messageSourcePromise.fail(resultHandler.cause().getMessage());
//      }
//    });
//    return messageSourcePromise.future();
//  }

  /**
   * Creates the Web Server
   *
   * @param port the port on which it listens
   * @return the Future with the httpServer result
   */
  protected Future<HttpServer> createAPIServer(int port, Router router) {
    Promise<HttpServer> promise = Promise.promise();
    vertx.createHttpServer().
      requestHandler(router).
      listen(port, res -> {
        if (res.succeeded()) {
          promise.complete(res.result());
        } else {
          promise.fail(res.cause().getMessage());
        }
      });
    return promise.future();
  }


}
