package com.github.tkutcher.autograder.gradedtest;


import static com.github.tkutcher.autograder.gradedtest.Consts.GradescopeJson.*;

public class GradedTestResult {
    private String name;
    private String number;
    private double points;
    private String visibility;

    private double score;
    private StringBuilder output;

    public GradedTestResult(String name, String number, double points, String visibility)
            throws IllegalArgumentException {
        this.name = name;
        this.number = number;
        this.points = points;

        if (!(visibility.equals(HIDDEN) || visibility.equals(VISIBLE) ||
                visibility.equals(AFTER_DUE_DATE) || visibility.equals(AFTER_PUBLISHED)))
            throw new IllegalArgumentException("visibility should be one of 'hidden', 'visible', " +
                    "'after_due_date', or 'after_published'");

        this.visibility = visibility;
        this.score = 0;
        this.output = new StringBuilder();
    }

    public void addOutput(String s) {
        this.output.append(s);
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public double getPoints() {
        return points;
    }

    public String getVisibility() {
        return visibility;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) throws InternalError {
        if (score > this.points)
            throw new InternalError("Cannot set score above max number of points");
        this.score = score;
    }

    public String getOutput() {
        return this.output.toString();
    }
}
