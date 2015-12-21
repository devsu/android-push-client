package com.devsu.library.pushclient.client;

/**
 * The callback invoked after PushClient's initialization.
 */
public interface InitCallback {

    /**
     * Callback when registration ID fetch is successful.
     * @param registrationId The retrieved registration ID.
     * @param hasBeenUpdated <b>true</b> if the registration ID was obtained after a successful GCM registration.
     *                       <b>false</b> if the registration ID was obtained from the Shared Preferences (hence, no update).
     */
    void onSuccess(String registrationId, boolean hasBeenUpdated);

    void onError(Throwable throwable);
}
