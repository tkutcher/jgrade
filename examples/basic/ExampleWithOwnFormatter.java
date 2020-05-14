import com.github.tkutcher.jgrade.AfterGrading;
import com.github.tkutcher.jgrade.BeforeGrading;
import com.github.tkutcher.jgrade.Grade;
import com.github.tkutcher.jgrade.Grader;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;
import com.github.tkutcher.jgrade.gradescope.GradescopeJsonFormatter;

import static com.github.tkutcher.jgrade.gradedtest.GradedTestResult.VISIBLE;


/* This is an example of a class where you don't use the options for the main entry
 * point to produce output. To do this you would invoke JGrade with the option to
 * produce no output (the --no-output flag). So this could be invoked as follows:
 *
 * jgrade -x -c ExampleWithOwnFormatter
 */

public class ExampleWithOwnFormatter extends BasicGraderExample {

    Formatter formatter;


    @BeforeGrading
    public void initGrader(Grader grader) {
        grader.setMaxScore(20.0);
    }


    /* The reflection will invoke inherited annotated methods as well as the ones in this
     * class (note that this class extends the basic example class).
     */

    @Grade
    public void subclassMethod(Grader grader) {
        grader.addGradedTestResult(
                new GradedTestResult("subclass", "", 1.0, VISIBLE)
        );
    }

    /* You can set details of the formatter (like Gradescope specific things like
     * stdout_visibility and pretty-printing for json). This should pretty-print the
     * JSON to standard out.
     *
     * FIXME - Is there a better way than overriding the ending method in the subclass
     *         and stopping the timer there?
     */

    @AfterGrading
    @Override
    public void endGrader(Grader grader) {
        grader.stopTimer();
        formatter.setPrettyPrint(4);
        System.out.println(formatter.format(grader));
    }

}
