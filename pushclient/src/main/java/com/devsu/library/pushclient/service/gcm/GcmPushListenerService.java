package com.devsu.library.pushclient.service.gcm;

import android.os.Bundle;
import com.devsu.library.pushclient.client.PushClient;
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
        if (PushClient.getSenderId().contentEquals(from)) {
            PushClient.getDelegate().handleNotification(this, data);
        }
    }
}