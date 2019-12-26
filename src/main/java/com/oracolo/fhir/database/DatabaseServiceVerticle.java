package com.oracolo.fhir.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import utils.FhirUtils;

public class DatabaseServiceVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    MongoClient mongoClient = FhirUtils.createFhirMongoDbConnection(vertx);

    ServiceBinder binder = new ServiceBinder(vertx);
    binder
      .setAddress(FhirUtils.DATABASE_SERVICE_ADDRESS)
      .register(DatabaseService.class, new DatabaseServiceImpl(mongoClient))
      .completionHandler(proxyAsync -> {
        if(proxyAsync.succeeded()){
          startPromise.complete();
        }
        else {
          startPromise.fail(proxyAsync.cause());
        }
      });
  }
}
