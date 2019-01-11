import edu.jhu.cs.jgrade.gradedtest.GradedTest;
import org.junit.Test;

import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.HIDDEN;
import static edu.jhu.cs.jgrade.gradedtest.GradedTestResult.VISIBLE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/* Some example unit tests annotated with @GradedTest. An instance of a GradedTestListener
 * when attached to a JUnit run will keep a list of all results from @GradedTest annotated
 * methods.
 */

public class ExampleGradedTests {
    @Test
    @GradedTest(name="True is true", points=2.0, visibility=VISIBLE)
    public void trueIsTrue() {
        assertTrue(true);
    }

    @Test
    @GradedTest(name="False is false", number="2", points=3.0, visibility=HIDDEN)
    public void falseIsFalse() {
        assertFalse(false);
    }

    @Test
    @GradedTest(name="Captures output")
    public void capturesOutput() {
        System.out.println("hello");
    }

    @Test
    @GradedTest(name="This test should fail")
    public void badTest() {
        fail();
    }
}
