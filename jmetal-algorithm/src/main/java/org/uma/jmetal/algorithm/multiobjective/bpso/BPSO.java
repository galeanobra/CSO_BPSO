package org.uma.jmetal.algorithm.multiobjective.bpso;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.UDN.StaticCSO;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * This class implements a version of BPSO
 *
 * @author Juan Jesús Espinosa Martínez <jespinosv@alumnos.unex.es>
 */
public class BPSO extends AbstractParticleSwarmOptimization<BinarySolution, List<BinarySolution>> {

    private final Problem<BinarySolution> problem;
    private final double c1;
    private final double c2;
    private final double wMax;
    private final double wMin;
    private final double vMax;
    private final double vMin;

    private final int swarmSize;
    private final int maxEvaluations;
    private int evaluations;

    private final GenericSolutionAttribute<BinarySolution, BinarySolution> localBest;
    private final double[][] speed;

    private final JMetalRandom randomGenerator;
    private final Random random;

    private final BoundedArchive<BinarySolution> leaders;
    private final Comparator<BinarySolution> dominanceComparator;

    private final MutationOperator<BinarySolution> mutation;

//    private final double[] deltaMax;
//    private final double[] deltaMin;

    private final SolutionListEvaluator<BinarySolution> evaluator;

    /**
     * Constructor
     */
    public BPSO(Problem<BinarySolution> problem, int swarmSize, BoundedArchive<BinarySolution> leaders, MutationOperator<BinarySolution> mutationOperator, int maxEvaluations, SolutionListEvaluator<BinarySolution> evaluator, double c1, double c2, double wMax, double wMin, double vMax, double vMin) {
        this.problem = problem;
        this.swarmSize = swarmSize;
        this.leaders = leaders;
        this.mutation = mutationOperator;
        this.maxEvaluations = maxEvaluations;
        this.c1 = c1;
        this.c2 = c2;
        this.wMax = wMax;
        this.wMin = wMin;
        this.vMax = vMax;
        this.vMin = vMin;

        randomGenerator = JMetalRandom.getInstance();
        random = new Random();
        this.evaluator = evaluator;

        dominanceComparator = new DominanceComparator<BinarySolution>();
        localBest = new GenericSolutionAttribute<BinarySolution, BinarySolution>();
        speed = new double[swarmSize][problem.createSolution().getNumberOfBits(0)];
    }

    protected void updateLeadersDensityEstimator() {
        leaders.computeDensityEstimator();
    }

    @Override
    protected void initProgress() {
        evaluations = getSwarm().size();
        updateLeadersDensityEstimator();
    }

    @Override
    protected void updateProgress() {
        evaluations += getSwarm().size();
        updateLeadersDensityEstimator();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<BinarySolution> createInitialSwarm() {
        List<BinarySolution> swarm = new ArrayList<>(swarmSize);

        BinarySolution newSolution;
        for (int i = 0; i < swarmSize; i++) {
            newSolution = problem.createSolution();
            swarm.add(newSolution);
        }

        return swarm;
    }

    @Override
    protected List<BinarySolution> evaluateSwarm(List<BinarySolution> swarm) {
        for (BinarySolution s : swarm) {
            ((StaticCSO) problem).intelligentSwitchOff(s);
        }
        swarm = evaluator.evaluate(swarm, problem);

        return swarm;
    }

    @Override
    protected void initializeLeader(List<BinarySolution> swarm) {
        for (BinarySolution particle : swarm) {
            leaders.add(particle);
        }
    }

    @Override
    protected void initializeParticlesMemory(List<BinarySolution> swarm) {
        for (BinarySolution particle : swarm) {
            localBest.setAttribute(particle, (BinarySolution) particle.copy());
        }
    }

    @Override
    protected void initializeVelocity(List<BinarySolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            for (int j = 0; j < swarm.get(i).getNumberOfBits(0); j++) {
                speed[i][j] = 0.0;
            }
        }
    }

    @Override
    protected void updateVelocity(List<BinarySolution> swarm) {
        double r1, r2;
        BinarySolution bestGlobal;

        for (int i = 0; i < swarm.size(); i++) {
            BinarySolution particle = (BinarySolution) swarm.get(i).copy();
            BinarySolution bestParticle = (BinarySolution) localBest.getAttribute(swarm.get(i)).copy();

            BinarySet particleBSet = particle.variables().get(0);
            BinarySet bestParticleBSet = bestParticle.variables().get(0);

            bestGlobal = selectGlobalBest();
            BinarySet bestGlobalBSet = bestGlobal.variables().get(0);

            r1 = random.nextDouble();
            r2 = random.nextDouble();

            for (int j = 0; j < swarm.get(i).getNumberOfBits(0); j++) {
                speed[i][j] = inertiaWeightUpdate(this.evaluations, this.maxEvaluations, this.wMax, this.wMin) * speed[i][j] + c1 * r1 * ((bestParticleBSet.get(j) ? 1 : 0) - (particleBSet.get(j) ? 1 : 0)) + c2 * r2 * ((bestGlobalBSet.get(j) ? 1 : 0) - (particleBSet.get(j) ? 1 : 0));

                if (speed[i][j] > this.vMax) {
                    speed[i][j] = this.vMax;
                } else if (speed[i][j] < this.vMin) {
                    speed[i][j] = this.vMin;
                }

            }
        }
    }

    private double inertiaWeightUpdate(int iter, int maxIter, double wmax, double wmin) {
        return wmax - iter * ((wmax - wmin) / maxIter);
    }

    public double vShapedMapping(double v) {
        return java.lang.Math.abs(java.lang.Math.tanh(v));
    }

    @Override
    protected void updatePosition(List<BinarySolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            BinarySolution particle = swarm.get(i);
            BinarySet particleBSet = particle.variables().get(0);

            for (int j = 0; j < swarm.get(i).getNumberOfBits(0); j++) {
                particleBSet.set(j, !(vShapedMapping(speed[i][j]) < random.nextDouble()));
            }

        }
    }

    @Override
    protected void perturbation(List<BinarySolution> swarm) {
        for (BinarySolution binarySolution : swarm) {
            mutation.execute(binarySolution);
        }
    }

    @Override
    protected void updateLeaders(List<BinarySolution> swarm) {
        for (BinarySolution particle : swarm) {
            leaders.add((BinarySolution) particle.copy());
        }
    }

    @Override
    protected void updateParticlesMemory(List<BinarySolution> swarm) {
        for (BinarySolution binarySolution : swarm) {
            int flag = dominanceComparator.compare(binarySolution, localBest.getAttribute(binarySolution));
            if (flag <= 0) {
                BinarySolution particle = (BinarySolution) binarySolution.copy();
                localBest.setAttribute(binarySolution, particle);
            }
        }
    }

    @Override
    public List<BinarySolution> getResult() {
        return getSwarm();
    }

    @Override
    public String getName() {
        return "BPSO";
    }

    @Override
    public String getDescription() {
        return "Binary PSO";
    }

    protected BinarySolution selectGlobalBest() {
        BinarySolution one;
        BinarySolution two;
        BinarySolution bestGlobal;
        int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
        int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
        one = leaders.getSolutionList().get(pos1);
        two = leaders.getSolutionList().get(pos2);

        if (leaders.getComparator().compare(one, two) <= 0) {
            bestGlobal = (BinarySolution) one.copy();
        } else {
            bestGlobal = (BinarySolution) two.copy();
        }

        return bestGlobal;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public int getSwarmSize() {
        return this.swarmSize;
    }
}
