package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

import java.io.IOException;

public class MainSetCoverage {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("SetCoverage::Main: Usage: java MainSetCoverage <FrontFile> <ReferenceFrontFile>");
            System.exit(1);
        }

        double[][] front = new double[0][];
        double[][] referenceFront = new double[0][];

        try {
            front = VectorUtils.readVectors(args[0]);
            referenceFront = VectorUtils.readVectors(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println(new SetCoverage(referenceFront).compute(front));
    }
}
