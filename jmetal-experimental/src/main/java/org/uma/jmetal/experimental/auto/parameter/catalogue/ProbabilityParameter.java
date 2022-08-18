package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.RealParameter;

public class ProbabilityParameter extends RealParameter {
    public ProbabilityParameter(String name, String[] args) {
        super(name, args, 0.0, 1.0);
    }
}
