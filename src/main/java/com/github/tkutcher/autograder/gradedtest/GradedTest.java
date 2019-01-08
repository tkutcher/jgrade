package com.github.tkutcher.autograder.gradedtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.tkutcher.autograder.gradedtest.Consts.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    String name() default DEFAULT_NAME;
    String number() default DEFAULT_NUMBER;
    double points() default DEFAULT_POINTS;
    String visibility() default DEFAULT_VISIBILITY;
}
