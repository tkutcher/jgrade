package com.github.tkutcher.autograder.gradedtest;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

// NOTE - Not Thread-Safe

public class GradedTestListener extends RunListener {

    // Instance Variables
    private List<GradedTestResult> gradedTestResults;
    private int numFailedGradedTests;
    private ByteArrayOutputStream testOutput;
    private PrintStream originalOutStream;
    private GradedTestResult currentGradedTestResult;

    public GradedTestListener() {
        this.gradedTestResults = new ArrayList<>();
        this.numFailedGradedTests = 0;
        this.testOutput = new ByteArrayOutputStream();
        this.originalOutStream = System.out;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
        // TODO - Anything?
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
        // TODO - Anything?
    }

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
            // Default start with points, set to 0 if failed.
            this.currentGradedTestResult.setScore(gradedTestAnnotation.points());
        }

        this.testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.testOutput));
    }

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

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        if (this.currentGradedTestResult != null) {
            this.currentGradedTestResult.setScore(0);
            this.currentGradedTestResult.addOutput(failure.getMessage() == null ?
                    failure.toString() : failure.getMessage());
            numFailedGradedTests++;
        }
    }

    public int getNumGradedTests() {
        return this.gradedTestResults.size();
    }

    public List<GradedTestResult> getGradedTestResults() {
        return this.gradedTestResults;
    }

    public GradedTestResult getCurrentGradedTestResult() {
        return this.currentGradedTestResult;
    }


    public int getNumFailedGradedTests() {
        return numFailedGradedTests;
    }
}
