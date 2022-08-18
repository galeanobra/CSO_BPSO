package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SelectionParameter extends CategoricalParameter {
    public SelectionParameter(String args[], List<String> selectionStrategies) {
        super("selection", args, selectionStrategies);
    }

    public MatingPoolSelection<?> getParameter(int matingPoolSize, Comparator<?> comparator) {
        MatingPoolSelection<Solution<?>> result;
        switch (getValue()) {
            case "tournament":
                int tournamentSize = (Integer) findSpecificParameter("selectionTournamentSize").getValue();

                Ranking<Solution<?>> ranking = new FastNonDominatedSortRanking<>();
                DensityEstimator<Solution<?>> densityEstimator = new CrowdingDistanceDensityEstimator<>();

                MultiComparator<Solution<?>> rankingAndCrowdingComparator =
                        new MultiComparator<>(
                                Arrays.asList(Comparator.comparing(ranking::getRank), Comparator.comparing(densityEstimator::getValue).reversed()));
                result = new NaryTournamentMatingPoolSelection<>(tournamentSize, matingPoolSize, rankingAndCrowdingComparator);

                break;
            case "random":
                result = new RandomMatingPoolSelection<>(matingPoolSize);
                break;
            default:
                throw new RuntimeException("Selection component unknown: " + getValue());
        }

        return result;
    }
}