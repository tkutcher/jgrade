import com.github.tkutcher.jgrade.*;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import com.github.tkutcher.jgrade.gradescope.GradescopeJsonObserver;

public class BasicGraderExample {

    private GradescopeJsonObserver output;

    @BeforeGrade
    public void initGrader(Grader grader) {
        grader.startTimer();
//        output = new GradescopeJsonObserver(grader);
//        grader.attachOutputObserver(output);
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
//        grader.notifyOutputObservers();
//        System.out.println(output.toString(2));
    }
}
