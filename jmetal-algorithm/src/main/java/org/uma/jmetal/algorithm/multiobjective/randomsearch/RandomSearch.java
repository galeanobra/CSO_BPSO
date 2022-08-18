package org.uma.jmetal.algorithm.multiobjective.randomsearch;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import java.util.List;

/**
 * This class implements a simple random search algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSearch<S extends Solution<?>> implements Algorithm<List<S>> {
    private final Problem<S> problem;
    private final int maxEvaluations;
    NonDominatedSolutionListArchive<S> nonDominatedArchive;

    /**
     * Constructor
     */
    public RandomSearch(Problem<S> problem, int maxEvaluations) {
        this.problem = problem;
        this.maxEvaluations = maxEvaluations;
        nonDominatedArchive = new NonDominatedSolutionListArchive<S>();
    }

    /* Getter */
  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  @Override public void run() {
    for (int i = 0; i < maxEvaluations; i++) {
      S newSolution = problem.createSolution() ;
      problem.evaluate(newSolution);
      nonDominatedArchive.add(newSolution);
    }
  }

  @Override public List<S> getResult() {
    return nonDominatedArchive.getSolutionList();
  }

  @Override public String getName() {
    return "RS" ;
  }

  @Override public String getDescription() {
    return "Multi-objective random search algorithm" ;
  }
} 
