package org.uma.jmetal.experimental.componentbasedalgorithm.example.multiobjective.moead;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the MOEA/D-DE algorithm using class {@link MOEADDE} and the
 * constructor taking the parameters used in the paper describing the algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADDEDefaultConfigurationExample extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws FileNotFoundException {
    DoubleProblem problem;
    MOEADDE algorithm;

    String problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
    String referenceParetoFront = "resources/referenceFrontsCSV/LZ09_F2.csv";

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;
    int maximumNumberOfFunctionEvaluations = 150000;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    algorithm =
        new MOEADDE(
            problem,
            populationSize,
            cr,
            f,
            aggregativeFunction,
            neighborhoodSelectionProbability,
            maximumNumberOfReplacedSolutions,
            neighborhoodSize,
            "resources/weightVectorFiles/moead",
            new TerminationByEvaluations(maximumNumberOfFunctionEvaluations));

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
