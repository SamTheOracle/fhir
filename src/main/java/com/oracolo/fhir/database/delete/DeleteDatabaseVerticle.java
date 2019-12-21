package com.oracolo.fhir.database.delete;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import utils.FhirUtils;

public class DeleteDatabaseVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MongoClient mongoClient = FhirUtils.createFhirMongoDbConnection(vertx);

    ServiceBinder binder = new ServiceBinder(vertx);
    binder
      .setAddress(FhirUtils.DELETE_SERVICE_ADDRESS)
      .register(DeleteDatabaseService.class, new DeleteDatabaseServiceImpl(mongoClient))
      .completionHandler(proxyAsync -> {
        if (proxyAsync.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(proxyAsync.cause());
        }
      });
  }
}
