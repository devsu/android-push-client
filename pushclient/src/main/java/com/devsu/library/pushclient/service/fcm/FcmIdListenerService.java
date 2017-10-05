package com.devsu.library.pushclient.service.fcm;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * The Listener Service for Registration ID's refresh.
 */
public class FcmIdListenerService extends FirebaseInstanceIdService {

    /**
     * Starts the Registration Service when the Registration ID has to be refreshed.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, FcmRegistrationIntentService.class);
        startService(intent);
    }
}