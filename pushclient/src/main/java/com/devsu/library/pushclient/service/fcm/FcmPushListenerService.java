package com.devsu.library.pushclient.service.fcm;

import com.devsu.library.pushclient.client.PushClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * The IntentService that processes the Push Message.
 */
public class FcmPushListenerService extends FirebaseMessagingService {

    private static final String TAG = FcmPushListenerService.class.getSimpleName();

    /**
     * Retrieves the GCM Push Message.
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (PushClient.getSenderId().contentEquals(message.getFrom())) {
            PushClient.getDelegate().handleNotification(this, message.getData());
        }
    }
}