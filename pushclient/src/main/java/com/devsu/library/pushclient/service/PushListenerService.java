package com.devsu.library.pushclient.service;

import android.os.Bundle;

import com.devsu.library.pushclient.client.PushClient;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * The IntentService that processes the Push Message.
 */
public class PushListenerService extends GcmListenerService {

    /**
     * Retrieves the GCM Push Message.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (PushClient.getGcmId().contentEquals(from))
            PushClient.getDelegate().handleNotification(this, data);
    }
}