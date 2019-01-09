import com.github.tkutcher.autograder.BeforeGrade;
import com.github.tkutcher.autograder.DoneGrade;
import com.github.tkutcher.autograder.Grade;
import com.github.tkutcher.autograder.Grader;
import com.github.tkutcher.autograder.gradedtest.GradedTestResult;

public class BasicGraderExample {

    @BeforeGrade
    public void initGrader(Grader grader) {
        grader.startTimer();
    }

    @Grade
    public void singleTestResult(Grader grader) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000);
        grader.addGradedTestResult(new GradedTestResult("name", "1", 1.0, "hidden"));
    }

    @DoneGrade
    public void endGrader(Grader grader) {
        grader.stopTimer();
    }
}
