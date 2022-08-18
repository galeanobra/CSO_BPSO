package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE21Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE21();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(2, problem.getNumberOfObjectives());
    assertEquals(3, problem.getNumberOfConstraints());
    assertEquals("CRE21", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE21();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.variables().size());
    assertEquals(2, solution.objectives().length);
    assertEquals(3, solution.constraints().length);
  }
}
