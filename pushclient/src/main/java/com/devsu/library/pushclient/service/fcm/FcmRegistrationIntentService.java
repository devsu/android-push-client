package com.devsu.library.pushclient.service.fcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.constants.BundleConstants;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * The GCM registration IntentService.
 */
public class FcmRegistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = FcmRegistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public FcmRegistrationIntentService() {
        super(TAG);
    }

    /**
     * Generates a new registration ID using InstanceID.
     * @param intent The intent with the Receiver tag, and GCM ID.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        if (receiver == null) {
            receiver = PushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.BUNDLE_SERVICE_ORIGIN, TAG);
        String regId = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(regId)) {
            receiver.send(Activity.RESULT_FIRST_USER, bundle);
            return;
        }
        bundle.putString(BundleConstants.BUNDLE_REGISTRATION_ID, regId);
        receiver.send(Activity.RESULT_OK, bundle);
    }
}