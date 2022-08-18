package org.uma.jmetal.solution.impl;

import org.junit.Test;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class DefaultIntegerPermutationSolutionTest {

  @Test
  public void shouldConstructorCreateAValidSolution() {
    int permutationLength = 20;
    AbstractIntegerPermutationProblem problem =
        new MockIntegerPermutationProblem(permutationLength);
    PermutationSolution<Integer> solution = problem.createSolution();

    List<Integer> values = new ArrayList<>();
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      values.add(solution.variables().get(i));
    }

    Collections.sort(values);

    List<Integer> expectedList = new ArrayList<>(permutationLength);
    for (int i = 0; i < permutationLength; i++) {
      expectedList.add(i);
    }

    assertArrayEquals(expectedList.toArray(), values.toArray());
  }

  /** Mock class representing a integer permutation problem */
  @SuppressWarnings("serial")
  private class MockIntegerPermutationProblem extends AbstractIntegerPermutationProblem {

    /** Constructor */
    public MockIntegerPermutationProblem(int permutationLength) {
      setNumberOfVariables(permutationLength);
      setNumberOfObjectives(2);
    }

    @Override
    public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
      return solution;
    }

    @Override
    public int getLength() {
      return getNumberOfVariables();
    }
  }
}
