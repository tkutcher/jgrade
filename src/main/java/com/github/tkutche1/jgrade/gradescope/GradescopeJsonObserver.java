package com.github.tkutche1.jgrade.gradescope;

import com.github.tkutche1.jgrade.Grader;
import com.github.tkutche1.jgrade.GraderObserver;
import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.AFTER_DUE_DATE;
import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.AFTER_PUBLISHED;
import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.HIDDEN;
import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.VISIBLE;


/**
 * A concrete observer for a {@link Grader} where the output it produces
 * is the JSON a Gradescope Autograder can work with. The Grader does not
 * notify on its own after changes, so if not using the main JGrade method
 * then be sure to call {@link Grader#notifyOutputObservers()} to see
 * updates.
 */
public class GradescopeJsonObserver implements GraderObserver {

    private static final String EXECUTION_TIME = "execution_time";
    private static final String STDOUT_VISIBILITY = "stdout_visibility";
    private static final String TESTS = "tests";
    private static final String SCORE = "score";
    private static final String MAX_SCORE = "max_score";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String OUTPUT = "output";
    private static final String VISIBILITY = "visibility";

    private JSONObject json;
    private int prettyPrint;
    private String visibility;
    private String stdoutVisibility;

    /**
     * Creates an instance of the observer. By default the pretty-print
     * option is off (the integer is negative).
     */
    public GradescopeJsonObserver() {
        this.json = new JSONObject();
        this.prettyPrint = -1;
    }

    private boolean hasVisibility() { return this.visibility != null; }
    private boolean hasStdoutVisibility() { return this.stdoutVisibility != null; }

    // <editor-fold "desc="accessors">

    /**
     * Sets the visibility for all of the test cases.
     * @param visibility The top-level visibility to use for all test cases.
     * @throws GradescopeJsonException If visibility not valid.
     */
    public void setVisibility(String visibility) throws GradescopeJsonException {
        if (!isValidVisibility(visibility)) {
            throw new GradescopeJsonException(visibility + " is not a valid visibility");
        }
        this.visibility = visibility;
    }

    /**
     * Sets the visibility for standard out during the run.
     * @param visibility The visibility to set for standard out.
     * @throws GradescopeJsonException If visibility is not valid.
     */
    public void setStdoutVisibility(String visibility) throws GradescopeJsonException {
        if (!isValidVisibility(visibility)) {
            throw new GradescopeJsonException(visibility + " is not a valid visibility");
        }
        this.stdoutVisibility = visibility;
    }

    /**
     * Sets the pretty-print for the JSON to output. The integer is how many
     * spaces to add for each indent level. A negative integer corresponds to
     * disabling pretty-print. If non-negative, simply calls
     * {@link JSONObject#toString(int)}
     * @param prettyPrint The integer for how much to indent
     */
    public void setPrettyPrint(int prettyPrint) { this.prettyPrint = prettyPrint; }

    // </editor-fold>


    @Override
    public void update(Grader grader) {
        this.json = new JSONObject();
        this.assemble(grader);
    }

    private JSONObject assemble(GradedTestResult r) {
        try {
            return new JSONObject()
                    .put(NAME, r.getName())
                    .put(SCORE, r.getScore())
                    .put(MAX_SCORE, r.getPoints())
                    .put(NUMBER, r.getNumber())
                    .put(OUTPUT, r.getOutput())
                    .put(VISIBILITY, r.getVisibility());
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }

    private JSONArray assemble(List<GradedTestResult> l) {
        JSONArray testResults = new JSONArray();
        for (GradedTestResult r : l) {
            testResults.put(assemble(r));
        }
        return testResults;
    }

    private void assemble(Grader grader) throws GradescopeJsonException {
        try {
            validateGrader(grader);
            if (grader.hasScore())
                this.json.put(SCORE, grader.getScore());
            if (grader.hasMaxScore())
                this.json.put(MAX_SCORE, grader.getMaxScore());
            if (grader.hasExecutionTime())
                this.json.put(EXECUTION_TIME, grader.getExecutionTime());
            if (grader.hasOutput())
                this.json.put(OUTPUT, grader.getOutput());
            if (this.hasVisibility())
                this.json.put(VISIBILITY, this.visibility);
            if (this.hasStdoutVisibility())
                this.json.put(STDOUT_VISIBILITY, this.stdoutVisibility);
            if (grader.hasGradedTestResults())
                this.json.put(TESTS, this.assemble(grader.getGradedTestResults()));
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }

    private void validateGrader(Grader grader) {
        if (!(grader.hasScore() || grader.hasGradedTestResults())) {
            throw new GradescopeJsonException("Gradescope Json must have either tests or score set");
        }

        /* The following checks ~should~ all pass because they would have been checked when set. */
        assert isValidVisibility(this.visibility);
        assert isValidVisibility(this.stdoutVisibility);
        assert allValidVisibility(grader.getGradedTestResults());
    }

    private static boolean allValidVisibility(List<GradedTestResult> results) {
        for (GradedTestResult r : results) {
            if (!isValidVisibility(r.getVisibility())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidVisibility(String visibility) {
        return visibility == null ||   // Just wasn't set, which is OK
                visibility.equals(VISIBLE) ||
                visibility.equals(HIDDEN) ||
                visibility.equals(AFTER_DUE_DATE) ||
                visibility.equals(AFTER_PUBLISHED);
    }

    // TODO - Enforce that if score and tests both present that they match?

    /**
     * Get the observers output of the JSON String.
     * <p>
     *     Note: just calls <code>toString()</code>
     * </p>
     * @return The JSON String for the observed Grader.
     */
    public String getOutput() {
        return this.toString();
    }

    @Override
    public String toString() {
        try {
            return this.prettyPrint >= 0 ? this.json.toString(this.prettyPrint) : this.json.toString();
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
