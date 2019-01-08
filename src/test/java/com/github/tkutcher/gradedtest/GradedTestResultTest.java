package com.github.tkutcher.gradedtest;

import com.github.tkutcher.autograder.gradedtest.GradedTestResult;
import org.junit.Before;
import org.junit.Test;

import javax.naming.SizeLimitExceededException;

import static com.github.tkutcher.autograder.gradedtest.Consts.*;
import static org.junit.Assert.assertEquals;


public class GradedTestResultTest {

    private GradedTestResult unit;

    @Before
    public void initUnit() {
        this.unit = new GradedTestResult(
                DEFAULT_NAME,
                DEFAULT_NUMBER,
                DEFAULT_POINTS,
                DEFAULT_VISIBILITY
        );
    }

    @Test
    public void hasCorrectParams() {
        assertEquals(DEFAULT_NAME, unit.getName());
        assertEquals(DEFAULT_NUMBER, unit.getNumber());
        assertEquals(DEFAULT_POINTS, unit.getPoints(), 0.0);
        assertEquals(DEFAULT_VISIBILITY, unit.getVisibility());
    }

    @Test
    public void startsWithNoOutput() {
        assertEquals("", unit.getOutput());
    }

    @Test
    public void startsWithNoScore() {
        assertEquals(0, unit.getScore(), 0.0);
    }

    @Test
    public void canAddOutput() {
        String s1 = "Ex@mpl3";
        String s2 = "OuTPUt";
        assertEquals("", unit.getOutput());
        unit.addOutput(s1);
        assertEquals(s1, unit.getOutput());
        unit.addOutput(s2);
        assertEquals(s1 + s2, unit.getOutput());
    }

    @Test
    public void canAddScore() throws SizeLimitExceededException {
        double score1 = 0.5;
        double score2 = 0.75;
        assertEquals(0, unit.getScore(), 0.0);
        unit.setScore(score1);
        assertEquals(score1, unit.getScore(), 0.0);
        unit.setScore(score2);
        assertEquals(score2, unit.getScore(), 0.0);
    }

    @Test(expected=InternalError.class)
    public void cannotAddScoreGreaterThanPoints() {
        unit.setScore(15.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void visibilityMustBeValid() {
        new GradedTestResult(DEFAULT_NAME, DEFAULT_NUMBER, DEFAULT_POINTS, "INVALID");
    }
}
