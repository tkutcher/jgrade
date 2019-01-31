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
                double amountToDeduct = this.deduct(r);
                r.setScore(0 - amountToDeduct);
                this.deductedPoints += amountToDeduct;
            }
            r.setPoints(0);
        }
    }

    private double deduct(GradedTestResult r) {
        if ((this.deductedPoints + r.getPoints()) > potentialDeductions()) {
            r.addOutput("Failed test but deductive grading did not subtract"
                    + "points below floor");
            return potentialDeductions() - this.deductedPoints;
        } else {
            return r.getPoints();
        }
    }

    private double potentialDeductions() {
        return this.startingScore - this.floor;
    }
}
