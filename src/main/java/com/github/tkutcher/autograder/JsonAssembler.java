package com.github.tkutcher.autograder;

import com.github.tkutcher.autograder.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.github.tkutcher.autograder.gradedtest.Consts.GradescopeJson.*;


public class JsonAssembler implements OutputObserver {
    private JSONObject obj;

    public JsonAssembler() {
        this.obj = new JSONObject();
    }

    public void update() {

    }

    public String getOutput() {
        return "";
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

    public void assemble(Grader g) {
        try {
            this.obj.put(STDOUT_VISIBILITY, HIDDEN)
                    .put(EXECUTION_TIME, g.getExecutionTime())
                    .put(TESTS, this.assemble(g.getGradedTestResults()));
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }

    public String toString(int indentationLevel) {
        try {
            return this.obj.toString(indentationLevel);
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
