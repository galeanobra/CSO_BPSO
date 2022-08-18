package org.uma.jmetal.solution.compositesolution;

import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CompositeSolutionTest {

  @Test
  public void shouldConstructorCreateAValidNotNullCompositeSolutionComposedOfASolution() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    3, 0, Arrays.asList(Bounds.create(3.0, 5.0)));

    assertNotNull(new CompositeSolution(Arrays.asList(doubleSolution))) ;
  }

  @Test(expected = Exception.class)
  public void shouldConstructorRaiseAnExceptionIfTheNumberOfObjectivesIsIncoherent() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    3, 0, Arrays.asList(Bounds.create(3.0, 5.0)));
    IntegerSolution integerSolution =
            new DefaultIntegerSolution(
                    2, 0, Arrays.asList(Bounds.create(2, 10)));

    new CompositeSolution(Arrays.asList(doubleSolution, integerSolution)) ;
  }

  @Test
  public void
      shouldConstructorCreateAValidCompositeSolutionComposedOfaDoubleAndAnIntegerSolutions() {
    int numberOfObjectives = 2;
    int numberOfConstraints = 1;
    DoubleSolution doubleSolution =
        new DefaultDoubleSolution(
            numberOfObjectives, numberOfConstraints, List.of(Bounds.create(3.0, 5.0)));
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            numberOfObjectives, numberOfConstraints, List.of(Bounds.create(2, 10)));

    CompositeSolution solution =
        new CompositeSolution(Arrays.asList(doubleSolution, integerSolution));
    assertNotNull(solution);
    assertEquals(2, solution.variables().size());
    assertEquals(numberOfObjectives, solution.objectives().length);
    assertEquals(numberOfConstraints, solution.constraints().length);
    assertEquals(numberOfObjectives, solution.variables().get(0).objectives().length);
    assertEquals(numberOfObjectives, solution.variables().get(1).objectives().length);
    assertEquals(numberOfConstraints, solution.variables().get(0).constraints().length);
    assertEquals(numberOfConstraints, solution.variables().get(1).constraints().length);
    assertTrue(solution.variables().get(0) instanceof DoubleSolution);
    assertTrue(solution.variables().get(1) instanceof IntegerSolution);
  }

  @Test
  public void shouldCopyConstructorWorkProperly() {
    int numberOfObjectives = 2;
    int numberOfConstraints = 1;
    DoubleSolution doubleSolution =
        new DefaultDoubleSolution(
            numberOfObjectives,
            numberOfConstraints,
            Arrays.asList(Bounds.create(3.0, 5.0), Bounds.create(1.0, 3.0)));
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            numberOfObjectives, numberOfConstraints, Arrays.asList(Bounds.create(2, 10)));

    CompositeSolution solution =
        new CompositeSolution(Arrays.asList(doubleSolution, integerSolution));

    CompositeSolution newSolution = new CompositeSolution(solution) ;

    assertEquals(solution.variables().size(), newSolution.variables().size());
    assertEquals(solution.objectives().length, newSolution.objectives().length);
    assertEquals(solution.constraints().length, newSolution.constraints().length);

    assertEquals(solution.variables().get(0).variables().size(), newSolution.variables().get(0).variables().size());
    assertEquals(solution.variables().get(1).variables().size(), newSolution.variables().get(1).variables().size());
    assertNotSame(solution.variables().get(0), newSolution.variables().get(0));
    assertNotSame(solution.variables().get(1), newSolution.variables().get(1));

    assertEquals(solution.variables().get(0).variables(), newSolution.variables().get(0).variables());
    assertEquals(solution.variables().get(1).variables(), newSolution.variables().get(1).variables());
  }
}
