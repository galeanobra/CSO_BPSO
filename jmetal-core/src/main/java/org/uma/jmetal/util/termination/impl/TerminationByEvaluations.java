package org.uma.jmetal.util.termination.impl;

import org.uma.jmetal.util.termination.Termination;

import java.util.Map;

/**
 * Class that allows to check the termination condition based on a maximum number of indicated
 * evaluations.
 *
 *  @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByEvaluations implements Termination {
  private final int maximumNumberOfEvaluations;

  public TerminationByEvaluations(int maximumNumberOfEvaluations) {
    this.maximumNumberOfEvaluations = maximumNumberOfEvaluations ;
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    int currentNumberOfEvaluations = (int) algorithmStatusData.get("EVALUATIONS") ;

    return (currentNumberOfEvaluations >= maximumNumberOfEvaluations) ;
  }
}
