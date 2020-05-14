package com.github.tkutche1.jgrade;

/**
 * A formatter that formats data of a {@link Grader} to produce output.
 */
public interface OutputFormatter {

    /**
     * Get the formatted output of the grader.
     * @param grader The grader observing.
     */
    String format(Grader grader);
}
