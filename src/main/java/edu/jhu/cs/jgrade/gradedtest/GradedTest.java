package edu.jhu.cs.jgrade.gradedtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    String name() default GradedTestResult.DEFAULT_NAME;
    String number() default GradedTestResult.DEFAULT_NUMBER;
    double points() default GradedTestResult.DEFAULT_POINTS;
    String visibility() default GradedTestResult.DEFAULT_VISIBILITY;
}
