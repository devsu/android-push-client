package com.devsu.library.pushclient.exception;


public class InvalidLightsPatternException extends PushClientException {

    public InvalidLightsPatternException() {
        super("The light pattern MUST be an array of size 2 with positive values.");
    }
}
