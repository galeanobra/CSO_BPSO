package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.MOEADReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;
import org.uma.jmetal.util.termination.Termination;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * This class is intended to provide an implementation of the MOEA/D algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MOEAD<S extends Solution<?>> extends ComponentBasedEvolutionaryAlgorithm<S> {

    /**
     * Constructor
     */
    public MOEAD(
            Evaluation<S> evaluation,
            SolutionsCreation<S> initialPopulationCreation,
            Termination termination,
            RandomMatingPoolSelection<S> selection,
            CrossoverAndMutationVariation<S> variation,
            MOEADReplacement<S> replacement) {
        super(
                "MOEAD",
                evaluation,
                initialPopulationCreation,
                termination,
                selection,
                variation,
                replacement);
    }

    /**
     * Constructor
     *
     * @param problem
     * @param populationSize
     * @param mutationOperator
     * @param crossoverOperator
     * @param aggregativeFunction
     * @param neighborhoodSelectionProbability
     * @param maximumNumberOfReplacedSolutions
     * @param neighborhoodSize
     * @param weightVectorDirectory
     * @param termination
     */
    public MOEAD(
            Problem<S> problem,
            int populationSize,
            MutationOperator<S> mutationOperator,
            CrossoverOperator<S> crossoverOperator,
            AggregativeFunction aggregativeFunction,
            double neighborhoodSelectionProbability,
            int maximumNumberOfReplacedSolutions,
            int neighborhoodSize,
            String weightVectorDirectory,
            Termination termination) {
        this.name = "MOEAD";
        this.problem = problem;
        this.observable = new DefaultObservable<>(name);
        this.attributes = new HashMap<>();

        SequenceGenerator<Integer> subProblemIdGenerator =
                new IntegerPermutationGenerator(populationSize);

        this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

        int offspringPopulationSize = 1;
        this.variation =
                new CrossoverAndMutationVariation<>(
                        offspringPopulationSize, crossoverOperator, mutationOperator);

        WeightVectorNeighborhood<S> neighborhood = null;

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
                new PopulationAndNeighborhoodMatingPoolSelection<S>(
                        variation.getMatingPoolSize(),
                        subProblemIdGenerator,
                        neighborhood,
                        neighborhoodSelectionProbability,
                        true);

        this.replacement =
                new MOEADReplacement<S>(
                        (PopulationAndNeighborhoodMatingPoolSelection<S>) selection,
                        neighborhood,
                        aggregativeFunction,
                        subProblemIdGenerator,
                        maximumNumberOfReplacedSolutions);

        this.termination = termination;

        this.evaluation = new SequentialEvaluation<>(problem);

        this.archive = null;
    }
}
