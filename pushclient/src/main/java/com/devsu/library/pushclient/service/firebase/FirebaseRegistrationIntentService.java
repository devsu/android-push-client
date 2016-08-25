package com.devsu.library.pushclient.service.firebase;

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
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * The GCM registration IntentService.
 */
public class FirebaseRegistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = FirebaseRegistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public FirebaseRegistrationIntentService() {
        super(TAG);
    }

    /**
     * Generates a new registration ID using InstanceID.
     * @param intent The intent with the Receiver tag, and GCM ID.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        if (PushClient.getProvider() != Provider.FCM) {
            return;
        }
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        if (receiver == null) {
            receiver = PushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(PrefsConstants.SERVICE_ORIGIN, TAG);
        String regId = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(regId)) {
            receiver.send(Activity.RESULT_FIRST_USER, bundle);
            return;
        }
        bundle.putString(PrefsConstants.PREF_REG_ID, regId);
        receiver.send(Activity.RESULT_OK, bundle);
    }
}