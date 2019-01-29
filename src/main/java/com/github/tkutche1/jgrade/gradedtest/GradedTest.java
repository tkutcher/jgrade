package com.github.tkutche1.jgrade.gradedtest;

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
    /**
     * The name of the test.
     * @return The name of the test.
     */
    String name() default GradedTestResult.DEFAULT_NAME;

    /**
     * The number corresponding to the test.
     * @return The number corresponding to the test.
     */
    String number() default GradedTestResult.DEFAULT_NUMBER;

    /**
     * The number of points the test is worth.
     * @return The number of points the test is worth.
     */
    double points() default GradedTestResult.DEFAULT_POINTS;

    /**
     * The visibility level of the test.
     * @return The visibility level of the test.
     */
    String visibility() default GradedTestResult.DEFAULT_VISIBILITY;
}
