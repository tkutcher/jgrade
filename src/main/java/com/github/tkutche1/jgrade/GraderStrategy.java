package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;

import java.util.List;

/**
 * Interface for the strategy design pattern on top of a {@link Grader} object.
 * If you want to change the grading strategy, for example to grade deductively
 * instead of simply adding the tests as they are, you can call
 * {@link Grader#setGraderStrategy(GraderStrategy)}.
 */
public interface GraderStrategy {

    /**
     * Do work on the list of {@link GradedTestResult}s before they get
     * added to the overall list of tests.
     * @param l The list of {@link GradedTestResult}s to modify (or not
     *          in the case of the default strategy).
     */
    void grade(List<GradedTestResult> l);
}
