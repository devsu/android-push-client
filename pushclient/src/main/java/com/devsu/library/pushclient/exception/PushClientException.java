package com.devsu.library.pushclient.exception;

public class PushClientException extends Exception {

    public PushClientException(String message) {
        super(message);
    }

    public PushClientException(Throwable t) {
        super(t);
    }
}