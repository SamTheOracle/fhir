package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.DomainResource;

/**
 * A collection of error, warning, or information messages that result from a system action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationOutcome extends DomainResource {
  /**
   * Resource type
   * <p>Cardinality: 1..1</p>
   */
  private String resourceType = "OperationOutcome";

  /**
   * An error, warning, or information message that results from a system action.
   * <p>Cardinality: 1..1</p>
   */
  private OperationOutcomeIssue issue;

  public String getResourceType() {
    return resourceType;
  }

  public OperationOutcomeIssue getIssue() {
    return issue;
  }

  public OperationOutcome setIssue(OperationOutcomeIssue issue) {
    this.issue = issue;
    return this;
  }
}
