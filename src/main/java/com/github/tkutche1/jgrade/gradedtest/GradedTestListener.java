package com.github.tkutche1.jgrade.gradedtest;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A class that extends a JUnit {@link RunListener} to check for unit test
 * methods annotated with the {@link GradedTest} annotation. It builds up a
 * list of {@link GradedTestResult}s, one for each method with the annotation.
 * Captures anything printed to standard out during the test run and adds it
 * to the output of the {@link GradedTestResult}.
 * <p>
 *     Note: Not thread-safe. In order to set the score of a result it relies
 *     on the assumption that the test that just finished was the one that
 *     also most recently started (for annotated methods).
 * </p>
 */
public class GradedTestListener extends RunListener {

    private List<GradedTestResult> gradedTestResults;
    private int numFailedGradedTests;
    private ByteArrayOutputStream testOutput;
    private PrintStream originalOutStream;
    private GradedTestResult currentGradedTestResult;

    /**
     * Constructor for a new listener. Initializes a list of
     * {@link GradedTestResult}s and remembers the original
     * <code>System.out</code> to restore it.
     */
    public GradedTestListener() {
        this.gradedTestResults = new ArrayList<>();
        this.numFailedGradedTests = 0;
        this.testOutput = new ByteArrayOutputStream();
        this.originalOutStream = System.out;
    }

    // <editor-fold "desc="accessors">

    /**
     * Get the count of graded tests for this listener.
     * @return The number of graded tests.
     */
    public int getNumGradedTests() {
        return this.gradedTestResults.size();
    }

    /**
     * Get the list of {@link GradedTestResult}.
     * @return The list of {@link GradedTestResult}.
     */
    public List<GradedTestResult> getGradedTestResults() {
        return this.gradedTestResults;
    }

    /**
     * Get the number of failed graded tests.
     * @return The number of graded tests that failed.
     */
    public int getNumFailedGradedTests() {
        return numFailedGradedTests;
    }

    // </editor-fold>


    /* NOTE - inherit testRunStarted() and testRunFinished() if any features
     *   require something to do before/after a run. 1.0.0 has no need.
     *
     * FIXME - is it possible to only create the GradedTestResult after the
     *   test has run? Problem is testRunFinished called regardless of success.
     */

    /**
     * {@inheritDoc}
     *
     * Calls super method and then looks for a {@link GradedTest} annotation.
     * If one exists, it creates a {@link GradedTestResult} and sets its score
     * to the number of points. If the test fails the score would be set to 0.
     * Finally, it sets <code>System.out</code> to be its own
     * <code>PrintStream</code> to capture output.
     */
    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);

        this.currentGradedTestResult = null;

        GradedTest gradedTestAnnotation = description.getAnnotation(GradedTest.class);
        if (gradedTestAnnotation != null) {
            this.currentGradedTestResult =  new GradedTestResult(
                    gradedTestAnnotation.name(),
                    gradedTestAnnotation.number(),
                    gradedTestAnnotation.points(),
                    gradedTestAnnotation.visibility()
            );

            this.currentGradedTestResult.setScore(gradedTestAnnotation.points());
        }

        this.testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.testOutput));
    }

    /**
     * {@inheritDoc}
     *
     * Calls super method, adds the {@link GradedTestResult} if it exists, and
     * restores the original <code>PrintStream</code>.
     */
    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);

        if (this.currentGradedTestResult != null) {
            this.currentGradedTestResult.addOutput(testOutput.toString());
            this.gradedTestResults.add(this.currentGradedTestResult);
        }

        this.currentGradedTestResult = null;
        System.setOut(originalOutStream);
    }

    /**
     * {@inheritDoc}
     *
     * Calls super method and if the test was annotated with
     * {@link GradedTest} then sets the {@link GradedTestResult} score to 0
     * and adds the failure message to the output.
     */
    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        if (this.currentGradedTestResult != null) {
            this.currentGradedTestResult.setScore(0);
            this.currentGradedTestResult.addOutput("FAILED:\n");
            if (failure.getMessage() != null) {
                this.currentGradedTestResult.addOutput(failure.getMessage());
            } else {
                // This way no testing information is leaked - if you want to give students
                // feedback on why they failed use a message.
                this.currentGradedTestResult.addOutput("No description provided");
            }
            this.currentGradedTestResult.addOutput("\n");
            numFailedGradedTests++;
            this.currentGradedTestResult.setPassed(false);
        }
    }

}
