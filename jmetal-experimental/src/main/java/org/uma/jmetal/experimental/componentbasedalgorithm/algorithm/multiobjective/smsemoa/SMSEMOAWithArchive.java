package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.termination.Termination;

import java.util.List;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
@SuppressWarnings("serial")
public class SMSEMOAWithArchive<S extends Solution<?>> extends SMSEMOA<S> {
  private final Archive<S> archive;

  /** Constructor */
  public SMSEMOAWithArchive(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Hypervolume<S> hypervolume,
      Ranking<S> ranking,
      Archive<S> archive) {
    super(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        hypervolume,
        ranking);
    this.archive = archive;
  }

  /**
   * Constructor
   *
   * @param problem
   * @param populationSize
   * @param crossoverOperator
   * @param mutationOperator
   * @param termination
   * @param archive
   */
  public SMSEMOAWithArchive(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Archive<S> archive) {
    this(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        new PISAHypervolume<>(),
        new FastNonDominatedSortRanking<>(),
        archive);
  }

  @Override
  protected List<S> evaluatePopulation(List<S> solutionList) {
    List<S> evaluatedSolutionList = super.evaluatePopulation(solutionList);
    for (S solution : evaluatedSolutionList) {
      archive.add(solution);
    }

    return evaluatedSolutionList;
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList() ;
  }

  public Archive<S> getArchive() {
    return archive;
  }

  public List<S> getPopulation() {
    return population ;
  }
}
