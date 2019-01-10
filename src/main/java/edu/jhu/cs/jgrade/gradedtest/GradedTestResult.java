package edu.jhu.cs.jgrade.gradedtest;


public class GradedTestResult {
    // Visibility options
    public static final String VISIBLE = "visible";
    public static final String HIDDEN = "hidden";
    public static final String AFTER_DUE_DATE = "after_due_date";
    public static final String AFTER_PUBLISHED = "after_published";

    // GradedTest annotation defaults
    public static final String DEFAULT_NAME = "Unnamed Test";
    public static final String DEFAULT_NUMBER = "";
    public static final double DEFAULT_POINTS = 1.0;
    public static final String DEFAULT_VISIBILITY = VISIBLE;

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
