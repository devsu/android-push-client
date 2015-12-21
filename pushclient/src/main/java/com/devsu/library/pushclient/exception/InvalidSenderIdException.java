package com.devsu.library.pushclient.exception;

/**
 * Exception thrown when the GCM ID is invalid upon registration.
 */
public class InvalidSenderIdException extends PushClientException {

    /**
     * Single param constructor.
     * @param gcmId The invalid GCM ID.
     */
    public InvalidSenderIdException(String gcmId) {
        super("The GCM ID '" + gcmId + "' if invalid. Please check your current GCM ID.");
    }
}
