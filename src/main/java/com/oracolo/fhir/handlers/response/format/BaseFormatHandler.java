package com.oracolo.fhir.handlers.response.format;

import com.oracolo.fhir.model.domain.OperationOutcome;
import com.oracolo.fhir.model.domain.OperationOutcomeIssue;
import com.oracolo.fhir.utils.FhirHttpHeader;
import io.vertx.core.json.JsonObject;
import org.json.JSONObject;
import org.json.XML;

import java.util.UUID;

public class BaseFormatHandler implements FormatHandler {


  private FhirHttpHeader prefer;
  private FhirHttpHeader accept;

  public BaseFormatHandler() {

  }


  @Override
  public FormatHandler withAcceptHeader(String value) {
    this.accept = FhirHttpHeader.of(FhirHttpHeader.ACCEPT, value);
    return this;
  }

  @Override
  public FormatHandler withPreferHeader(String value) {
    this.prefer = FhirHttpHeader.of(FhirHttpHeader.PREFER, value);

    return this;
  }

  @Override
  public Format createFormat(Object returnObject) {
    JsonObject domainResource = JsonObject.mapFrom(returnObject);
    //prefer header set from client
    if (prefer != null) {
      if (prefer.value().equals(FhirHttpHeader.PREFER_MINIMAL.value())) {
        return new Format("", FhirHttpHeader.TEXT.value());
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
          return new Format(finalXML, FhirHttpHeader.APPLICATION_XML.value());
        }

        String operationOutcomeString = JsonObject.mapFrom(operationOutcome).encodePrettily();
        return new Format(operationOutcomeString, FhirHttpHeader.APPLICATION_JSON.value());
      }
      //prefer is neither minimal or outcome -> prefer = representation. According to the accept header, creating xml version
      //if needed, otherwise the default case is json
      if (accept != null && accept.value().contains("xml")) {
        JSONObject jsonObject = new JSONObject(domainResource.encodePrettily());

        String domainResourceXml = XML
          .toString(jsonObject, "" + jsonObject.getString("resourceType") + " xmlns=\"http://hl7.org/fhir\"");
        String finalXML = XML.unescape(domainResourceXml);
        return new Format(finalXML, FhirHttpHeader.APPLICATION_XML.value());
      }
      String domainResourceString = domainResource.encodePrettily();
      return new Format(domainResourceString, FhirHttpHeader.APPLICATION_JSON.value());
    }
    //prefer is not set, so returning the resource in xml if set, otherwise default case is json
    if (accept != null && accept.value().contains("xml")) {
      JSONObject jsonObject = new JSONObject(domainResource.encodePrettily());

      String domainResourceXml = XML
        .toString(jsonObject, "" + jsonObject.getString("resourceType") + " xmlns=\"http://hl7.org/fhir\"");
      String finalXML = XML.unescape(domainResourceXml);
      return new Format(finalXML, FhirHttpHeader.APPLICATION_XML.value());
    }
    return new Format(domainResource.encodePrettily(), FhirHttpHeader.APPLICATION_JSON.value());
  }
}
