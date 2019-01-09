package com.github.tkutcher.autograder;

import com.github.tkutcher.autograder.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.github.tkutcher.autograder.gradedtest.Consts.GradescopeJson.*;


public class GradescopeJsonObserver implements OutputObserver {
    private JSONObject json;
    private Grader grader;

    public GradescopeJsonObserver(Grader grader) {
        this.grader = grader;
        this.json = new JSONObject();
    }

    public void update() {
        this.json = new JSONObject();
        this.assemble();
    }

    public JSONObject assemble(GradedTestResult r) {
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

    public JSONArray assemble(List<GradedTestResult> l) {
        JSONArray testResults = new JSONArray();
        for (GradedTestResult r : l) {
            testResults.put(assemble(r));
        }
        return testResults;
    }

    public void assemble() {
        try {
            this.json.put(STDOUT_VISIBILITY, HIDDEN)
                    .put(EXECUTION_TIME, this.grader.getExecutionTime())
                    .put(TESTS, this.assemble(this.grader.getGradedTestResults()));
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }

    public String toString(int indentationLevel) {
        try {
            return this.json.toString(indentationLevel);
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
