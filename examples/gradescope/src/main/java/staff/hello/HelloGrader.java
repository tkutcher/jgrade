package staff.hello;

import edu.jhu.cs.jgrade.Grade;
import edu.jhu.cs.jgrade.Grader;

public class HelloGrader {
    @Grade
    public void runUnitTests(Grader grader) {
        grader.runJUnitGradedTests(HelloTest.class);
    }

    @Grade
    public void runCheckstyle(Grader grader) {
//        CheckstyleGrader checker = new CheckstyleGrader();
//        checker.setLib(PATH_TO_LIBRARY);
//        checker.setConfig(PATH_TO_CHECKSTYLE_CONFIG);
//        checker.setTotalPoints(10);
//        checker.setPointsPerError(2.0);
//        GradedTestResult r = checker.runForGradedTestResult();
//        grader.addGradedTestResult(r);
    }
}
