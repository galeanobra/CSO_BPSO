package org.uma.jmetal.algorithm.multiobjective.mocell;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;

import java.util.List;

/**
 * Created by juanjo
 */
public class MOCellBuilder<S extends Solution<?>> implements AlgorithmBuilder<MOCell<S>> {
    public enum MOCellVariant {MOCell, SteadyStateMOCell, Measures, HybridMOCell}

    /**
     * MOCellBuilder class
     */
    protected final Problem<S> problem;
    protected int maxEvaluations;
    protected int populationSize;
    protected CrossoverOperator<S> crossoverOperator;
    protected MutationOperator<S> mutationOperator;
    protected SelectionOperator<List<S>, S> selectionOperator;
    protected SolutionListEvaluator<S> evaluator;
    protected Neighborhood<S> neighborhood;
    protected BoundedArchive<S> archive;

    protected MOCellVariant variant;

    /**
     * MOCellBuilder constructor
     */
    public MOCellBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        this.problem = problem;
        maxEvaluations = 25000;
        populationSize = 100;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
        neighborhood = new C9<S>((int) Math.sqrt(populationSize), (int) Math.sqrt(populationSize));
        evaluator = new SequentialSolutionListEvaluator<S>();
        archive = new CrowdingDistanceArchive<>(populationSize);

        this.variant = MOCellVariant.MOCell;
    }

    public MOCellBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public MOCellBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;
        this.neighborhood = new C9<S>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
        this.archive = new CrowdingDistanceArchive<>(this.populationSize);
        return this;
    }

    public MOCellBuilder<S> setArchive(BoundedArchive<S> archive) {
        this.archive = archive;

        return this;
    }

    public MOCellBuilder<S> setNeighborhood(Neighborhood<S> neighborhood) {
        this.neighborhood = neighborhood;

        return this;
    }

    public MOCellBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("selectionOperator is null");
        }
        this.selectionOperator = selectionOperator;

        return this;
    }

    public MOCellBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }

    public MOCell<S> build() {
        MOCell<S> algorithm = null;

        if (variant.equals(MOCellVariant.MOCell))
            algorithm = new MOCell<S>(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        else if (variant.equals(MOCellVariant.HybridMOCell))
            algorithm = new HybridMOCell<S>(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        return algorithm;
    }

    /* Getters */
    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public BoundedArchive<S> getArchive() {
        return archive;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return evaluator;
    }

    public MOCellVariant getVariant() {
        return variant;
    }

    public MOCellBuilder<S> setVariant(MOCellVariant variant) {
        this.variant = variant;

        return this;
    }
}
