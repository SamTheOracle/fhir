package com.oracolo.fhir.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.FhirDomainResourceAbstract;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of error, warning, or information messages that result from a system action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationOutcome extends FhirDomainResourceAbstract {
  /**
   * Resource type
   * <p>Cardinality: 1..1</p>
   */
  private String resourceType = "OperationOutcome";

  /**
   * An error, warning, or information message that results from a system action.
   * <p>Cardinality: 1..1</p>
   */
  private List<OperationOutcomeIssue> issue;

  public String getResourceType() {
    return resourceType;
  }

  public List<OperationOutcomeIssue> getIssue() {
    return issue;
  }

  public OperationOutcome setIssue(List<OperationOutcomeIssue> issue) {
    this.issue = issue;
    return this;
  }

  public OperationOutcome addNewIssue(OperationOutcomeIssue issue) {
    if (this.issue == null) {
      this.issue = new ArrayList<>();
    }
    this.issue.add(issue);
    return this;
  }
}
