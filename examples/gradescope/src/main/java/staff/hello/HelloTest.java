package staff.hello;

import edu.jhu.cs.jgrade.gradedtest.GradedTest;
import org.junit.Before;
import org.junit.Test;
import student.hello.Greeting;

import static org.junit.Assert.assertEquals;

public class HelloTest {

    static final boolean DEBUG = true;

    private static final String GREETING = "Hello";

    private Greeting unit;

    @Before
    public void initUnit() {
        this.unit = DEBUG ? new Hello(GREETING) : new student.hello.Hello(GREETING);
    }

    @Test
    @GradedTest(name="greet() works")
    public void defaultGreeting() {
        assertEquals(GREETING, unit.greet());
    }

    @Test
    @GradedTest(name="greet(String who) works", points=2.0)
    public void greetSomebody() {
        assertEquals(GREETING + ", World!", unit.greet("World"));
    }

    @Test
    @GradedTest(name="prints greeting", points=0.0)
    public void printGreeting() {
        unit.printGreeting();
    }
}
