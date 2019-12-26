package com.oracolo.fhir;

import com.oracolo.fhir.database.DatabaseServiceVerticle;
import com.oracolo.fhir.http.FhirServer;
import com.oracolo.fhir.http.Gateway;
import com.oracolo.fhir.http.T4CRestInterface;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class ApplicationBootstrap extends AbstractVerticle {
  //local bootstrap
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ApplicationBootstrap());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Promise<String> dbVerticle = Promise.promise();
    vertx.deployVerticle(new DatabaseServiceVerticle(), dbVerticle);

    dbVerticle.future().compose(deploymentResult -> {
      Promise<String> httpDeployPromise = Promise.promise();
      DeploymentOptions deploymentOptions = new DeploymentOptions()
        .setInstances(1);
      vertx.deployVerticle(FhirServer.class, deploymentOptions, httpDeployPromise);
      vertx.deployVerticle(new Gateway());
      vertx.deployVerticle(new T4CRestInterface());
      return httpDeployPromise.future();
    }).setHandler(deploymentResult -> {
      if (deploymentResult.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(deploymentResult.cause());
      }
    });
  }
}
