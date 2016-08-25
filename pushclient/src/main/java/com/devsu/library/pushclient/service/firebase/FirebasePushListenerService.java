package com.devsu.library.pushclient.service.firebase;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.service.Provider;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * The IntentService that processes the Push Message.
 */
public class FirebasePushListenerService extends FirebaseMessagingService {

    private static final String TAG = FirebasePushListenerService.class.getSimpleName();

    /**
     * Retrieves the GCM Push Message.
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (PushClient.getProvider() == null) {
            throw new RuntimeException(new PushClientException(TAG + " error. Provider cannot be null."));
        }
        if (PushClient.getGcmId().contentEquals(message.getFrom()) && PushClient.getProvider() == Provider.FCM) {
            PushClient.getDelegate().handleNotification(this, message.getData());
        }
    }
}