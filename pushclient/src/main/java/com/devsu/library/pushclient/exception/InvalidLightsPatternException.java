package com.devsu.library.pushclient.exception;

/**
 * Exception thrown when the Lights Pattern is not valid.
 */
public class InvalidLightsPatternException extends PushClientException {

    /**
     * Default constructor.
     */
    public InvalidLightsPatternException() {
        super("The light pattern MUST be an array of size 2 with positive values.");
    }
}
