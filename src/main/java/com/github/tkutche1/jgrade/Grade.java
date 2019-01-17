package com.github.tkutche1.jgrade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for any method to grade when the jar is run as an executable.
 * Just annotate a method with @Grade; make sure the method takes exactly one
 * parameter of type Grader. That Grader object will be the same one used for
 * all methods within that class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Grade {
}
