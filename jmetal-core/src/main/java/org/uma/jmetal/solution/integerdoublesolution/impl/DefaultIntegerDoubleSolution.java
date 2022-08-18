package org.uma.jmetal.solution.integerdoublesolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integerdoublesolution.IntegerDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.HashMap;
import java.util.List;

/**
 * Defines an implementation of interface {@Link IntegerDoubleSolution}. This kind of solutions
 * contains a two solutions: an integer one and double one. The number of variables is then equal to
 * two, having the first and second variable assigned the integer and the double solution,
 * respectively.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class DefaultIntegerDoubleSolution extends AbstractSolution<Solution<?>>
    implements IntegerDoubleSolution {

  /** Constructor */
  public DefaultIntegerDoubleSolution(
      List<Pair<Integer, Integer>> integerBounds,
      List<Pair<Double, Double>> doubleBounds,
      int numberOfObjectives,
      int numberOfConstraints) {
    super(2, numberOfObjectives, numberOfConstraints);

    variables().set(
        0, new DefaultIntegerSolution(integerBounds, numberOfObjectives, numberOfConstraints));
    variables().set(
        1, new DefaultDoubleSolution(doubleBounds, numberOfObjectives, numberOfConstraints));
  }

  /** Constructor */
  public DefaultIntegerDoubleSolution(
      List<Pair<Integer, Integer>> integerBounds,
      List<Pair<Double, Double>> doubleBounds,
      int numberOfObjectives) {
    this(integerBounds, doubleBounds, numberOfObjectives, 0);
  }

  /** Constructor */
  public DefaultIntegerDoubleSolution(
      IntegerSolution integerSolution, DoubleSolution doubleSolution) {
    super(2, integerSolution.objectives().length, integerSolution.constraints().length);
    Check.that(
        integerSolution.objectives().length == doubleSolution.objectives().length,
        "The two solutions must have the same number of objectives");
    Check.that(
        integerSolution.constraints().length == doubleSolution.constraints().length,
        "The two solutions must have the same number of constraints");

    variables().set(0, integerSolution);
    variables().set(1, doubleSolution);
  }

  /** Copy constructor */
  public DefaultIntegerDoubleSolution(DefaultIntegerDoubleSolution solution) {
    super(
        solution.variables().size(),
        solution.objectives().length,
        solution.constraints().length);

    for (int i = 0; i < solution.variables().size(); i++) {
      variables().set(i, solution.variables().get(i).copy());
    }

    for (int i = 0; i < solution.objectives().length; i++) {
      objectives()[i] = solution.objectives()[i];
    }

    for (int i = 0; i < solution.constraints().length; i++) {
      constraints()[i] =  solution.constraints()[i];
    }

    attributes = new HashMap<Object, Object>(solution.attributes);
  }

  @Override
  public IntegerSolution getIntegerSolution() {
    return (IntegerSolution) variables().get(0);
  }

  @Override
  public DoubleSolution getDoubleSolution() {
    return (DoubleSolution) variables().get(1);
  }

  @Override
  public DefaultIntegerDoubleSolution copy() {
    return new DefaultIntegerDoubleSolution(this);
  }
}
