package com.devsu.library.pushclient.exception;


public class PlayServicesNotFoundException extends PushClientException {

    public PlayServicesNotFoundException() {
        super("Google Play Services not found.");
    }
}
