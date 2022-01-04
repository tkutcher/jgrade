package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.HIDDEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeductiveGraderStrategyTest {

    private static final double STARTING_SCORE = 14;
    private static final double FLOOR = 2;

    private DeductiveGraderStrategy unit;

    @BeforeEach
    public void initUnit() {
        this.unit = new DeductiveGraderStrategy(STARTING_SCORE, "blah");
    }

    private static GradedTestResult failedGradedTestResult(double points) {
        GradedTestResult r = new GradedTestResult("", "", points, HIDDEN);
        r.setPassed(false);
        return r;
    }

    private static GradedTestResult successfulGradedTestResult(double points) {
        GradedTestResult r = new GradedTestResult("", "", points, HIDDEN);
        r.setPassed(true);
        r.setScore(points);
        return r;
    }

    @Test
    public void noDeductions() {
        List<GradedTestResult> l = new ArrayList<>();
        l.add(successfulGradedTestResult(2.0));
        l.add(successfulGradedTestResult(2.0));
        l.add(successfulGradedTestResult(2.0));
        l.add(successfulGradedTestResult(2.0));

        this.unit.grade(l);

        for (GradedTestResult r : l.subList(0, l.size() - 1)) {
            assertEquals(0, r.getPoints(), 0.0);
            assertEquals(0, r.getScore(), 0.0);
        }

        assertEquals(0, unit.getDeductedPoints(), 0.0);
        assertEquals(STARTING_SCORE, l.get(l.size() - 1).getScore(), 0.0);
    }

    @Test
    public void deductToZero() {
        List<GradedTestResult> l = new ArrayList<>();
        l.add(failedGradedTestResult(STARTING_SCORE));

        this.unit.grade(l);

        for (GradedTestResult r : l.subList(0, l.size() - 1)) {
            assertEquals(0, r.getPoints(), 0.0);
            assertEquals(0 - STARTING_SCORE, r.getScore(), 0.0);
        }

        assertEquals(STARTING_SCORE, unit.getDeductedPoints(), 0.0);
        assertEquals(STARTING_SCORE, l.get(l.size() - 1).getScore(), 0.0);
    }

    @Test
    public void deductExactlyToFloor() {
        List<GradedTestResult> l = new ArrayList<>();
        l.add(failedGradedTestResult(STARTING_SCORE - 2));
        l.add(failedGradedTestResult(3));

        this.unit.grade(l);

        assertEquals(0 - STARTING_SCORE + 2, l.get(0).getScore(), 0.0);
        assertEquals(-2, l.get(1).getScore(), 0.0);
        assertEquals(STARTING_SCORE, unit.getDeductedPoints(), 0.0);
        assertEquals(STARTING_SCORE, l.get(l.size() - 1).getScore(), 0.0);
    }

    @Test
    public void deductExactlyToFloorOneTest() {
        List<GradedTestResult> l = new ArrayList<>();
        l.add(failedGradedTestResult(STARTING_SCORE + 2));

        this.unit.grade(l);

        assertEquals(0 - STARTING_SCORE, l.get(0).getScore(), 0.0);
        assertEquals(STARTING_SCORE, unit.getDeductedPoints(), 0.0);
        assertEquals(STARTING_SCORE, l.get(l.size() - 1).getScore(), 0.0);
    }

    @Test
    public void setDifferentFloor() {
        this.unit.setFloor(FLOOR);

        List<GradedTestResult> l = new ArrayList<>();
        l.add(failedGradedTestResult(STARTING_SCORE));

        this.unit.grade(l);

        assertEquals(0 - STARTING_SCORE + FLOOR, l.get(0).getScore(), 0.0);
        assertEquals(STARTING_SCORE - FLOOR, unit.getDeductedPoints(), 0.0);
        assertEquals(STARTING_SCORE, l.get(l.size() - 1).getScore(), 0.0);
    }
}
