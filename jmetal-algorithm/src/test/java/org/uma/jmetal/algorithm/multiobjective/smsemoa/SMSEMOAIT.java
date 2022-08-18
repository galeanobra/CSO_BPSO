package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SMSEMOAIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    ZDT4 problem = new ZDT4();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Hypervolume<DoubleSolution> hypervolumeImplementation;
    hypervolumeImplementation = new PISAHypervolume<>();
    hypervolumeImplementation.setOffset(100.0);

    algorithm =
        new SMSEMOABuilder<>(problem, crossover, mutation)
            .setSelectionOperator(new RandomSelection<DoubleSolution>())
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setHypervolumeImplementation(hypervolumeImplementation)
            .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and usually SMSEMOA, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Hypervolume<DoubleSolution> hypervolumeImplementation;
    hypervolumeImplementation = new PISAHypervolume<>();
    hypervolumeImplementation.setOffset(100.0);

    algorithm =
        new SMSEMOABuilder<>(problem, crossover, mutation)
            .setSelectionOperator(new RandomSelection<DoubleSolution>())
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setHypervolumeImplementation(hypervolumeImplementation)
            .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("../resources/referenceFrontsCSV/ZDT1.csv");

    // Rationale: the default problem is ZDT1, and SMSEMOA, configured with standard settings,
    // should
    // return find a front with a hypervolume value higher than 0.65

    double hv = hypervolume.evaluate(population);

    assertTrue(hv > 0.65);
  }
}
