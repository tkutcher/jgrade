package com.github.tkutcher.autograder;

import com.github.tkutcher.autograder.gradedtest.GradedTestListener;
import com.github.tkutcher.autograder.gradedtest.GradedTestResult;
import org.junit.runner.JUnitCore;

import java.util.ArrayList;
import java.util.List;

public class Grader {

    private List<OutputObserver> observers;

    private List<GradedTestResult> gradedTestResults;
    private long executionTime;
    private long startTime;

    public Grader() {
        this.observers = new ArrayList<>();
        this.gradedTestResults = new ArrayList<>();
        this.executionTime = 0;
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        if (this.startTime == 0)
            throw new RuntimeException("cannot stop timer not started");
        this.executionTime += System.currentTimeMillis() - this.startTime;
        this.startTime = 0;
    }

    public void addGradedTestResult(GradedTestResult result) {
        gradedTestResults.add(result);
    }

    public List<GradedTestResult> getGradedTestResults() {
        return gradedTestResults;
    }

    public long getExecutionTime() { return this.executionTime; }



    public void runJUnitGradedTests(Class testSuite) {
        GradedTestListener listener = new GradedTestListener();
        JUnitCore runner = new JUnitCore();
        runner.addListener(listener);
        runner.run(testSuite);
        this.gradedTestResults.addAll(listener.getGradedTestResults());
    }

    public void attachOutputObserver(OutputObserver o) {
        this.observers.add(o);
    }

    public void notifyOutputObservers() {
        for (OutputObserver o : this.observers) {
            o.update();
        }
    }

    public String toJson(int indentationLevel) {
        GradescopeJsonObserver assembler = new GradescopeJsonObserver();
        assembler.assemble(this);
        return assembler.toString(indentationLevel);
    }

    public String toJson() {
        return this.toJson(0);
    }

    // Static Global stuff

}
