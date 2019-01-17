package com.github.tkutche1.jgrade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a method to run before the other @Grade methods when the jar
 * is being run in executable mode. Only run one time per class. The method
 * it annotates must also take a single parameter of type Grader.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeGrading {
}
