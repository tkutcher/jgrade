package com.github.tkutcher.gradedtest;

import com.github.tkutcher.jgrade.gradedtest.GradedTest;
import com.github.tkutcher.jgrade.gradedtest.GradedTestListener;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.util.List;

import static com.github.tkutcher.jgrade.gradedtest.Consts.*;
import static com.github.tkutcher.jgrade.gradedtest.Consts.GradescopeJson.HIDDEN;
import static org.junit.Assert.*;


public class GradedTestListenerTest {

    private static final String EXAMPLE_STRING = "t3ST StR1nG";
    private static final String EXAMPLE_MESSAGE = "FAILURE";
    private static final String EXAMPLE_NAME = "Graded Test";
    private static final String EXAMPLE_NUMBER = "Example Number";
    private static final double EXAMPLE_POINTS = 2.0;

    private GradedTestListener listener;

    @Before
    public void initUnit() {
        this.listener = new GradedTestListener();
    }

    private void runWithListenerForExample(Class exampleUnitTests) {
        JUnitCore runner = new JUnitCore();
        runner.addListener(this.listener);
        runner.run(exampleUnitTests);
    }

    private GradedTestResult getOnlyGradedTestResult(Class exampleUnitTests) {
        runWithListenerForExample(exampleUnitTests);
        List<GradedTestResult> results = this.listener.getGradedTestResults();
        assertEquals(1, results.size());
        return results.get(0);
    }

    @Test
    public void newListenerNoTests() {
        assertEquals(0, listener.getNumGradedTests());
    }

    @Test
    public void basicCountGradedTests() {
        runWithListenerForExample(BasicGradedTests.class);
        assertEquals(2, listener.getNumGradedTests());
    }

    @Test
    public void onlyCountsGradedTests() {
        runWithListenerForExample(NotAllTestsGraded.class);
        assertEquals(1, listener.getNumGradedTests());
    }

    @Test
    public void addsDefaultGradedTestResult() {
        GradedTestResult result = getOnlyGradedTestResult(SingleDefaultGradedTest.class);
        assertEquals(DEFAULT_NAME, result.getName());
        assertEquals(DEFAULT_NUMBER, result.getNumber());
        assertEquals(DEFAULT_POINTS, result.getPoints(), 0.0);
        assertEquals(DEFAULT_VISIBILITY, result.getVisibility());
    }

    @Test
    public void addsCustomGradedTestResult() {
        GradedTestResult result = getOnlyGradedTestResult(SingleCustomGradedTest.class);
        assertEquals(EXAMPLE_NAME, result.getName());
        assertEquals(EXAMPLE_NUMBER, result.getNumber());
        assertEquals(EXAMPLE_POINTS, result.getPoints(), 0.0);
        assertEquals(HIDDEN, result.getVisibility());
    }

    @Test
    public void correctlyCountsFailedGradedTest() {
        GradedTestResult result = getOnlyGradedTestResult(SingleFailedGradedTest.class);
        assertEquals(1, listener.getNumFailedGradedTests());
        assertEquals(0.0, result.getScore(), 0.0);
    }

    @Test
    public void setsScoreToZeroForFailedTest() {
        GradedTestResult result = getOnlyGradedTestResult(SingleFailedGradedTest.class);
        assertEquals(0.0, result.getScore(), 0.0);
    }

    @Test
    public void setsScoreToMaxForPassedTest() {
        GradedTestResult result = getOnlyGradedTestResult(SingleCustomGradedTest.class);
        assertEquals(EXAMPLE_POINTS, result.getScore(), 0.0);
    }

    @Test
    public void onlyCountsFailedIfGradedTest() {
        runWithListenerForExample(MultipleFailOneGradedTest.class);
        assertEquals(1, listener.getNumFailedGradedTests());
    }

    @Test
    public void capturesOutputFromTest() {
        GradedTestResult result = getOnlyGradedTestResult(TestWithOutput.class);
        assertEquals(EXAMPLE_STRING, result.getOutput());
    }

    @Test
    public void outputHasFailMessage() {
        GradedTestResult result = getOnlyGradedTestResult(SingleFailWithMessageGradedTest.class);
        assertEquals(EXAMPLE_MESSAGE, result.getOutput());
    }

    @Test
    public void addsRegularFailureToStringIfNoMessage() {
        GradedTestResult result = getOnlyGradedTestResult(SingleFailedGradedTest.class);
        assertNotEquals("", result.getOutput());  // FIXME ?
    }

    /* * HELPER EXAMPLE "UNIT TEST" CLASSES * */

    public static class BasicGradedTests {
        @Test
        @GradedTest(name=EXAMPLE_NAME, points=2.0)
        public void trueIsTrue() { assertTrue(true); }

        @Test
        @GradedTest(name=EXAMPLE_NAME, points=2.0)
        public void falseIsFalse() { assertFalse(false); }
    }

    public static class NotAllTestsGraded {
        @Test
        @GradedTest(name=EXAMPLE_NAME, points=2.0)
        public void trueIsTrue() { assertTrue(true); }

        @Test
        public void falseIsFalse() { assertFalse(false); }
    }

    public static class SingleDefaultGradedTest {
        @Test
        @GradedTest public void gradedTest() { assertTrue(true); }
    }

    public static class SingleCustomGradedTest {
        @Test
        @GradedTest(
                name=EXAMPLE_NAME,
                number=EXAMPLE_NUMBER,
                points=EXAMPLE_POINTS,
                visibility=HIDDEN)
        public void gradedTest() { assertTrue(true); }
    }

    public static class SingleFailedGradedTest {
        @Test
        @GradedTest public void gradedTest() { fail(); }
    }

    public static class SingleFailWithMessageGradedTest {
        @Test
        @GradedTest public void gradedTest() { fail(EXAMPLE_MESSAGE); }
    }

    public static class MultipleFailOneGradedTest {
        @Test
        public void nonGradedTest() { fail(); }
        @Test
        @GradedTest public void gradedTest() { fail(); }
    }

    public static class TestWithOutput {
        @Test
        @GradedTest public void gradedTest() { System.out.print(EXAMPLE_STRING); }
    }
}
