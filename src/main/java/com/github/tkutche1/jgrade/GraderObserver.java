package com.github.tkutche1.jgrade;

/**
 * An observer that can observe a {@link Grader} to produce output. Note that
 * the {@link Grader} does not automatically call
 * {@link GraderObserver#update(Grader)} on any changes, so the client must
 * manually make sure the {@link Grader} updates.
 */
public interface GraderObserver {

    /**
     * Update for the grader observing.
     * @param grader The grader observing.
     */
    void update(Grader grader);

    /**
     * Get the output for the observed grader.
     * @return The output of the observed grader.
     */
    String getOutput();
}
