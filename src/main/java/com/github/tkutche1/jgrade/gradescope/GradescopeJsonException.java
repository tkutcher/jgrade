package com.github.tkutche1.jgrade.gradescope;

/**
 * An exception for when the JSON to output to Gradescope does not match the
 * schema it expects.
 */
public class GradescopeJsonException extends RuntimeException {

    /**
     * Create an instance of this exception.
     * @param message The message for what caused the exception.
     */
    public GradescopeJsonException(String message) {
        super(message);
    }
}
