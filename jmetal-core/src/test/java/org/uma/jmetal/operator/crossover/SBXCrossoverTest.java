package org.uma.jmetal.operator.crossover;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Note: this class does check that the SBX crossover operator does not return invalid
 * values, but not that it works properly (@see SBXCrossoverWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SBXCrossoverTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1 ;
    SBXCrossover crossover = new SBXCrossover(crossoverProbability, 2.0) ;
    assertEquals(crossoverProbability, (Double) ReflectionTestUtils
        .getField(crossover, "crossoverProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    SBXCrossover crossover = new SBXCrossover(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(crossover, "distributionIndex"), EPSILON) ;
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double crossoverProbability = -0.1 ;
    new SBXCrossover(crossoverProbability, 2.0) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    new SBXCrossover(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetProbabilityReturnTheRightValue() {
    SBXCrossover crossover = new SBXCrossover(0.1, 20.0) ;
    assertEquals(0.1, crossover.getCrossoverProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    SBXCrossover crossover = new SBXCrossover(0.1, 30.0) ;
    assertEquals(30.0, crossover.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    SBXCrossover crossover = new SBXCrossover(0.1, 20.0) ;

    crossover.execute(null) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteWithInvalidSolutionListSizeThrowAnException() {
    DoubleProblem problem = new MockDoubleProblem(1) ;

    SBXCrossover crossover = new SBXCrossover(0.1, 20.0) ;

    crossover.execute(Arrays.asList(problem.createSolution(), problem.createSolution(), problem.createSolution())) ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfProbabilityIsZero() {
    double crossoverProbability = 0.0;
    double distributionIndex = 20.0 ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfNotCrossoverIsApplied() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;

    double crossoverProbability = 0.9;
    double distributionIndex = 20.0 ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);
    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
    verify(randomGenerator).getRandomValue() ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnValidSolutions() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.6, 0.6) ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    Bounds<Double> bounds0 = solutions.get(0).getBounds(0);
    Bounds<Double> bounds1 = solutions.get(1).getBounds(0);
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    verify(randomGenerator, times(4)).getRandomValue();
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsWithSimilarValueReturnTheSameVariables() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2) ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;
    solutions.get(0).variables().set(0, 1.0);
    solutions.get(1).variables().set(0, 1.0);

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), newSolutions.get(0).variables().get(0), EPSILON) ;
    assertEquals(solutions.get(1).variables().get(0), newSolutions.get(1).variables().get(0), EPSILON) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldCrossingTwoDoubleVariableSolutionsReturnValidSolutions() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.8, 0.3, 0.2, 0.8, 0.3) ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(2) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.variables().set(0, 1.0);
    solution1.variables().set(1, 2.0);
    solution2.variables().set(0, 2.0);
    solution2.variables().set(1, 1.0);
    List<DoubleSolution> solutions = Arrays.asList(solution1, solution2) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    Bounds<Double> bounds0 = solutions.get(0).getBounds(0);
    Bounds<Double> bounds1 = solutions.get(1).getBounds(0);
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(1), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(1), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(1), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(1), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    verify(randomGenerator, times(7)).getRandomValue();
  }

  @Test
  public void shouldCrossingTheSecondVariableReturnTheOtherVariablesUnchangedInTheOffspringSolutions() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.3, 0.7,  0.2, 0.2, 0.2, 0.7) ;

    SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(3) ;

    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);
    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), newSolutions.get(1).variables().get(0), EPSILON);
    assertNotEquals(solutions.get(0).variables().get(1), newSolutions.get(0).variables().get(1), EPSILON);
    assertEquals(solutions.get(0).variables().get(2), newSolutions.get(1).variables().get(2), EPSILON);

    assertEquals(solutions.get(1).variables().get(0), newSolutions.get(0).variables().get(0), EPSILON);
    assertNotEquals(solutions.get(1).variables().get(1), newSolutions.get(1).variables().get(1), EPSILON);
    assertEquals(solutions.get(1).variables().get(2), newSolutions.get(0).variables().get(2), EPSILON);

    verify(randomGenerator, times(6)).getRandomValue();
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[0] = 0.0;
      solution.objectives()[1] = 1.0;

      return solution ;
    }
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double crossoverProbability = 0.1;
		int alpha = 20;
		RepairDoubleSolutionWithBoundValue solutionRepair = new RepairDoubleSolutionWithBoundValue();

		List<DoubleSolution> solutions = new LinkedList<>();

    List<Bounds<Double>> bounds = Arrays.asList(Bounds.create(0.0, 1.0)) ;

		solutions.add(new DefaultDoubleSolution(2, bounds));
		solutions.add(new DefaultDoubleSolution(2, bounds));

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new SBXCrossover(crossoverProbability, alpha, solutionRepair).execute(solutions);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new SBXCrossover(crossoverProbability, alpha, solutionRepair, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solutions);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
