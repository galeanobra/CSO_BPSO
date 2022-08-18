package org.uma.jmetal.problem.multiobjective.UDN;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.problem.multiobjective.UDN.model.StaticUDN;
import org.uma.jmetal.problem.multiobjective.UDN.model.UDN;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.BTS;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.Cell;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.Sector;
import org.uma.jmetal.util.binarySet.BinarySet;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem ZDT1
 */
public class StaticCSO extends CSO {

    /**
     * Creates an instance of the Static CSO problem
     */
    public StaticCSO(String mainConfig, int run) {

        //Create the UDN model
        udn_ = new StaticUDN(mainConfig, run);

        bits = udn_.getTotalNumberOfActivableCells();

        setNumberOfVariables(1);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("StaticCSO");

        udn_.getTotalNumberOfActivableCells();

        run_ = run;

        //load operators config
        loadOperatorsConfig(udn_.getOperatorsFile());

//        udn_.getCellsOfInterestByPoint();
    }

    public StaticCSO(String problemconf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public StaticCSO(String mainconf, int run, int epochs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    /**
     * Evaluates a solution.
     *
     * @param solution The solution to evaluate.
     */
    @Override
    public BinarySolution evaluate(BinarySolution solution) {
        BinarySet cso = solution.variables().get(0);

        boolean noActiveCells = cso.cardinality() == 0;

        if (!noActiveCells) {
            //map the activation to the udn
            udn_.setCellActivation(cso);

            //update the avera
            udn_.computeSignaling();

            double capacity = networkCapacity(solution);
            double powerConsumption = powerConsumptionStatic();
            solution.objectives()[0] = powerConsumption;
            solution.objectives()[1] = -capacity;
//            System.out.println(powerConsumption + " " + capacity);
        } else {
            solution.objectives()[0] = 0.0;
            solution.objectives()[1] = 0.0;
        }

        return solution;
    } // evaluate

    /**
     * m
     * In this function operators are applied in order to improve the sinr of
     * certain problematic points in the network by switching off some BTS
     *
     * @param solution: Solution to be modified
     */
    @Override
    public void intelligentSwitchOff(BinarySolution solution) {

        if (this.operators_.containsKey("maintenancePowerOp"))
            maintenancePowerOp(this.operators_.get("maintenancePowerOp"), solution);

        if (this.operators_.containsKey("noUsersOp"))
            noUsersOp(this.operators_.get("noUsersOp"), solution);

        if (this.operators_.containsKey("priorizeSmallCellsOp"))
            priorizeSmallCellsOp(this.operators_.get("priorizeSmallCellsOp"), solution);

        if (this.operators_.containsKey("priorizeFemtoOp"))
            priorizeFemtoOp(this.operators_.get("priorizeFemtoOp"), solution);

    }

    /**
     * Calculates the power consumption taking into account the total traffic demand
     * and the maintenance power, in the case of small cells (pico, femto)
     *
     * @return Power consumption
     */
    double powerConsumptionStatic() {
        double sum = 0.0;
        boolean hasActiveCells;
        double maintenancePower = 2000; //mW

        for (List<BTS> btss : udn_.btss_.values()) {
            for (BTS bts : btss) {
                hasActiveCells = false;
                for (Sector sector : bts.getSectors()) {
                    for (Cell cell : sector.getCells()) {
                        if (cell.isActive()) {
                            hasActiveCells = true;
                            sum += sector.getTransmittedPower() * sector.getAlfa() + sector.getBeta() + sector.getDelta() * cell.getTrafficDemand() + 10;
                        } else {
                            //residual consuption in sleep mode (mW)
                            sum += sector.getTransmittedPower() * 0.01;
                        }
                    }
                }
                if (hasActiveCells) {
                    sum += maintenancePower;
                }
            }
        }

        //mW -> W -> kW -> MW
        sum /= 1000000000;

        return sum;
    }// powerConsumptionStatic

    public UDN getUDN() {
        return udn_;
    }

    @Override
    public List<Integer> getListOfBitsPerVariable() {
        return Arrays.asList(bits);
    }
} // Planning UDN
