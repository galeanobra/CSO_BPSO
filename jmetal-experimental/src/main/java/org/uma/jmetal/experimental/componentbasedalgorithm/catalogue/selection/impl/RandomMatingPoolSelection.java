package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RandomMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private final int matingPoolSize;

  public RandomMatingPoolSelection(int matingPoolSize) {
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    Check.notNull(solutionList);

    List<S> matingPool = new ArrayList<>();
    IntStream.range(0, matingPoolSize)
        .forEach(
            i ->
                matingPool.add(
                    solutionList.get(JMetalRandom.getInstance().nextInt(0, solutionList.size()-1))));

    return matingPool;
  }
}
