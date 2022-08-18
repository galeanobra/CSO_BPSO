package org.uma.jmetal.parallel.synchronous;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.List;

/**
 * Class implementing an {@link Evaluation} based on Apache Spark.
 * Reference: C. Barba-González, J. García-Nieto, Antonio J. Nebro, J.F.Aldana-Montes: Multi-objective Big
 * Data Optimization with jMetal and Spark. EMO 2017". DOI: http://dx.doi.org/10.1007/978-3-319-54157-0_2
 *
 * @param <S>
 * @author Antonio J. Nebro
 */
public class SparkEvaluation<S extends Solution<?>> implements Evaluation<S>, Serializable {
    private final int numberOfComputedEvaluations;
    private final Problem<S> problem;
    static private JavaSparkContext sparkContext;

    public SparkEvaluation(JavaSparkContext sparkContext, Problem<S> problem) {
        this.numberOfComputedEvaluations = 0;
        this.problem = problem;
        SparkEvaluation.sparkContext = sparkContext;
    }

    @Override
    public List<S> evaluate(List<S> solutionList) {
    JavaRDD<S> solutionsToEvaluate = sparkContext.parallelize(solutionList);
    JavaRDD<S> evaluatedSolutions = solutionsToEvaluate.map(problem::evaluate);

    return evaluatedSolutions.collect() ;
  }

  public int getComputedEvaluations() {
    return numberOfComputedEvaluations ;
  }
}
