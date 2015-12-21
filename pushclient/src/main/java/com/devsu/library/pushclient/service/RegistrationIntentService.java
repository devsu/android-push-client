package com.devsu.library.pushclient.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * The GCM registration IntentService.
 */
public class RegistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = RegistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public RegistrationIntentService() {
        super(TAG);
    }

    /**
     * Generates a new registration ID using InstanceID.
     * @param intent The intent with the generated registration ID OR the Exception that ocurred.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        Bundle bundle = new Bundle();
        try {
            String gcmId = intent.getStringExtra(PrefsConstants.PREF_GCM_ID);
            InstanceID instanceID = InstanceID.getInstance(this);
            String regId = instanceID.getToken(gcmId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            bundle.putString(PrefsConstants.PREF_REG_ID, regId);
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(PrefsConstants.REGISTRATION_EXCEPTION, e);
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }
}