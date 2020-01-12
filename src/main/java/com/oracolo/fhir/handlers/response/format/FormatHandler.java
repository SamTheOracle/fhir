package com.oracolo.fhir.handlers.response.format;

public interface FormatHandler {

  FormatHandler withAcceptHeader(String value);

  FormatHandler withPreferHeader(String value);

  Format createFormat(Object returnObject);
}
