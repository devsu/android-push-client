package com.devsu.library.pushclient.service;

import android.app.IntentService;
import android.content.Intent;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.receiver.GCMBroadcastReceiver;

public class PushClientIntentService extends IntentService {

    public static final String TAG = PushClientIntentService.class.getSimpleName();

    public PushClientIntentService() {
        super(TAG);
    }

    public void onHandleIntent(Intent intent) {
        PushClient.getDelegate().onReceive(this, intent);
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }
}