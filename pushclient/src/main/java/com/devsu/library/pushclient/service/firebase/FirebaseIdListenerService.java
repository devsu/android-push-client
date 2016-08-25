package com.devsu.library.pushclient.service.firebase;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * The Listener Service for Registration ID's refresh.
 */
public class FirebaseIdListenerService extends FirebaseInstanceIdService {

    /**
     * Starts the Registration Service when the Registration ID has to be refreshed.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
        startService(intent);
    }
}