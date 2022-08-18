package org.uma.jmetal.util.evaluator.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class SequentialSolutionListEvaluator<S> implements SolutionListEvaluator<S> {

  @Override
  public List<S> evaluate(List<S> solutionList, Problem<S> problem) throws JMetalException {
    solutionList.forEach(problem::evaluate);

    return solutionList;
  }

  @Override
  public void shutdown() {
    // This method is an intentionally-blank override.
  }
}
