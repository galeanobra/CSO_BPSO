package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/** Class representing problem Schaffer */
public class Schaffer extends AbstractDoubleProblem {
  private static final long serialVersionUID = -2366503015218789989L;

  /** Constructor. Creates a default instance of problem Schaffer */
  public Schaffer() {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setName("Schaffer");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-100000.0);
      upperLimit.add(100000.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.objectives().length];
    double value = solution.variables().get(0);

    f[0] = value * value;
    f[1] = (value - 2.0) * (value - 2.0);

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution;
  }
}
