package com.oracolo.fhir.database;

public class UpdateResult {

  private String body;
  private Integer status;


  public String getBody() {
    return body;
  }

  public UpdateResult setBody(String body) {
    this.body = body;
    return this;
  }

  public Integer getStatus() {
    return status;
  }

  public UpdateResult setStatus(Integer status) {
    this.status = status;
    return this;
  }
}
