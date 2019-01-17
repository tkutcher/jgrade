package com.github.tkutche1.jgrade;

import java.util.List;


/** For the result of interaction with a command line interface program. */
public interface CLIResult {

    /**
     * Enumeration for the different types of streams to get output for,
     * including STDOUT and STDERR.
     */
    enum STREAM {
        STDOUT, STDERR;
    }

    /**
     * Get the output for the specified stream.
     * @param stream The {@link STREAM} to get output from.
     * @return A single String of the output.
     */
    String getOutput(STREAM stream);

    /**
     * Get the output for the specified stream split by lines.
     * @param stream The {@link STREAM} to get output from.
     * @return A List of Strings of output, one list item per line.
     */
    List<String> getOutputByLine(STREAM stream);
}
