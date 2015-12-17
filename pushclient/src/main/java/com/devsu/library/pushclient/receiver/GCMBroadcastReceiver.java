package com.devsu.library.pushclient.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.devsu.library.pushclient.service.PushClientIntentService;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), PushClientIntentService.class.getName());
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}
