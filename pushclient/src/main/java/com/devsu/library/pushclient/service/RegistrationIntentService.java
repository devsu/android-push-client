package com.devsu.library.pushclient.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.devsu.library.pushclient.client.PushClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {

    public static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        try {
            ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
//            RegistrationResultReceiver receiver = (RegistrationResultReceiver) intent.getParcelableExtra(RegistrationResultReceiver.TAG);
            String gcmId = intent.getStringExtra(PushClient.PREF_GCM_ID);
            InstanceID instanceID = InstanceID.getInstance(this);
            Bundle bundle = new Bundle();
            String regId = instanceID.getToken(gcmId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            bundle.putString(PushClient.PREF_REG_ID, regId);
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}