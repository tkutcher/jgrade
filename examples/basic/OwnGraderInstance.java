/* OwnGraderInstance.java
 *
 * Alternatively, the Grader class is public so it can just be instantiated
 * on it's own if you don't want to use the annotations (since they essentially
 * use the class like a singleton anyway). This would be invoked like any main
 * program:
 *
 * $ java -cp jgrade-1.0.jar OwnGraderInstance
 */

import com.github.tkutche1.jgrade.Grader;
import com.github.tkutche1.jgrade.gradescope.GradescopeJsonObserver;

public class OwnGraderInstance {
    public static void main(String[] args) {
        Grader myGrader = new Grader();
        GradescopeJsonObserver myObserver = new GradescopeJsonObserver();
        myGrader.attachOutputObserver(myObserver);

        myGrader.startTimer();
        myGrader.runJUnitGradedTests(ExampleGradedTests.class);
        myGrader.stopTimer();

        myGrader.setMaxScore(100.0);
        myGrader.notifyOutputObservers();

        myObserver.setPrettyPrint(2);
        System.out.println(myObserver.toString());
    }
}
