package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the SMPSO algorithm described in:
 * SMPSO: A new PSO-based metaheuristic for multi-objective optimization
 * MCDM 2009. DOI: http://dx.doi.org/10.1109/MCDM.2009.4938830
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {
    private final DoubleProblem problem;

    private final double c1Max;
    private final double c1Min;
    private final double c2Max;
    private final double c2Min;
    private final double r1Max;
    private final double r1Min;
    private final double r2Max;
    private final double r2Min;
    private final double weightMax;
    private final double weightMin;
    private final double changeVelocity1;
    private final double changeVelocity2;

    private final int swarmSize;
    private final int maxIterations;
    private int iterations;

    private final GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
    private final double[][] speed;

    private final JMetalRandom randomGenerator;

    private final BoundedArchive<DoubleSolution> leaders;
    private final Comparator<DoubleSolution> dominanceComparator;

    private final MutationOperator<DoubleSolution> mutation;

    private final double[] deltaMax;
    private final double[] deltaMin;

    private final SolutionListEvaluator<DoubleSolution> evaluator;

    /**
     * Constructor
     */
    public SMPSO(DoubleProblem problem, int swarmSize, BoundedArchive<DoubleSolution> leaders,
                 MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max,
                 double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                 double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
               SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.leaders = leaders;
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;

    this.r1Max = r1Max;
    this.r1Min = r1Min;
    this.r2Max = r2Max;
    this.r2Min = r2Min;
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;

    randomGenerator = JMetalRandom.getInstance();
    this.evaluator = evaluator;

    dominanceComparator = new DominanceComparator<DoubleSolution>();
    localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>();
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      Bounds<Double> bounds = problem.getBoundsForVariables().get(i) ;
      deltaMax[i] = (bounds.getUpperBound() - bounds.getLowerBound()) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }
  }

  protected void updateLeadersDensityEstimator() {
    leaders.computeDensityEstimator();
  }

  @Override
  protected void initProgress() {
    iterations = 1;
    updateLeadersDensityEstimator();
  }

  @Override
  protected void updateProgress() {
    iterations += 1;
    updateLeadersDensityEstimator();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override
  protected void initializeLeader(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      leaders.add(particle);
    }
  }

  @Override
  protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override
  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override
  protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    double wmax, wmin;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest.getAttribute(swarm.get(i)).copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.variables().size(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) * (
                        inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var] +
                                c1 * r1 * (bestParticle.variables().get(var) - particle.variables().get(var)) +
                                c2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var))),
                deltaMax, deltaMin, var);
      }
    }
  }

  @Override
  protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.variables().size(); j++) {
        particle.variables().set(j, particle.variables().get(j) + speed[i][j]);

        Bounds<Double> bounds = problem.getBoundsForVariables().get(j) ;
        Double lowerBound = bounds.getLowerBound() ;
        Double upperBound = bounds.getUpperBound() ;
        if (particle.variables().get(j) < lowerBound) {
          particle.variables().set(j, lowerBound);
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.variables().get(j) > upperBound) {
          particle.variables().set(j, upperBound);
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  @Override
  protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override
  protected void updateLeaders(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      leaders.add((DoubleSolution) particle.copy());
    }
  }

  @Override
  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return leaders.getSolutionList();
  }

  protected DoubleSolution selectGlobalBest() {
    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    one = leaders.getSolutionList().get(pos1);
    two = leaders.getSolutionList().get(pos2);

    if (leaders.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  private double velocityConstriction(double v, double[] deltaMax, double[] deltaMin,
                                      int variableIndex) {
    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  protected double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma;
  }

  @Override
  public String getName() {
    return "SMPSO";
  }

  @Override
  public String getDescription() {
    return "Speed contrained Multiobjective PSO";
  }

  /* Getters */
  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public int getIterations() {
    return iterations;
  }

  /* Setters */
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
}
