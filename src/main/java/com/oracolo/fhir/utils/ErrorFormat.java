package com.oracolo.fhir.utils;

public class ErrorFormat {


  private String errorCode;

  private ErrorFormat(String errorCode) {
    this.errorCode = errorCode;
  }

  public static ErrorFormat createFormat(int code) {

    switch (code) {
      case 422:
        return new ErrorFormat("business-rule");
      case 410:
        return new ErrorFormat("deleted");
      case 404:
        return new ErrorFormat("not-found");
      default: //mongo db connection failure
        return new ErrorFormat("exception");

    }
  }


  public String getFhirErrorCode() {
    return errorCode;
  }
}
