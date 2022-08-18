package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 * Modified by Juanjo on 07/04/2015
 */
@SuppressWarnings("serial")
public class CrowdingDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
    private final Comparator<S> crowdingDistanceComparator;
    private final DensityEstimator<S> crowdingDistance;

    public CrowdingDistanceArchive(int maxSize) {
        super(maxSize);
        crowdingDistance = new CrowdingDistanceDensityEstimator<S>();
        crowdingDistanceComparator = Comparator.comparing(crowdingDistance::getValue).reversed();
    }

    @Override
    public void prune() {
        if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), crowdingDistanceComparator) ;
      getSolutionList().remove(worst);
    }
  }

  @Override
  public Comparator<S> getComparator() {
    return crowdingDistanceComparator ;
  }

  @Override
  public void computeDensityEstimator() {
    crowdingDistance.compute(getSolutionList());
  }
}
