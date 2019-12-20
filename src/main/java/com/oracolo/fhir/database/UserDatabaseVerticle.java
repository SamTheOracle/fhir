package com.oracolo.fhir.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import utils.Utils;

public class UserDatabaseVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MongoClient mongoClient = Utils.createFhirMongoDbConnection(vertx);

    ServiceBinder binder = new ServiceBinder(vertx);
    binder
      .setAddress(Utils.SERVICE_ADDRESS)
      .register(UserDatabaseService.class, new UserDatabaseServiceImpl(mongoClient))
      .completionHandler(proxyAsync->{
        if(proxyAsync.succeeded()){
          startPromise.complete();
        }
        else {
          startPromise.fail(proxyAsync.cause());
        }
      });
  }
}
