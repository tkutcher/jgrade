package edu.jhu.cs.jgrade;

public interface Temp {

    /**
     * Enumeration for the different types of streams to get output for,
     * including STDIN and STDERR.
     */
    public enum STREAM {
        STDIN, STDERR;
    }

    String getOutput(STREAM stream);
}
