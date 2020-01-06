package com.oracolo.fhir.utils;

import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.OperationOutcomeIssue;
import io.vertx.core.json.JsonObject;
import org.json.JSONObject;
import org.json.XML;

import java.util.UUID;

public class ResponseFormat {


  private FhirHttpHeader contentType;
  private FhirHttpHeader prefer;
  private FhirHttpHeader accept;
  private String finalResponse;

  public ResponseFormat() {

  }

  public ResponseFormat(String finalResponse, FhirHttpHeader contentType) {
    this.finalResponse = finalResponse;
    this.contentType = contentType;
  }


  public ResponseFormat format(JsonObject domainResource) {
    //prefer header set from client
    if (prefer != null) {
      if (prefer.value().equals(FhirHttpHeader.PREFER_MINIMAL.value())) {
        return new ResponseFormat("", FhirHttpHeader.TEXT);
      }
      //prefer outcome, setting xml or json according to the format
      if (prefer.value().equals(FhirHttpHeader.PREFER_OUTCOME.value())) {
        OperationOutcome operationOutcome = new OperationOutcome();
        operationOutcome.setId(UUID.randomUUID().toString());
        OperationOutcomeIssue operationOutcomeIssue = new OperationOutcomeIssue();
        operationOutcomeIssue.setCode("informational");
        operationOutcomeIssue.setSeverity("information")
          .setDiagnostics("Resource correctly created");
        operationOutcome.setIssue(operationOutcomeIssue);
        //possible accept header: application/fhir+json; application/json, application/xml, */json, */xml ecc.
        if (accept != null && accept.value().contains("xml")) {
          JSONObject jsonObject = new JSONObject(domainResource.encodePrettily());

          String domainResourceXml = XML
            .toString(jsonObject, "" + jsonObject.getString("resourceType") + " xmlns=\"http://hl7.org/fhir\"");
          String finalXML = XML.unescape(domainResourceXml);
          return new ResponseFormat(finalXML, FhirHttpHeader.APPLICATION_XML);
        }

        String operationOutcomeString = JsonObject.mapFrom(operationOutcome).encodePrettily();
        return new ResponseFormat(operationOutcomeString, FhirHttpHeader.APPLICATION_JSON);
      }
      //prefer is neither minimal or outcome -> prefer = representation. According to the accept header, creating xml version
      //if needed, otherwise the default case is json
      if (accept != null && accept.value().contains("xml")) {
        JSONObject jsonObject = new JSONObject(domainResource.encodePrettily());

        String domainResourceXml = XML
          .toString(jsonObject, "" + jsonObject.getString("resourceType") + " xmlns=\"http://hl7.org/fhir\"");
        String finalXML = XML.unescape(domainResourceXml);
        return new ResponseFormat(finalXML, FhirHttpHeader.APPLICATION_XML);
      }
      String domainResourceString = domainResource.encodePrettily();
      return new ResponseFormat(domainResourceString, FhirHttpHeader.APPLICATION_JSON);
    }
    //prefer is not set, so returning the resource in xml if set, otherwise default case is json
    if (accept != null && accept.value().contains("xml")) {
      JSONObject jsonObject = new JSONObject(domainResource.encodePrettily());

      String domainResourceXml = XML
        .toString(jsonObject, "" + jsonObject.getString("resourceType") + " xmlns=\"http://hl7.org/fhir\"");
      String finalXML = XML.unescape(domainResourceXml);
      return new ResponseFormat(finalXML, FhirHttpHeader.APPLICATION_XML);
    }
    return new ResponseFormat(domainResource.encodePrettily(), FhirHttpHeader.APPLICATION_JSON);

  }

  public FhirHttpHeader contentType() {
    return this.contentType;
  }

  /**
   * Final response to write on routing context
   *
   * @return
   */
  public String response() {
    return finalResponse;
  }

  public ResponseFormat withPreferHeader(FhirHttpHeader prefer) {
    if (!prefer.name().equals(FhirHttpHeader.PREFER)) {
      throw new IllegalArgumentException("Wrong Header value");
    }
    this.prefer = prefer;
    return this;
  }

  public ResponseFormat withAcceptHeader(FhirHttpHeader accept) {
    if (!accept.name().equals(FhirHttpHeader.ACCEPT)) {
      throw new IllegalArgumentException("Wrong Header value");
    }
    this.accept = accept;
    return this;
  }


  public FhirHttpHeader prefer() {
    return this.prefer;
  }


}
