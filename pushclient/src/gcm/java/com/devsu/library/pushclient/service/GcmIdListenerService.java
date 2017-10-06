package com.devsu.library.pushclient.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * The Listener Service for Registration ID's refresh.
 */
public class GcmIdListenerService extends InstanceIDListenerService {

    /**
     * Starts the Registration Service when the Registration ID has to be refreshed.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}