package org.uma.jmetal.experimental.componentbasedalgorithm.example.multiobjective.moead;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the MOEA/D algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADDefaultConfigurationExample extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments.
     * @throws SecurityException Invoking command: java
     *                           org.uma.jmetal.runner.multiobjective.moead.MOEADRunner problemName [referenceFront]
     */
    public static void main(String[] args) throws FileNotFoundException {
        DoubleProblem problem;
        MOEAD<DoubleSolution> algorithm;
        MutationOperator<DoubleSolution> mutation;
        CrossoverOperator<DoubleSolution> crossover;

        String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
        String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        int populationSize = 100;

        crossover = new SBXCrossover(1.0, 20.0);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        double neighborhoodSelectionProbability = 0.9;
        int neighborhoodSize = 20;

        int maximumNumberOfReplacedSolutions = 2;

        AggregativeFunction aggregativeFunction = new PenaltyBoundaryIntersection();

        algorithm =
                new MOEAD<>(
                        problem,
                        populationSize,
                        mutation,
                        crossover,
                        aggregativeFunction,
                        neighborhoodSelectionProbability,
                        maximumNumberOfReplacedSolutions,
                        neighborhoodSize,
                        "resources/weightVectorsFiles/moead",
                        new TerminationByEvaluations(25000));

        algorithm.run();

        List<DoubleSolution> population = algorithm.getResult();
        JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
        JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
                .print();

        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

        System.exit(0);
    }
}
