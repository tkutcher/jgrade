package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.GradedTestListener;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import org.junit.runner.JUnitCore;

import java.util.ArrayList;
import java.util.List;

public class Grader {
    private static final int NOT_SET = -1;

    private List<OutputObserver> observers;

    private List<GradedTestResult> gradedTestResults;
    private long startTime;
    private long executionTime;
    private Double score;
    private Double maxScore;
    private StringBuilder output;
    private String visibility;
    private String stdoutVisibility;

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
    public boolean hasVisibility() { return this.visibility != null; }
    public boolean hasStdoutVisibility() { return this.stdoutVisibility != null; }

    public void setScore(double score) { this.score = score; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    public void addGradedTestResult(GradedTestResult result) { gradedTestResults.add(result); }
    public void addOutput(String output) { this.output.append(output); }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public void setStdoutVisibility(String stdoutVisibility) { this.stdoutVisibility = stdoutVisibility; }

    public double getScore() { return this.score; }
    public double getMaxScore() { return this.maxScore; }
    public long getExecutionTime() { return this.executionTime; }
    public List<GradedTestResult> getGradedTestResults() { return gradedTestResults; }
    public String getOutput() { return this.output.toString(); }
    public String getVisibility() { return this.visibility; }
    public String getStdoutVisibility() { return this.stdoutVisibility; }

    // </editor-fold>

    public void attachOutputObserver(OutputObserver o) {
        this.observers.add(o);
    }

    public void notifyOutputObservers() {
        for (OutputObserver o : this.observers) {
            o.update();
        }
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        if (this.startTime == 0)
            throw new RuntimeException("cannot stop timer not started");
        this.setExecutionTime(this.getExecutionTime() + System.currentTimeMillis() - this.startTime);
        this.startTime = 0;
    }

    public void runJUnitGradedTests(Class testSuite) {
        GradedTestListener listener = new GradedTestListener();
        JUnitCore runner = new JUnitCore();
        runner.addListener(listener);
        runner.run(testSuite);
        this.gradedTestResults.addAll(listener.getGradedTestResults());
    }
}
