package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to apply a list of crossover operator on the solutions belonging to a list of
 * {@link CompositeSolution} objects. It is required that the operators be compatible with the
 * solutions inside the composite solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CompositeMutation implements MutationOperator<CompositeSolution> {
    private final List<MutationOperator<Solution<?>>> operators;
    private final double mutationProbability = 1.0;

    /**
     * Constructor
     */
    public CompositeMutation(List<?> operators) {
        Check.notNull(operators);
        Check.collectionIsNotEmpty(operators);

        this.operators = new ArrayList<>();
        for (int i = 0; i < operators.size(); i++) {
            Check.that(
                    operators.get(i) instanceof MutationOperator,
          "The operator list does not contain an object implementing class CrossoverOperator");
      this.operators.add((MutationOperator<Solution<?>>) operators.get(i));
    }
  }

  /* Getters */
  @Override
  public double getMutationProbability() {
    return mutationProbability;
  }

  /** Execute() method */
  @Override
  public CompositeSolution execute(CompositeSolution solution) {
    Check.notNull(solution);

    List<Solution<?>> mutatedSolutionComponents = new ArrayList<>();
    int numberOfSolutionsInCompositeSolution = solution.variables().size();
    for (int i = 0; i < numberOfSolutionsInCompositeSolution; i++) {
      mutatedSolutionComponents.add(operators.get(i).execute(solution.variables().get(i))) ;
    }

    return new CompositeSolution(mutatedSolutionComponents);
  }

  public List<MutationOperator<Solution<?>>> getOperators() {
    return operators ;
  }
}
