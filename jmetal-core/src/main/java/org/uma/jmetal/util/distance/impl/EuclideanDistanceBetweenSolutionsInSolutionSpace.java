package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionsInSolutionSpace<S extends Solution<Double>>
    implements Distance<S, S> {

  private final EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors();

  @Override
  public double compute(S solution1, S solution2) {
    double[] vector1 = new double[solution1.variables().size()] ;
    double[] vector2 = new double[solution1.variables().size()] ;
    for (int i = 0 ; i < solution1.variables().size(); i++) {
      vector1[i] = solution1.variables().get(i) ;
      vector2[i] = solution2.variables().get(i) ;
    }

    return distance.compute(vector1, vector2) ;
  }
}
