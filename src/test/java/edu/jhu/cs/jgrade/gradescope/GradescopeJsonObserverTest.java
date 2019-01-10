package edu.jhu.cs.jgrade.gradescope;

import edu.jhu.cs.jgrade.Grader;
import edu.jhu.cs.jgrade.gradedtest.GradedTestResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class GradescopeJsonObserverTest {

    private GradescopeJsonObserver unit;
    private Grader grader;

    @Before
    public void initUnit() {
        unit = new GradescopeJsonObserver();
        grader = new Grader();
    }

    private static void assertValidJson(String s) throws JSONException {
        JSONObject o = new JSONObject(s);
    }

    @Test(expected=GradescopeJsonException.class)
    public void invalidIfEmpty() {
        unit.update(grader);
        unit.getOutput();
    }

    @Test(expected=GradescopeJsonException.class)
    public void invalidIfNoTestsOrScore() {
        grader.setExecutionTime(45);
        unit.update(grader);
        unit.getOutput();
    }

    @Test
    public void validIfScoreSet() throws JSONException {
        grader.setScore(20.0);
        unit.update(grader);
        assertValidJson(unit.getOutput());
    }

    @Test
    public void validIfTests() throws JSONException {
        grader.addGradedTestResult(new GradedTestResult("", "", 20.0, "visible"));
        unit.update(grader);
        assertValidJson(unit.getOutput());
    }

    @Test(expected=GradescopeJsonException.class)
    public void catchesInvalidVisibility() {
        unit.setVisibility("invisible");
    }

    @Test(expected=GradescopeJsonException.class)
    public void catchesInvalidStdoutVisibility() {
        unit.setStdoutVisibility("invisible");
    }
}
