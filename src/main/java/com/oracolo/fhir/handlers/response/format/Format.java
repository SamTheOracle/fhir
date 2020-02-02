package com.oracolo.fhir.handlers.response.format;

public class Format {

  private String response;
  private String contentType;

  public Format(String response, String contentType) {
    this.response = response;
    this.contentType = contentType;
  }

  public String getResponse() {
    return response;
  }


  public String getContentType() {
    return contentType;
  }

}
