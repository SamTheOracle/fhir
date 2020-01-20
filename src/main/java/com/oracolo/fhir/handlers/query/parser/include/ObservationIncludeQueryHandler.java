package com.oracolo.fhir.handlers.query.parser.include;

public class ObservationIncludeQueryHandler {

  public final static String patient = "patient";

  public void parseInclude(String paramValue) {
    String[] parsedValues = paramValue.split(":");
    if (parsedValues.length > 1 & parsedValues[1].equals(patient)) {

    }
  }


}
