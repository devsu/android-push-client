package com.devsu.library.pushclient.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.receiver.GCMBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * The IntentService that processes the Push Message.
 */
public class PushClientIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = PushClientIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public PushClientIntentService() {
        super(TAG);
    }

    /**
     * Retrieves the GCM Push Message payload, and sends it to the Delegate's handleNotification method.
     * @param intent The push message's intent.
     */
    public void onHandleIntent(Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        if ((!extras.isEmpty()) && (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))) {
            PushClient.getDelegate().handleNotification(this, extras);
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }
}