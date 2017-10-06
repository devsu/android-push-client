package com.devsu.library.pushclient.exception;

/**
 * Exception wrapper for any Exception that has not been previously controlled.
 */
public class PushClientException extends RuntimeException {

    /**
     * Single param constructor.
     * @param message The message.
     */
    public PushClientException(String message) {
        super(message);
    }

    /**
     * Single param constructor.
     * @param t The throwable that triggered this exception.
     */
    public PushClientException(Throwable t) {
        super(t);
    }
}