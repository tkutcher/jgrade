
/* BasicGraderExample.java
 *
 * This is an example class that you could pass as a parameter to the main method
 * of JGrade. You could invoke it as follows to get pretty-printed JSON output for
 * all of the Grader members it modifies. The entry point of the jar is the main
 * method of the JGrade class.
 *
 * jgrade -f json --pretty-print -c BasicGraderExample
 *
 * The main program also defaults to json output if no -f (--format) flag is
 * provided. So, on gradescope, it may be invoked like this (since you wouldn't
 * need pretty-print and could put it right in to a results.json file.
 *
 * jgrade -o results/results.json -c BasicGraderExample
 */


/* You'll have to import classes from the jgrade and jgrade.gradedtest packages. */
import com.github.tkutcher.jgrade.BeforeGrading;
import com.github.tkutcher.jgrade.AfterGrading;
import com.github.tkutcher.jgrade.Grade;
import com.github.tkutcher.jgrade.Grader;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;

import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.HIDDEN;


public class BasicGraderExample {

    /* All @Grade/@BeforeGrading/@AfterGrading methods must take exactly one parameter
     * of type Grader. This parameter is the same grader throughout.
     *
     * @BeforeGrading methods are run before others.
     */

    @BeforeGrading
    public void initGrader(Grader grader) {
        grader.startTimer();
    }


    /* You can run unit tests that are annotated with @GradedTest to add
     * GradedTestResults to the Grader in this way.
     */

    @Grade
    public void runGradedUnitTests(Grader grader) {
        grader.runJUnitGradedTests(ExampleGradedTests.class);
    }


    /* You can also manually add GradedTestResults you create to the grader. */

    @Grade
    public void singleTestResult(Grader grader) {
        grader.addGradedTestResult(
                new GradedTestResult("manual test", "1", 1.0, HIDDEN)
        );
    }


    /* Grader.startTimer() and Grader.stopTimer() can be used to time the grader */

    @Grade
    public void loopForTime(Grader grader) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1000);
    }


    /* @AfterGrading methods are run after all other methods. */

    @AfterGrading
    public void endGrader(Grader grader) {
        grader.stopTimer();
    }
}
