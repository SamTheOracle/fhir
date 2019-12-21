package com.oracolo.fhir;

import com.oracolo.fhir.database.delete.DeleteDatabaseVerticle;
import com.oracolo.fhir.database.user.UserDatabaseVerticle;
import com.oracolo.fhir.http.Gateway;
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
    Promise<String> userDbVerticlePromise = Promise.promise();
    Promise<String> deleteVerticlePromise = Promise.promise();
    vertx.deployVerticle(new UserDatabaseVerticle(), userDbVerticlePromise);
    userDbVerticlePromise.future().compose(deploymentResult -> {
      vertx.deployVerticle(new DeleteDatabaseVerticle(), deleteVerticlePromise);
      return deleteVerticlePromise.future();
    }).compose(deploymentResult -> {
      Promise<String> httpDeployPromise = Promise.promise();
      DeploymentOptions deploymentOptions = new DeploymentOptions()
        .setInstances(5);
      vertx.deployVerticle(Gateway.class,deploymentOptions,httpDeployPromise);
      return httpDeployPromise.future();
    }).setHandler(deploymentResult->{
      if(deploymentResult.succeeded()){
        startPromise.complete();
      }
      else {
        startPromise.fail(deploymentResult.cause());
      }
    });
  }
}
