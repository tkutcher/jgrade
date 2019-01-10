package com.github.tkutcher.jgrade.gradescope;

import com.github.tkutcher.jgrade.Grader;
import com.github.tkutcher.jgrade.OutputObserver;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.AFTER_DUE_DATE;
import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.AFTER_PUBLISHED;
import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.HIDDEN;
import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.VISIBLE;


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

    private void assemble(Grader grader) {
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
        } catch (GradescopeJsonException e) {
            System.err.println(e.getMessage());
        }
    }

    private void validateGrader(Grader grader) {
        if (!(grader.hasScore() || grader.hasGradedTestResults())) {
            throw new GradescopeJsonException("Gradescope Json must have either tests or score set");
        } else if (this.hasVisibility() && !isValidVisibility(this.visibility)) {
            throw new GradescopeJsonException(this.visibility + " is not a valid visibility option");
        } else if (this.hasStdoutVisibility() && !isValidVisibility(this.stdoutVisibility)) {
            throw new GradescopeJsonException(this.stdoutVisibility + " is not a valid visibility option");
        }

        for (GradedTestResult r : grader.getGradedTestResults()) {
            if (!isValidVisibility(r.getVisibility())) {
                throw new GradescopeJsonException(r.getVisibility() + " is not a valid visibility option");
            }
        }
    }

    private static boolean isValidVisibility(String visibility) {
        return visibility.equals(VISIBLE) ||
                visibility.equals(HIDDEN) ||
                visibility.equals(AFTER_DUE_DATE) ||
                visibility.equals(AFTER_PUBLISHED);
    }

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
