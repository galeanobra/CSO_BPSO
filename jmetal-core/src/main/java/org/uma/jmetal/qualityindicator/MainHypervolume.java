package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.IOException;

public class MainHypervolume {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Hypervolume::Main: Usage: java MainHypervolume <FrontFile> <TrueFrontFile>");
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

        double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
        double[][] normalizedFront = NormalizeUtils.normalize(
                front,
                NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));


        // TODO borrar

        BufferedWriter bufferedWriter = new DefaultFileOutputContext("referencia").getFileWriter();

        try {
            if (normalizedReferenceFront.length > 0) {
                int numberOfObjectives = 2;
                for (double[] doubles : normalizedReferenceFront) {
                    for (int j = 0; j < numberOfObjectives; j++) {
                        bufferedWriter.write(doubles[j] + " ");
                    }

                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
        } catch (IOException e) {
            throw new JMetalException("Error printing objectives to file: ", e);
        }

        bufferedWriter = new DefaultFileOutputContext("frente").getFileWriter();

        try {
            if (normalizedFront.length > 0) {
                int numberOfObjectives = 2;
                for (double[] doubles : normalizedFront) {
                    for (int j = 0; j < numberOfObjectives; j++) {
                        bufferedWriter.write(doubles[j] + " ");
                    }

                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
        } catch (IOException e) {
            throw new JMetalException("Error printing objectives to file: ", e);
        }

        // TODO borrar

        System.out.println(new PISAHypervolume(normalizedReferenceFront).compute(normalizedFront));
    }
}
