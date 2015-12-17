package com.devsu.library.pushclient.client;

public interface InitCallback {

    void onSuccess(String registrationId, boolean hasBeenUpdated);

    void onError(Throwable throwable);
}
