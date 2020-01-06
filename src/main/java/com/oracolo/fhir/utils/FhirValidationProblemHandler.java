package com.oracolo.fhir.utils;

import com.oracolo.fhir.model.exceptions.NotValidFhirResourceException;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;

import java.util.List;

public class FhirValidationProblemHandler implements ProblemHandler {
  /**
   * Handles the problems found while validating a JSON document.
   *
   * @param problems the problems found, cannot be {@code null}.
   */
  private int size;

  @Override
  public void handleProblems(List<Problem> problems) {
    if (!problems.isEmpty()) {
      this.size = problems.size();

    }
  }

  public void checkProblems() throws NotValidFhirResourceException {

    if (size > 0) {
      throw new NotValidFhirResourceException("Not valid resource");
    }
  }

}
