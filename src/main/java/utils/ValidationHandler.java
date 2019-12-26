package utils;

import model.exceptions.NotValideFhirResourceException;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;

import java.util.List;

public class ValidationHandler implements ProblemHandler {
  /**
   * Handles the problems found while validating a JSON document.
   *
   * @param problems the problems found, cannot be {@code null}.
   */
  private List<Problem> problems;

  @Override
  public void handleProblems(List<Problem> problems) {
    if (!problems.isEmpty()) {
      this.problems = problems;

    }
  }

  public void checkProblems() throws NotValideFhirResourceException {
    if (problems != null && problems.size() > 0) {
      problems.forEach(System.out::println);
      throw new NotValideFhirResourceException(problems.get(1).getMessage());
    }
  }

}
