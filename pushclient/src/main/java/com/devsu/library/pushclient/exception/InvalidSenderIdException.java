package com.devsu.library.pushclient.exception;


public class InvalidSenderIdException extends PushClientException {

    public InvalidSenderIdException(String gcmId) {
        super("The GCM ID '" + gcmId + "' if invalid. Please check your current GCM ID.");
    }
}
