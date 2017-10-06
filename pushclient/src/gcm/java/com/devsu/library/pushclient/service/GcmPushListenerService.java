package com.devsu.library.pushclient.service;

import android.os.Bundle;
import com.devsu.library.pushclient.client.GcmPushClient;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * The IntentService that processes the Push Message.
 */
public class GcmPushListenerService extends GcmListenerService {

    private static final String TAG = GcmPushListenerService.class.getSimpleName();

    /**
     * Retrieves the GCM Push Message.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (GcmPushClient.getSenderId().contentEquals(from)) {
            GcmPushClient.getDelegate().handleNotification(this, data);
        }
    }
}