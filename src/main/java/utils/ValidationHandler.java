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
  private Problem problem;
  @Override
  public void handleProblems(List<Problem> problems) {
    if(!problems.isEmpty()){
      this.problem = problems.get(0);

    }
  }
  public void checkProblems() throws NotValideFhirResourceException {
    if(problem!=null){
      throw new NotValideFhirResourceException(problem.getMessage());
    }
  }

}
