package org.uma.jmetal.algorithm.multiobjective.bpso;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.measure.Measurable;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;

import java.util.List;

/**
 * This class implements a version of BPSO using measures
 *
 * @author Juan Jesús Espinosa Martínez <jespinosv@alumnos.unex.es>
 */
@SuppressWarnings("serial")
public class BPSOMeasures extends BPSO implements Measurable {
    protected CountingMeasure evaluations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;

    protected BasicMeasure<List<BinarySolution>> solutionListMeasure;

    /**
     * Constructor
     *
     * @param problem
     * @param swarmSize
     * @param leaders
     * @param mutationOperator
     * @param maxIterations
     * @param evaluator
     */
    public BPSOMeasures(Problem<BinarySolution> problem, int swarmSize, BoundedArchive<BinarySolution> leaders, MutationOperator<BinarySolution> mutationOperator, int maxIterations, SolutionListEvaluator<BinarySolution> evaluator, double c1, double c2, double wMax, double wMin, double vMax, double vMin) {
        super(problem, swarmSize, leaders, mutationOperator, maxIterations, evaluator, c1, c2, wMax, wMin, vMax, vMin);

        initMeasures();
    }

    @Override
    public void run() {
        durationMeasure.reset();
        durationMeasure.start();
        super.run();
        durationMeasure.stop();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations.get() >= getMaxEvaluations();
    }

    @Override
    protected void initProgress() {
        evaluations.reset(getSwarm().size());
        updateLeadersDensityEstimator();
    }

    @Override
    protected void updateProgress() {
        evaluations.increment(getSwarm().size());
        updateLeadersDensityEstimator();

        solutionListMeasure.push(super.getResult());
    }

    @Override
    public MeasureManager getMeasureManager() {
        return measureManager;
    }

    /* Measures code */
    private void initMeasures() {
        durationMeasure = new DurationMeasure();
        evaluations = new CountingMeasure(0);
        solutionListMeasure = new BasicMeasure<>();

        measureManager = new SimpleMeasureManager();
        measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
        measureManager.setPullMeasure("currentIteration", evaluations);

        measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
        measureManager.setPushMeasure("currentEvaluation", evaluations);
    }

    @Override
    public String getName() {
        return "BPSOMeasures";
    }

    @Override
    public String getDescription() {
        return "BPSO. Version using measures";
    }
}
