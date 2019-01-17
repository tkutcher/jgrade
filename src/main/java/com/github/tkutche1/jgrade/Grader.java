package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.GradedTestListener;
import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;
import org.junit.runner.JUnitCore;

import java.util.ArrayList;
import java.util.List;


/**
 * A class to encompass all of the options for grading tests. Bundles together
 * {@link GradedTestResult}s, a total score, a total max score, any output
 * relevant to the entire submission, and a timer feature for timing execution
 * time. Is Observable to {@link GraderObserver}s.
 *
 * <p>
 *     Has undocumented accessors for score, max score, execution time,
 *     graded test results, and output.
 * </p>
 */
public class Grader {
    private static final int NOT_SET = -1;

    private List<GraderObserver> observers;
    private List<GradedTestResult> gradedTestResults;
    private long startTime;
    private long executionTime;
    private Double score;
    private Double maxScore;
    private StringBuilder output;

    /** Create a new Grader. */
    public Grader() {
        this.observers = new ArrayList<>();
        this.gradedTestResults = new ArrayList<>();
        this.executionTime = NOT_SET;
        this.output = new StringBuilder();
    }

    // <editor-fold desc="accessors">

    public boolean hasScore() { return this.score != null; }
    public boolean hasMaxScore() { return this.maxScore != null; }
    public boolean hasExecutionTime() { return this.executionTime != NOT_SET; }
    public boolean hasGradedTestResults() { return this.gradedTestResults.size() > 0; }
    public boolean hasOutput() { return this.output.length() > 0; }

    public void setScore(double score) { this.score = score; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    public void addGradedTestResult(GradedTestResult result) { gradedTestResults.add(result); }
    public void addOutput(String output) { this.output.append(output); }

    public double getScore() { return this.score; }
    public double getMaxScore() { return this.maxScore; }
    public long getExecutionTime() { return this.executionTime; }
    public List<GradedTestResult> getGradedTestResults() { return gradedTestResults; }
    public String getOutput() { return this.output.toString(); }

    // </editor-fold>

    /**
     * Attach a new {@link GraderObserver} to this Grader. When
     * {@link Grader#notifyOutputObservers()} is called all
     * {@link GraderObserver}s are updated.
     * @param o The observer to add.
     */
    public void attachOutputObserver(GraderObserver o) {
        this.observers.add(o);
    }

    /** Notify all observers that are observing this grader of a change. */
    public void notifyOutputObservers() {
        for (GraderObserver o : this.observers) {
            o.update(this);
        }
    }

    /** Starts (or resumes) the timer for the Grader. */
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Stops the timer for the Grader. Can start again after stopping.
     * @throws RuntimeException If the timer has not been started.
     */
    public void stopTimer() throws RuntimeException {
        if (this.startTime == 0)
            throw new RuntimeException("cannot stop timer not started");
        this.setExecutionTime(this.getExecutionTime() + System.currentTimeMillis() - this.startTime);
        this.startTime = 0;
    }

    /**
     * Runs JUnit tests and attaches a {@link GradedTestListener} to listen
     * for all {@link com.github.tkutche1.jgrade.gradedtest.GradedTest}s and add the
     * created {@link GradedTestResult}s. If class <code>MyTests</code> has
     * graded test JUnit test methods, then call this method with
     * <code>MyTests.class</code>. Similarly can use JUnit's
     * {@link org.junit.runners.Suite}.
     * @param testSuite The class containing the tests.
     */
    public void runJUnitGradedTests(Class testSuite) {
        GradedTestListener listener = new GradedTestListener();
        JUnitCore runner = new JUnitCore();
        runner.addListener(listener);
        runner.run(testSuite);
        this.gradedTestResults.addAll(listener.getGradedTestResults());
    }
}