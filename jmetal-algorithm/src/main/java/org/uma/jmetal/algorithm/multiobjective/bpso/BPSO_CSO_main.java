package org.uma.jmetal.algorithm.multiobjective.bpso;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.HybridNSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.TwoPointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.multiobjective.UDN.StaticCSO;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class BPSO_CSO_main extends AbstractAlgorithmRunner {
    public static void main(String[] args) throws JMetalException, FileNotFoundException {
        Problem<BinarySolution> problem;
        Algorithm<List<BinarySolution>> algorithm;
        MutationOperator<BinarySolution> mutation;

        int swarmSize = Integer.parseInt(args[0]);    // Population size
        int numEvals = Integer.parseInt(args[1]);   // Stopping condition
        int run = Integer.parseInt(args[2]);        // Seed selection
        int taskID = Integer.parseInt(args[3]);     // Task ID (for filename)
        int jobID = Integer.parseInt(args[4]);      // Job ID (for filename)
        String name = args[5];                      // Name (for output directory)
        String main = args[6];                      // Main configuration file
        boolean display = false;                    // Shows dynamic chart

        if (args.length == 8) display = Boolean.parseBoolean(args[7]);

        double c1 = 2.00F;
        double c2 = 2.00F;
        double wMax = 0.90F;
        double wMin = 0.40F;
        double vMax = 4;
        double vMin = 0.00F;
        problem = new StaticCSO(main, run);

        BoundedArchive<BinarySolution> archive = new CrowdingDistanceArchive<>(100);

        double mutationProbability = 1.0 / ((StaticCSO) problem).getTotalNumberOfActivableCells();

        mutation = new BitFlipMutation(mutationProbability);

        algorithm = new BPSOBuilder(problem, archive).setMutation(mutation).setMaxIterations(numEvals).setSwarmSize(swarmSize).setSolutionListEvaluator(new SequentialSolutionListEvaluator<BinarySolution>()).setVariant(BPSOBuilder.BPSOVariant.Measures).setC1(c1).setC2(c2).setWMax(wMax).setWMin(wMin).setvMax(vMax).setvMin(vMin).build();

        // Display code
        if (display) {
            MeasureManager measureManager = ((BPSOMeasures) algorithm).getMeasureManager();
            BasicMeasure<List<BinarySolution>> solutionListMeasure = (BasicMeasure<List<BinarySolution>>) measureManager.<List<BinarySolution>>getPushMeasure("currentPopulation");
            CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

            ChartContainer chart = new ChartContainer(algorithm.getName(), 100);
            chart.setFrontChart(0, 1, "resources/referenceFrontsCSV/0.csv");
            chart.initChart();

            solutionListMeasure.register(new BPSO_CSO_main.ChartListener(chart));
            iterationMeasure.register(new BPSO_CSO_main.IterationListener(chart));
        }


        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        List<BinarySolution> population = algorithm.getResult();

        String FUN = name + ".FUN." + taskID + "." + jobID;
        String VAR = name + ".VAR." + taskID + "." + jobID;

        new SolutionListOutput(population).setVarFileOutputContext(new DefaultFileOutputContext(VAR, " ")).setFunFileOutputContext(new DefaultFileOutputContext(FUN, " ")).print();

        System.out.println("Total execution time: " + algorithmRunner.getComputingTime() + "ms");
        System.out.println("Objectives values have been written to file " + FUN);
        System.out.println("Variables values have been written to file " + VAR);
    }

    private static class ChartListener implements MeasureListener<List<BinarySolution>> {
        private final ChartContainer chart;
        private int iteration = 0;

        public ChartListener(ChartContainer chart) {
            this.chart = chart;
            this.chart.getFrontChart().setTitle("Evaluation: " + this.iteration);
        }

        private void refreshChart(List solutionList) {
            if (this.chart != null) {
                iteration++;
                this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
                this.chart.updateFrontCharts(solutionList);
                this.chart.refreshCharts();
            }
        }

        @Override
        synchronized public void measureGenerated(List solutions) {
            refreshChart(solutions);
        }
    }

    private static class IterationListener implements MeasureListener<Long> {
        ChartContainer chart;

        public IterationListener(ChartContainer chart) {
            this.chart = chart;
            this.chart.getFrontChart().setTitle("Iteration: " + 0);
        }

        @Override
        synchronized public void measureGenerated(Long iteration) {
            if (this.chart != null) {
                this.chart.getFrontChart().setTitle("Iteration: " + iteration);
            }
        }
    }
}