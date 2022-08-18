package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the MOEA/D-DE algorithm described in :
 * Hui Li; Qingfu Zhang, "Multiobjective Optimization Problems With Complicated Pareto Sets,
 * MOEA/D and NSGA-II," Evolutionary Computation, IEEE Transactions on , vol.13, no.2, pp.284,302,
 * April 2009. doi: 10.1109/TEVC.2008.925798
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MOEAD<S extends Solution<?>> extends AbstractMOEAD<S> {
    protected DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public MOEAD(Problem<S> problem,
                 int populationSize,
                 int resultPopulationSize,
                 int maxEvaluations,
                 MutationOperator<S> mutation,
                 CrossoverOperator<S> crossover,
                 FunctionType functionType,
                 String dataDirectory,
                 double neighborhoodSelectionProbability,
                 int maximumNumberOfReplacedSolutions,
                 int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        idealPoint.update(population);

        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<S> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution((DoubleSolution) population.get(subProblemId));
                List<S> children = (List<S>) differentialEvolutionCrossover.execute((List<DoubleSolution>) parents);

                S child = (S) children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                idealPoint.update(child.objectives());
                updateNeighborhood(child, subProblemId, neighborType);
            }
        } while (evaluations < maxEvaluations);

    }

    protected void initializePopulation() {
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            S newSolution = problem.createSolution();

            problem.evaluate(newSolution);
            population.add(newSolution);
        }
    }

    @Override
    public String getName() {
        return "MOEAD";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition";
    }
}
