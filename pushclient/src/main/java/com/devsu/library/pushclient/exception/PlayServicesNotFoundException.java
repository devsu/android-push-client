package com.devsu.library.pushclient.exception;

/**
 * Exception thrown when no Play Services are found on the device.
 */
public class PlayServicesNotFoundException extends PushClientException {

    /**
     * Default constructor.
     */
    public PlayServicesNotFoundException() {
        super("Google Play Services not found.");
    }
}
