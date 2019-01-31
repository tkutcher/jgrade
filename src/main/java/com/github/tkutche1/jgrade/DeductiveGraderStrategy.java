package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;

import java.util.List;

/**
 * Strategy to grade deductively. This strategy will take a point value,
 * all tests will be worth 0, and failed tests will count negative.
 */
public class DeductiveGraderStrategy implements GraderStrategy {

    private double floor;
    private double startingScore;
    private double deductedPoints;

    /**
     * Create a new DeductiveGradingStrategy.
     * @param startingScore The score to deduct from.
     */
    public DeductiveGraderStrategy(double startingScore) {
        this.startingScore = startingScore;
        this.floor = 0;
        this.deductedPoints = 0;
    }

    /**
     * Set the floor to deduct to.
     * @param floor The floor to deduct to.
     */
    public void setFloor(double floor) {
        this.floor = floor;
    }

    /**
     * Get the amount of points deducted from running this.
     * @return The amount of points deducted.
     */
    public double getDeductedPoints() {
        return this.deductedPoints;
    }

    @Override
    public void grade(List<GradedTestResult> l) {
        for (GradedTestResult r : l) {
            if (!r.passed()) {
                this.deductedPoints += this.deduct(r);
            }
            r.setPoints(0);
        }
    }

    // Deduct from r, return the amount deducted
    private double deduct(GradedTestResult r) {
        double amountToDeduct = r.getPoints();
        if ((this.deductedPoints + r.getPoints()) > potentialDeductions()) {
            r.addOutput("Failed test but deductive grading did not subtract"
                    + "points below floor");
            amountToDeduct = potentialDeductions() - this.deductedPoints;
        }
        r.setScore(0 - amountToDeduct);
        return amountToDeduct;
    }

    // Get the amount of possible points that can be deducted in total.
    private double potentialDeductions() {
        return this.startingScore - this.floor;
    }
}
