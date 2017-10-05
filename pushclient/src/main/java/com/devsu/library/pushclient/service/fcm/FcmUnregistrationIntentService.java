package com.devsu.library.pushclient.service.fcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.devsu.library.pushclient.service.Provider;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * The GCM Unregistration IntentService.
 */
public class FcmUnregistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = FcmUnregistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public FcmUnregistrationIntentService() {
        super(TAG);
    }

    /**
     * Unregisters any GCM Registration ID on the device.
     * @param intent The intent with the Receiver tag.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        if (PushClient.getProvider() != Provider.FCM) {
            return;
        }
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        Bundle bundle = new Bundle();
        bundle.putString(PrefsConstants.SERVICE_ORIGIN, TAG);
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(PrefsConstants.REGISTRATION_EXCEPTION, e);
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }
}