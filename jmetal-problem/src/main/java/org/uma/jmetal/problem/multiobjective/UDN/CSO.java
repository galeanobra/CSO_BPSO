package org.uma.jmetal.problem.multiobjective.UDN;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.problem.multiobjective.UDN.model.Point;
import org.uma.jmetal.problem.multiobjective.UDN.model.UDN;
import org.uma.jmetal.problem.multiobjective.UDN.model.UDN.CellType;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.BTS;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.Cell;
import org.uma.jmetal.problem.multiobjective.UDN.model.cells.Sector;
import org.uma.jmetal.problem.multiobjective.UDN.model.users.User;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.uma.jmetal.problem.multiobjective.UDN.model.UDN.CellType.*;

/**
 * Class representing problem ZDT1
 */
public abstract class CSO extends AbstractBinaryProblem {

    /**
     * Number of activable cells
     */
    protected int bits;

    /**
     * The underlying UDN
     */
    protected UDN udn_;

    /**
     * Operator Configuration
     */
    protected Map<String, Double> operators_; //operators with their applications rate

    /**
     * The seed to generate the instance
     */
    protected int run_;

    public int getTotalNumberOfActivableCells() {
        return udn_.getTotalNumberOfActivableCells();
    }

    int pointsWithStatsComputed() {
        return udn_.pointsWithStatsComputed();
    }

    double powerConsumptionBasedOnTransmittedPower() {
        double sum = 0.0;

        for (List<Cell> cells : udn_.cells_.values()) {
            for (Cell c : cells) {
                if (c.isActive()) {
                    sum += 4.7 * c.getSector().getTransmittedPower();
                } else {
                    //residual consuption in sleep mode (mW)
                    sum += 160;
                }
            }
        }
        //mW -> W -> kW -> MW
        sum /= 1000000000;

        return sum;
    }

    /**
     * Calculates the power consumption taking into account the total traffic demand
     *
     * @return Power consumption
     */
    double powerConsumptionPiovesan() {
        double sum = 0.0;

        for (List<Cell> cells : udn_.cells_.values()) {
            for (Cell c : cells) {
                Sector sector = c.getSector();
                if (c.isActive()) {
                    sum += sector.getTransmittedPower() * sector.getAlfa() + sector.getBeta() + sector.getDelta() * c.getTrafficDemand() + 10;
                } else {
                    sum += sector.getTransmittedPower() * 0.01;
                }
            }
        }
        //mW -> W -> kW -> MW
        sum /= 1000000000;
        return sum;
    }


    /**
     * Max capacity of the 5G network. At each point, it returns the best SINR
     * for each of the different operating frequencies.
     *
     * @return Network capacity
     */
    double networkCapacity(BinarySolution solution) {
        /*
          For the dynamic problem addressing
         */
        List<Cell> assignment = new ArrayList<>();

        double capacity = 0.0;

        //0.- Reset number of users assigned to cells
        udn_.resetNumberOfUsersAssignedToCells();

        //1.- Assign users to cells, to compute the BW allocated to them
        for (User u : this.udn_.getUsers()) {
            u.setServingCell(udn_.getGridPoint(u.getX(), u.getY(), u.getZ()).getCellWithHigherSINR());
            u.getServingCell().addUserAssigned();

            //dynamic
            // assignment.add(u.getServingCell().getID());
            assignment.add(u.getServingCell());
        }

        //save the assignment into the solution
        solution.setUEsToCellAssignment(assignment);

        //1.- computes the Mbps allocated to each user
        for (User u : this.udn_.getUsers()) {
            double allocatedBW = u.getServingCell().getSharedBWForAssignedUsers();

            //computes the Mbps
            //double c = u.capacity(this.udn_, allocatedBW);
            double c = u.capacityMIMO(this.udn_, allocatedBW);
            capacity += c / 1000.0;
        }

        //udn_.validateUserAssigned();
        return capacity;
    }

    public int getRun() {
        return this.run_;
    }

    /**
     * In this function operators are applied in order to improve the sinr of
     * certain problematic points in the network by switching off some BTS
     */
    public void intelligentSwitchOff(BinarySolution solution) throws JMetalException {
        Map<Double, List<Point>> worsePoints = new HashMap<>();
        double sinr_limit = 12;

        for (double op_frequency : this.udn_.cells_.keySet()) {
            List<Point> l = new ArrayList<>();
            for (User u : this.udn_.getUsers()) {
                Cell c = u.getServingCell();
                double f = c.getBTS().getWorkingFrequency();
                if (Double.compare(f, op_frequency) == 0) {
                    Point p = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());
                    if (p.computeSINR(c) < sinr_limit) {
                        l.add(p);
                    }
                }
            }
            if (!l.isEmpty()) {
                l = sortList(l);
                worsePoints.put(op_frequency, l);
            }
        }

