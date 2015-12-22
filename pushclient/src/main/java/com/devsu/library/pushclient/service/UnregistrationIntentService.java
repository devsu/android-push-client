package com.devsu.library.pushclient.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * The GCM Unregistration IntentService.
 */
public class UnregistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = UnregistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public UnregistrationIntentService() {
        super(TAG);
    }

    /**
     * Unregisters any GCM Registration ID on the device.
     * @param intent The intent with the Receiver tag.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        Bundle bundle = new Bundle();
        bundle.putString(PrefsConstants.SERVICE_ORIGIN, TAG);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            instanceID.deleteInstanceID();
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(PrefsConstants.REGISTRATION_EXCEPTION, e);
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }
}