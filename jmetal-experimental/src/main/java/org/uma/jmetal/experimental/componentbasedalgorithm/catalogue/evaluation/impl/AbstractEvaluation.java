package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

public abstract  class AbstractEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private final SolutionListEvaluator<S> evaluator;
    private int numberOfComputedEvaluations;
    private final Problem<S> problem;

  public AbstractEvaluation(SolutionListEvaluator<S> evaluator, Problem<S> problem) {
    this.numberOfComputedEvaluations = 0 ;
    this.evaluator = evaluator ;
    this.problem = problem ;
  }

  @Override
  public List<S> evaluate(List<S> solutionList) {
    evaluator.evaluate(solutionList, problem) ;

    numberOfComputedEvaluations += solutionList.size() ;

    return solutionList;
  }

  public int getComputedEvaluations() {
    return numberOfComputedEvaluations ;
  }
}
