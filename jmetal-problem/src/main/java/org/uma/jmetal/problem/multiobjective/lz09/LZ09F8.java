package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F8
 */
@SuppressWarnings("serial")
public class LZ09F8 extends AbstractDoubleProblem {

  private final LZ09 lz09;

  /**
   * Creates a default LZ09F8 problem (10 variables and 2 objectives)
   */
  public LZ09F8() {
    this(21, 4, 21);
  }

  /**
   * Creates a LZ09F8 problem instance
   */
  public LZ09F8(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    setNumberOfVariables(10);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F8");

    lz09 = new LZ09(getNumberOfVariables(),
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(solution.objectives().length);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.variables().get(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = y.get(i);
    }
    return solution ;
  }
}

