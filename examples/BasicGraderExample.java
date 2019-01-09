import com.github.tkutcher.autograder.Grade;
import com.github.tkutcher.autograder.Grader;
import com.github.tkutcher.autograder.gradedtest.GradedTestResult;

public class BasicGraderExample {

    @Grade
    public void singleTestResult(Grader grader) {
        grader.addGradedTestResult(new GradedTestResult("name", "1", 1.0, "hidden"));
    }
}
