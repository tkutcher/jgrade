package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class GradescopeJsonObserver implements OutputObserver {

    private static final String EXECUTION_TIME = "execution_time";
    private static final String STDOUT_VISIBILITY = "stdout_visibility";
    private static final String LEADERBOARD = "leaderboard";
    private static final String TESTS = "tests";
    private static final String SCORE = "score";
    private static final String MAX_SCORE = "max_score";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String OUTPUT = "output";
    private static final String TAGS = "tags";
    private static final String EXTRA_DATA = "extra_data";
    private static final String VISIBILITY = "visibility";

    private JSONObject json;
    private Grader grader;



    private int prettyPrint;

    public GradescopeJsonObserver(Grader grader) {
        this.grader = grader;
        this.json = new JSONObject();
        this.prettyPrint = 0;
    }

    public void setPrettyPrint(int prettyPrint) {
        this.prettyPrint = prettyPrint;
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
            this.json.put(STDOUT_VISIBILITY, GradedTestResult.HIDDEN)
                    .put(EXECUTION_TIME, this.grader.getExecutionTime())
                    .put(TESTS, this.assemble(this.grader.getGradedTestResults()));
        } catch (JSONException e) {
            throw new InternalError(e);
        }
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
