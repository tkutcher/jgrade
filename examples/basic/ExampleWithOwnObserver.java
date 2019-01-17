import com.github.tkutche1.jgrade.AfterGrading;
import com.github.tkutche1.jgrade.BeforeGrading;
import com.github.tkutche1.jgrade.Grade;
import com.github.tkutche1.jgrade.Grader;
import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;
import com.github.tkutche1.jgrade.gradescope.GradescopeJsonObserver;

import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.VISIBLE;


/* This is an example of a class where you don't use the options for the main entry
 * point to produce output. To do this you would invoke JGrade with the option to
 * produce no output (the --no-output flag). So this could be invoked as follows:
 *
 * jgrade -x -c ExampleWithOwnObserver
 */

public class ExampleWithOwnObserver extends BasicGraderExample {

    private GradescopeJsonObserver observer;

    /* You can attach your own observer to alter the details it outputs. You can
     * also add to the grader members like a max score.
     */

    @BeforeGrading
    public void addObserverDetails(Grader grader) {
        observer = new GradescopeJsonObserver();
        grader.attachOutputObserver(observer);
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

    /* You can set details of the observer (like Gradescope specific things like
     * stdout_visibility and pretty-printing for json). This should pretty-print the
     * JSON to standard out. Also note that the timer is stopped before the observers
     * are notified so they know the time to output.
     *
     * FIXME - Is there a better way than overriding the ending method in the subclass
     *         and stopping the timer there?
     */

    @AfterGrading
    @Override
    public void endGrader(Grader grader) {
        grader.stopTimer();
        grader.notifyOutputObservers();
        observer.setPrettyPrint(4);
        System.out.println(observer.toString());
    }

}
