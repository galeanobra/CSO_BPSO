package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.MOEADReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;
import org.uma.jmetal.util.termination.Termination;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * This class is intended to provide an implementation of the MOEA/D-DE algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MOEADDE extends ComponentBasedEvolutionaryAlgorithm<DoubleSolution> {

  /** Constructor */
  public MOEADDE(
        Evaluation<DoubleSolution> evaluation,
        SolutionsCreation<DoubleSolution> initialPopulationCreation,
        Termination termination,
        PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
        DifferentialCrossoverVariation variation,
        MOEADReplacement<DoubleSolution> replacement) {
        super(
        "MOEAD-DE",
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement);
        }


  /**
   * Constructor with the parameters used in the paper describing MOEA/D-DE.
   *
   * @param problem
   * @param populationSize
   * @param f
   * @param cr
   * @param neighborhoodSelectionProbability
   * @param maximumNumberOfReplacedSolutions
   * @param neighborhoodSize
   * @param termination
   */
  public MOEADDE(
      Problem<DoubleSolution> problem,
      int populationSize,
      double cr,
      double f,
      AggregativeFunction aggregativeFunction,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborhoodSize,
      String weightVectorDirectory,
      Termination termination) {
    this.name = "MOEAD-DE" ;
    this.problem = problem ;
    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    SequenceGenerator<Integer> subProblemIdGenerator =
        new IntegerPermutationGenerator(populationSize);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    PolynomialMutation mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int offspringPopulationSize = 1;
    this.variation =
        new DifferentialCrossoverVariation(
            offspringPopulationSize, crossover, mutation, subProblemIdGenerator);

    WeightVectorNeighborhood<DoubleSolution> neighborhood = null ;
    if (problem.getNumberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.getNumberOfObjectives(),
                neighborhoodSize,
                 weightVectorDirectory);
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    this.selection =
        new PopulationAndNeighborhoodMatingPoolSelection<>(variation.getMatingPoolSize(),
            subProblemIdGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    this.replacement =
        new MOEADReplacement<>(
            (PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution>) selection,
            neighborhood,
            aggregativeFunction,
            subProblemIdGenerator,
            maximumNumberOfReplacedSolutions);

    this.termination = termination ;

    this.evaluation = new SequentialEvaluation<>(problem) ;

    this.archive = null ;
  }
}
