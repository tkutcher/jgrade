package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraderTest {

    private Grader unit;

    @BeforeEach
    public void initUnit() {
        unit = new Grader();
    }

    @Test
    public void newGraderHasNothing() {
        assertFalse(unit.hasOutput());
        assertFalse(unit.hasGradedTestResults());
        assertFalse(unit.hasScore());
        assertFalse(unit.hasExecutionTime());
        assertFalse(unit.hasMaxScore());
    }

    @Test
    public void canSetAndAccessBasicMembers() {
        unit.setMaxScore(25.0);
        assertTrue(unit.hasMaxScore());
        assertEquals(25.0, unit.getMaxScore(), 0.0);

        unit.setScore(20.0);
        assertTrue(unit.hasScore());
        assertEquals(20.0, unit.getScore(), 0.0);

        unit.setExecutionTime(5000);
        assertTrue(unit.hasExecutionTime());
        assertEquals(5000, unit.getExecutionTime());
    }

    @Test
    public void canAddOutput() {
        unit.addOutput("test");
        assertTrue(unit.hasOutput());
        assertEquals("test", unit.getOutput());
        unit.addOutput(" blah");
        assertEquals("test blah", unit.getOutput());
    }

    @Test
    public void canAddGradedTestResults() {
        GradedTestResult t = new GradedTestResult("", "", 0.0, "visible");
        unit.addGradedTestResult(t);
        assertTrue(unit.hasGradedTestResults());
        List<GradedTestResult> results = unit.getGradedTestResults();
        assertEquals(1, results.size());
        assertEquals(t, results.get(0));
    }

    @Test
    public void canTimeOnce() {
        unit.startTimer();
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 400);
        unit.stopTimer();

        assertTrue(unit.hasExecutionTime());
        // FIXME - Not sure how deterministic, but hopefully ok? Using big delta
        assertEquals((double) 400, (double) unit.getExecutionTime(), 10.0);
    }

    @Test
    public void canContinueTimer() {
        unit.startTimer();
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 400);
        unit.stopTimer();

        long atPause = unit.getExecutionTime();

        unit.startTimer();
        startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 300);
        unit.stopTimer();

        assertTrue(unit.getExecutionTime() > atPause);
    }

    @Test
    public void cannotStopTimerIfNotStarted() {
        assertThrows(IllegalStateException.class, () -> {
            unit.stopTimer();

        });
    }

}
