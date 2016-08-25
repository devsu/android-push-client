package com.devsu.library.pushclient.service.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.devsu.library.pushclient.service.Provider;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * The GCM registration IntentService.
 */
public class GcmRegistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = GcmRegistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public GcmRegistrationIntentService() {
        super(TAG);
    }

    /**
     * Generates a new registration ID using InstanceID.
     * @param intent The intent with the Receiver tag, and GCM ID.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        if (PushClient.getProvider() != Provider.GCM) {
            return;
        }
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        if (receiver == null) {
            receiver = PushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(PrefsConstants.SERVICE_ORIGIN, TAG);
        try {
            String gcmId = intent.getStringExtra(PrefsConstants.PREF_GCM_ID);
            InstanceID instanceID = InstanceID.getInstance(this);
            String regId = instanceID.getToken(gcmId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (TextUtils.isEmpty(regId)) {
                receiver.send(Activity.RESULT_FIRST_USER, null);
                return;
            }
            bundle.putString(PrefsConstants.PREF_REG_ID, regId);
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(PrefsConstants.REGISTRATION_EXCEPTION, e);
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }
}