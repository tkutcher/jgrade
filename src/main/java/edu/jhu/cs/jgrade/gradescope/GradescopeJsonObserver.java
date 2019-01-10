package edu.jhu.cs.jgrade.gradescope;

import edu.jhu.cs.jgrade.Grader;
import edu.jhu.cs.jgrade.OutputObserver;
import edu.jhu.cs.jgrade.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.AFTER_DUE_DATE;
import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.AFTER_PUBLISHED;
import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.HIDDEN;
import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.VISIBLE;


public class GradescopeJsonObserver implements OutputObserver {

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

    public GradescopeJsonObserver() {
        this.json = new JSONObject();
        this.prettyPrint = 0;
    }

    private boolean hasVisibility() { return this.visibility != null; }
    private boolean hasStdoutVisibility() { return this.stdoutVisibility != null; }

    public void setVisibility(String visibility) {
        if (!isValidVisibility(visibility)) {
            throw new GradescopeJsonException(visibility + " is not a valid visibility");
        }
        this.visibility = visibility;
    }

    public void setStdoutVisibility(String visibility) {
        if (!isValidVisibility(visibility)) {
            throw new GradescopeJsonException(visibility + " is not a valid visibility");
        }
        this.stdoutVisibility = visibility;
    }

    public void setPrettyPrint(int prettyPrint) { this.prettyPrint = prettyPrint; }

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

    public String getOutput() {
        return this.toString();
    }

    @Override
    public String toString() {
        try {
            return this.prettyPrint > 0 ? this.json.toString(this.prettyPrint) : this.json.toString();
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
