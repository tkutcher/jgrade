package edu.jhu.cs.jgrade.gradedtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The GradedTest annotation is primarily based off of capturing the data for
 * a test object in the Gradescope JSON output. They are used to create
 * corresponding {@link GradedTestResult} objects.
 *
 * Uses a String for the name of the test (default "Unnamed test") , a String
 * for the question number (default ""), a double for the number of points the
 * test is worth(defaults to 1.0), and a String for the visibility of the test
 * (defaults to "visible").
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    String name() default GradedTestResult.DEFAULT_NAME;
    String number() default GradedTestResult.DEFAULT_NUMBER;
    double points() default GradedTestResult.DEFAULT_POINTS;
    String visibility() default GradedTestResult.DEFAULT_VISIBILITY;
}
