package com.github.tkutche1.jgrade.gradedtest;

/**
 * A class for the data that models a graded test. Primarily based on the
 * data needed for Gradescope's Autograder JSON. When creating
 * {@link GradedTestResult}s and working with visibility, use the public
 * constants {@link GradedTestResult#VISIBLE}, {@link GradedTestResult#HIDDEN},
 * etc.
 */
public class GradedTestResult {

    // <editor-fold "desc="visibility options">

    /** Visible to the student always. */
    public static final String VISIBLE = "visible";

    /** Never visible to the student. */
    public static final String HIDDEN = "hidden";

    /** Visible to the student only after the due date. */
    public static final String AFTER_DUE_DATE = "after_due_date";

    /** Visible to the student only after grades have been released. */
    public static final String AFTER_PUBLISHED = "after_published";

    // </editor-fold>

    // GradedTest annotation defaults
    static final String DEFAULT_NAME = "Unnamed Test";
    static final String DEFAULT_NUMBER = "";
    static final double DEFAULT_POINTS = 1.0;
    static final String DEFAULT_VISIBILITY = VISIBLE;

    private String name;
    private String number;
    private double points;
    private String visibility;

    private double score;
    private StringBuilder output;
    private boolean passed;

    /**
     * Create a new GradedTestResult, setting the initial score to 0.
     * @param name The name/description of the test.
     * @param number The identifier for the question number.
     * @param points The number of points the test is worth.
     * @param visibility The visibility setting of the test.
     * @throws IllegalArgumentException If the visibility is not valid.
     */
    public GradedTestResult(String name, String number, double points, String visibility)
            throws IllegalArgumentException {
        this.name = name;
        this.number = number;
        this.points = points;

        if (!(visibility.equals(HIDDEN) || visibility.equals(VISIBLE)
                || visibility.equals(AFTER_DUE_DATE) || visibility.equals(AFTER_PUBLISHED))) {
            throw new IllegalArgumentException("visibility should be one of 'hidden', 'visible', "
                    + "'after_due_date', or 'after_published'");
        }

        this.visibility = visibility;
        this.score = 0;
        this.output = new StringBuilder();
        this.passed = true;
    }

    // <editor-fold "desc="accessors">

    /**
     * Add output to the test result.
     * @param s String to append to the output.
     */
    public void addOutput(String s) {
        this.output.append(s);
    }

    /**
     * Set the score for the test.
     * @param score The score to set.
     * @throws RuntimeException if the score exceeds the points for the test.
     */
    public void setScore(double score) {
        if (score > this.points) {
            throw new RuntimeException("Cannot set score above max number of points");
        }
        this.score = score;
    }

    /**
     * Set the number of points the test is worth.
     * @param points The number of points to set.
     */
    public void setPoints(double points) {
        this.points = points;
    }

    /**
     * Set whether or not this result passed.
     * @param passed The value to set.
     */
    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    /**
     * Get the name of the test.
     * @return The name of the test.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of the test.
     * @return The number of the test.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Get the points the test is worth.
     * @return The number of points the test is worth.
     */
    public double getPoints() {
        return points;
    }

    /**
     * Get the visibility setting of the test.
     * @return The visibility setting of the test.
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Get the score of the test.
     * @return The score of the test.
     */
    public double getScore() {
        return score;
    }

    /**
     * Get the output of the test.
     * @return The output of the test.
     */
    public String getOutput() {
        return this.output.toString();
    }


    /**
     * Determine if the test for this result was considered to have passed
     * or not.
     * @return True if the test passed (student got full credit), false
     *         otherwise.
     */
    public boolean passed() {
        return this.passed;
    }

    // <editor-fold "desc="accessors">
}
