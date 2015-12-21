package com.devsu.library.pushclient.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.devsu.library.pushclient.service.PushClientIntentService;

/**
 * The Broadcast Receiver. It receives the Push Messages and handles it via a service.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    /**
     * Receives the GCM Push Message's intent and starts the service.
     * @param context The context.
     * @param intent The intent.
     */
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), PushClientIntentService.class.getName());
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}
