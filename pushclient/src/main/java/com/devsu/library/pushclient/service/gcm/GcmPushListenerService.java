package com.devsu.library.pushclient.service.gcm;

import android.os.Bundle;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.service.Provider;
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
        if (PushClient.getProvider() == null) {
            throw new RuntimeException(new PushClientException(TAG + " error. Provider cannot be null."));
        }
        if (PushClient.getGcmId().contentEquals(from) && PushClient.getProvider() == Provider.GCM) {
            PushClient.getDelegate().handleNotification(this, data);
        }
    }
}