        modifySolution(solution);
    }

    /**
     * Sort a given list of Points by it SINR, being the worse the first
     *
     * @param l : list to sort
     * @return sorted list
     */
    public List<Point> sortList(List<Point> l) {
        double[] sinr_list = new double[l.size()];
        List<Point> sortedList = new ArrayList<>();
        double min_sinr = 5;

        for (int i = 0; i < l.size(); i++) {
            Point p = l.get(i);
            Cell c = p.getCellWithHigherSINR();
            double sinr = p.computeSINR(c);
            sinr_list[i] = sinr;

        }
        Arrays.sort(sinr_list);
        int index = 0;
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < l.size(); j++) {
                Point p_ = l.get(j);
                Cell c_ = p_.getCellWithHigherSINR();
                double sinr_ = p_.computeSINR(c_);
                if (Double.compare(sinr_, sinr_list[i]) == 0) {
                    index = j;
                    break;
                }
            }
            sortedList.add(i, l.get(index));
        }
        return sortedList;
    }

    /**
     * Cells with no users assigned are switched off.
     *
     * @param rate     : Application rate
     * @param solution The solution to be modified.
     */
    public void noUsersOp(double rate, BinarySolution solution) {
        if (new JavaRandomGenerator().nextDouble() < rate) {
            BinarySet cso = solution.variables().get(0);

            udn_.setCellActivation(cso);
            udn_.computeSignaling();
            udn_.resetNumberOfUsersAssignedToCells();

            if (udn_.getTotalNumberOfActiveCells() > 0) {

                //Assign users to cells, to compute the BW allocated to them
                for (User u : this.udn_.getUsers()) {
                    Point p = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());
                    Cell c = p.getCellWithHigherSINR();
                    c.addUserAssigned();
                    u.setServingCell(c);
                }

                for (double frequency : this.udn_.cells_.keySet()) {
                    for (Cell c : udn_.cells_.get(frequency)) {
                        if (c.getAssignedUsers() == 0)
                            c.setActivation(false);
                    }
                }

                modifySolution(solution);
            }
        }//if
    }

    /**
     * Switch on those femtocells that can serve UEs.
     *
     * @param solution Solution
     */
    public void priorizeFemtoOp(double rate, BinarySolution solution) {
        if (new JavaRandomGenerator().nextDouble() < rate) {
            BinarySet cso = solution.variables().get(0);

            //map the activation to the udn
            udn_.setCellActivation(cso);

            //recompute the signaling
            udn_.computeSignaling();

            //reset the UEs assigned to cells
            udn_.resetNumberOfUsersAssignedToCells();

            if (udn_.getTotalNumberOfActiveCells() > 0) {

                //Assign users to cells, to compute the BW allocated to them
                for (User u : this.udn_.getUsers()) {
                    Point p = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());

                    Cell c = p.getCellWithHigherSINR();

                    c.addUserAssigned();

                    u.setServingCell(c);
                }

                //Look for the candidate femtocells
                double threshold = 1;
                Cell alternative;
                Cell current;
                Point user_location;
                Map<Double, Cell> bestCells;

                for (User u : this.udn_.getUsers()) {
                    if ((u.getServingCell().getType() != FEMTO) || (u.getServingCell().getType() != PICO)) {
                        current = u.getServingCell();
                        user_location = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());
                        bestCells = user_location.getCellsWithBestSINRs();
                        for (Map.Entry<Double, Cell> actualEntry : bestCells.entrySet()) {
                            alternative = actualEntry.getValue();
                            if (user_location.computeSINR(alternative) > threshold) {
                                if ((alternative.getType() == FEMTO) || (alternative.getType() == PICO)) {
                                    u.setServingCell(alternative);
                                    alternative.addUserAssigned();
                                    current.removeUserAssigned();
                                    if (current.getAssignedUsers() == 0)
                                        current.setActivation(false);
                                    alternative.setActivation(true);
                                    break;
                                }
                            }
                        }
                    }//IF
                }//FOR

                //apply CSO -> switch off the remaining cells not serving any UE
                for (double frequency : this.udn_.cells_.keySet()) {
                    if (udn_.cells_.containsKey(frequency)) {
                        for (Cell c : udn_.cells_.get(frequency)) {
                            if (c.getAssignedUsers() == 0)
                                c.setActivation(false);
                        }
                    }
                }

                //Copy the modifications to the solution
                modifySolution(solution);
            }
        }
    }


    /**
     * Switch on those small cells (pico and femto) that can serve UEs.
     *
     * @param solution Solution
     */
    public void priorizeSmallCellsOp(double rate, BinarySolution solution) {
        if (new JavaRandomGenerator().nextDouble() < rate) {
            BinarySet cso = solution.variables().get(0);

            //map the activation to the udn
            udn_.setCellActivation(cso);

            //recompute the signaling
            udn_.computeSignaling();

            //reset the UEs assigned to cells
            udn_.resetNumberOfUsersAssignedToCells();

            if (udn_.getTotalNumberOfActiveCells() > 0) {

                //Assign users to cells, to compute the BW allocated to them
                for (User u : this.udn_.getUsers()) {
                    Point p = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());

                    Cell c = p.getCellWithHigherSINR();

                    c.addUserAssigned();

                    u.setServingCell(c);
                }

                //Look for the candidate femtocells
                double threshold = 1;
                Cell alternative;
                Cell current;
                Point user_location;
                Map<Double, Cell> bestCells;

                for (User u : this.udn_.getUsers()) {
                    if ((u.getServingCell().getType() != FEMTO) || (u.getServingCell().getType() != PICO)) {
                        current = u.getServingCell();
                        user_location = udn_.getGridPoint(u.getX(), u.getY(), u.getZ());
                        bestCells = user_location.getCellsWithBestSINRs();
                        for (Map.Entry<Double, Cell> actualEntry : bestCells.entrySet()) {
                            alternative = actualEntry.getValue();
                            if (user_location.computeSINR(alternative) > threshold) {
                                if ((alternative.getType() == FEMTO) || (alternative.getType() == PICO)) {
                                    u.setServingCell(alternative);
                                    alternative.addUserAssigned();
                                    current.removeUserAssigned();
                                    if (current.getAssignedUsers() == 0)
                                        current.setActivation(false);
                                    alternative.setActivation(true);
                                    break;
                                }
                            }
                        }
                    }//IF
                }//FOR

                //apply CSO -> switch off the remaining cells not serving any UE
                for (double frequency : this.udn_.cells_.keySet()) {
                    if (udn_.cells_.containsKey(frequency)) {
                        List<Cell> l = udn_.cells_.get(frequency);
                        for (Cell c : l) {
                            if (c.getAssignedUsers() == 0) {
                                c.setActivation(false);
                            }
                        }
                    }

                }

                //Copy the modifications to the solution
                modifySolution(solution);
            }
        }//if
    }


    /**
     * Turn off those BTSs that only have one active cell, saving the maintenance power
     *
     * @param rate     : application probability
     * @param solution : Solution to be modified by the operator
     */
    public void maintenancePowerOp(double rate, BinarySolution solution) {
        if (new JavaRandomGenerator().nextDouble() < rate) {
            BinarySet cso = solution.variables().get(0);

            udn_.setCellActivation(cso);
            udn_.computeSignaling();
            udn_.resetNumberOfUsersAssignedToCells();

            if (udn_.getTotalNumberOfActiveCells() > 0) {
                // v1
                for (List<BTS> btss : udn_.btss_.values()) {
                    for (BTS bts : btss) {
                        if (bts.getNumberOfActiveCells() == 1) {
                            //Turn off the active cell
                            for (Sector sector : bts.getSectors()) {
                                for (Cell cell : sector.getCells()) {
                                    if (cell.isActive()) {
                                        cell.setActivation(false);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                modifySolution(solution);
            }
        }//if
    }

    /**
     * Activates/deactivates BTSs in the solution according to the information
     * enclosed in the modified network of the problem
     *
     * @param solution Solution
     */
    public void modifySolution(BinarySolution solution) {
        BinarySet cso = solution.variables().get(0);
        int bts = 0;

        for (List<Cell> cells : udn_.cells_.values()) {
            for (Cell c : cells) {
                if (c.getType() != CellType.MACRO) {
                    cso.set(bts, c.isActive());
                    bts++;
                }
            }
        }
    }

    /**
     * Extract operators configuration from file
     *
     * @param configFile Config file
     */
    public void loadOperatorsConfig(String configFile) {
        //read operators configuration
        //double noUsersOp_rate, macro1Op_rate, macro2Op_rate, tooManyUsersOp_rate, priorizeFemtoOp_rate, maintenancePowerOp_rate;
        Properties pro = new Properties();
        this.operators_ = new HashMap<>();
        try {
            System.out.println("Loading operators configuration file...");
            pro.load(new FileInputStream(configFile));
            int numOperators = Integer.parseInt(pro.getProperty("numOperators", "2"));

            for (int i = 1; i <= numOperators; i++) {
                String operatorName = pro.getProperty("operator" + i, "unknownOp");
                double rate = Double.parseDouble(pro.getProperty("rate" + i, "0.0"));
                this.operators_.put(operatorName, rate);
            }

        } catch (IOException e) {
            System.out.println(e + "Error loading operators configuration: " + configFile);
            System.exit(-1);
        }


        System.out.println("OPERATORS APPLIED: ");
        for (String operator : this.operators_.keySet())
            System.out.println(operator + ", rate: " + this.operators_.get(operator).toString());
    }
} // CSO
