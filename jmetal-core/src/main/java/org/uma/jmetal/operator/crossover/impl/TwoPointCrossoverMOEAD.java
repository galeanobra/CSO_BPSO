package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by FlapKap on 27-05-2017.
 */
@SuppressWarnings("serial")
public class TwoPointCrossoverMOEAD<T> implements CrossoverOperator<Solution<T>> {
    NPointCrossoverMOEAD<T> operator;

    public TwoPointCrossoverMOEAD(double probability) {
        this.operator = new NPointCrossoverMOEAD<T>(probability, 2);
    }

    @Override
    public List<Solution<T>> execute(List<Solution<T>> solutions) {
        return operator.execute(solutions);
    }

    @Override
    public double getCrossoverProbability() {
        return operator.getCrossoverProbability();
    }

    @Override
    public int getNumberOfRequiredParents() {
        return operator.getNumberOfRequiredParents();
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }
}
