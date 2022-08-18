package org.uma.jmetal.algorithm.multiobjective.bpso;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasures;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * @author Juan Jesús Espinosa Martínez <jespinosv@alumnos.unex.es>
 */
public class BPSOBuilder implements AlgorithmBuilder<BPSO> {
    public enum BPSOVariant {BPSO, Measures}

    private final Problem<BinarySolution> problem;

    private double c1;
    private double c2;
    private double wMax;
    private double wMin;
    private double vMax;
    private double vMin;

    private int swarmSize;
    private int maxIterations;

    protected int archiveSize;

    protected MutationOperator<BinarySolution> mutationOperator;

    protected BoundedArchive<BinarySolution> leaders;

    protected SolutionListEvaluator<BinarySolution> evaluator;

    protected BPSOVariant variant;

    public BPSOBuilder(Problem<BinarySolution> problem, BoundedArchive<BinarySolution> leaders) {
        this.problem = problem;
        this.leaders = leaders;

        swarmSize = 100;
        maxIterations = 250;

        mutationOperator = new BitFlipMutation(1.0 / problem.getNumberOfVariables());
        evaluator = new SequentialSolutionListEvaluator<BinarySolution>();

        this.variant = BPSOVariant.BPSO;

    }

    /* Getters */
    public int getSwarmSize() {
        return swarmSize;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public MutationOperator<BinarySolution> getMutation() {
        return mutationOperator;
    }

    /* Setters */
    public BPSOBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;

        return this;
    }

    public BPSOBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;

        return this;
    }

    public BPSOBuilder setMutation(MutationOperator<BinarySolution> mutation) {
        mutationOperator = mutation;

        return this;
    }

    public BPSOBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
        JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

        return this;
    }

    public BPSOBuilder setSolutionListEvaluator(SolutionListEvaluator<BinarySolution> evaluator) {
        this.evaluator = evaluator;

        return this;
    }

    public BPSOBuilder setVariant(BPSOVariant variant) {
        this.variant = variant;

        return this;
    }

    public BPSOBuilder setC1(double c1) {
        this.c1 = c1;

        return this;
    }

    public BPSOBuilder setC2(double c2) {
        this.c2 = c2;

        return this;
    }

    public BPSOBuilder setWMax(double wMax) {
        this.wMax = wMax;

        return this;
    }

    public BPSOBuilder setWMin(double wMin) {
        this.wMin = wMin;

        return this;
    }

    public BPSOBuilder setvMax(double vMax) {
        this.vMax = vMax;

        return this;
    }

    public BPSOBuilder setvMin(double vMin) {
        this.vMin = vMin;

        return this;
    }

    public BPSO build() {
        if (this.variant.equals(BPSOVariant.Measures)) {
            return new BPSOMeasures(problem, swarmSize, leaders, mutationOperator, maxIterations, evaluator, c1, c2, wMax, wMin, vMax, vMin);
        } else {
            return new BPSO(problem, swarmSize, leaders, mutationOperator, maxIterations, evaluator, c1, c2, wMax, wMin, vMax, vMin);
        }
    }

    /*
     * Getters
     */
    public Problem<BinarySolution> getProblem() {
        return problem;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public MutationOperator<BinarySolution> getMutationOperator() {
        return mutationOperator;
    }

    public BoundedArchive<BinarySolution> getLeaders() {
        return leaders;
    }

    public SolutionListEvaluator<BinarySolution> getEvaluator() {
        return evaluator;
    }

    public double getC1() {
        return c1;
    }

    public double getC2() {
        return c2;
    }
}



