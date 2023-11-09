package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import com.github.tkutcher.jgrade.gradedtest.Visibility;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JGradeCommandLineTest {

    private PrintStream origOut;
    private PrintStream origErr;
    private ByteArrayOutputStream captureOut;
    private ByteArrayOutputStream captureErr;

    @Before
    public void captureOutput() {
        origOut = System.out;
        origErr = System.err;
        captureOut = new ByteArrayOutputStream();
        captureErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captureOut));
        System.setErr(new PrintStream(captureErr));
    }

    @After
    public void resetOutput() { ;
        System.setOut(origOut);
        System.setErr(origErr);
    }

    @Test(expected=RuntimeException.class)
    public void unknownFlagExits() {
        JGrade.main(new String[] {"-blah"});
    }

    @Test(expected=RuntimeException.class)
    public void requiresClassFlag() {
        JGrade.main(new String[] {"-f", "json"});
    }

    @Test
    public void acceptsFoundClass() {
        JGrade.main(new String[] {"-c", this.getClass().getCanonicalName()});
    }

    @Test
    public void acceptsMultipleFlags() {
        JGrade.main(new String[] {"-f", "json", "-c", this.getClass().getCanonicalName()});
    }

    @Test
    public void acceptsLongOption() {
        JGrade.main(new String[] {"--format", "json", "-c", this.getClass().getCanonicalName()});
    }

    @Test(expected=RuntimeException.class)
    public void rejectsNonExistentClass() {
        JGrade.main(new String[] {"-c", "thisClassDoesNotExist"});
    }

    @Test(expected=RuntimeException.class)
    public void rejectsInvalidFormatArguments() {
        JGrade.main(new String[] {"-f", "invalid", "-c", this.getClass().getCanonicalName()});
    }

    @Test
    public void printedValidJson() throws JSONException {
        JGrade.main(new String[] {"--format", "json", "-c", this.getClass().getCanonicalName()});
        JSONObject json = new JSONObject(captureOut.toString());
        assertTrue(json.has("tests"));
        JSONObject gradedTestResult = (JSONObject) json.getJSONArray("tests").get(0);
        assertTrue(gradedTestResult.has("name"));
        assertEquals("Test GradedTestResult", gradedTestResult.get("name"));
    }

    @Grade
    public void graderMethod(Grader g) {
        g.addGradedTestResult(new GradedTestResult(
                "Test GradedTestResult",
                "1",
                25.0,
                Visibility.VISIBLE
        ));
    }
}
