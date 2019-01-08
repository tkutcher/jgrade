package com.github.tkutcher.autograder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static edu.jhu.cs226.instructor.autograder.base.Consts.GradescopeJson.*;

public class JsonAssembler {
    private JSONObject obj;

    public JsonAssembler() {
        this.obj = new JSONObject();
    }

    public JSONObject assemble(GradedTestResult r) {
         return new JSONObject()
                 .put(NAME, r.getName())
                 .put(SCORE, r.getScore())
                 .put(MAX_SCORE, r.getPoints())
                 .put(NUMBER, r.getNumber())
                 .put(OUTPUT, r.getOutput())
                 .put(VISIBILITY, r.getVisibility());
    }

    public JSONArray assemble(List<GradedTestResult> l) {
        JSONArray testResults = new JSONArray();
        for (GradedTestResult r : l) {
            testResults.put(assemble(r));
        }
        return testResults;
    }

    public void assemble(Grader g) {
        this.obj.put(STDOUT_VISIBILITY, HIDDEN)
                .put(EXECUTION_TIME, g.getExecutionTime())
                .put(TESTS, this.assemble(g.getGradedTestResults()));
    }

    public String toString(int indentationLevel) {
        return this.obj.toString(indentationLevel);
    }
}
