package org.uma.jmetal.algorithm.multiobjective.moead;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F2;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F6;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MOEADIT {

  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
    LZ09F2 problem = new LZ09F2();

    double cr = 1.0;
    double f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

    algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
        .setCrossover(crossover)
        .setMutation(mutation)
        .setMaxEvaluations(150000)
        .setPopulationSize(300)
        .setResultPopulationSize(100)
        .setNeighborhoodSelectionProbability(0.9)
        .setMaximumNumberOfReplacedSolutions(2)
        .setNeighborSize(20)
        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
        .setDataDirectory(
            "MOEAD_Weights")
        .build();

    algorithm.run() ;
    List<DoubleSolution> population = algorithm.getResult();

    assertTrue(population.size() == 100);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValueWhenSolvingTheLZ09F2Instance() throws IOException {
    LZ09F2 problem = new LZ09F2();

    double cr = 1.0;
    double f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

    algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
        .setCrossover(crossover)
        .setMutation(mutation)
        .setMaxEvaluations(150000)
        .setPopulationSize(300)
        .setResultPopulationSize(100)
        .setNeighborhoodSelectionProbability(0.9)
        .setMaximumNumberOfReplacedSolutions(2)
        .setNeighborSize(20)
        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
        .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator hypervolume =
            new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/LZ09_F2.csv", ","));

    // Rationale: the default problem is LZ09F2", and MOEA/D, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.96

    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.65);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValueWhenSolvingTheLZ09F6Instance() throws Exception {
    LZ09F6 problem = new LZ09F6();

    JMetalRandom.getInstance().setSeed(1);

    double cr = 1.0;
    double f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
            mutationDistributionIndex);

    algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setMaxEvaluations(150000)
            .setPopulationSize(300)
            .setResultPopulationSize(100)
            .setNeighborhoodSelectionProbability(0.9)
            .setMaximumNumberOfReplacedSolutions(2)
            .setNeighborSize(20)
            .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
            .setDataDirectory("../resources/weightVectorFiles/moead")
            .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/LZ09_F6.csv", ","));

    // Rationale: the default problem is LZ09F6", and MOEA/D, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.35
    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.35);

    JMetalRandom.getInstance().setSeed(System.currentTimeMillis());
  }
}
