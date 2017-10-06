package com.devsu.library.pushclient.service.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.constants.BundleConstants;
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
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        if (receiver == null) {
            receiver = PushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.BUNDLE_SERVICE_ORIGIN, TAG);
        try {
            String gcmId = intent.getStringExtra(BundleConstants.BUNDLE_SENDER_ID);
            InstanceID instanceID = InstanceID.getInstance(this);
            String regId = instanceID.getToken(gcmId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (TextUtils.isEmpty(regId)) {
                receiver.send(Activity.RESULT_FIRST_USER, null);
                return;
            }
            bundle.putString(BundleConstants.BUNDLE_REGISTRATION_ID, regId);
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(BundleConstants.BUNDLE_REGISTRATION_EXCEPTION, e);
            Log.e(TAG, "Error occurred when using RegistrationIntentService", e);
            if (receiver != null) {
                receiver.send(Activity.RESULT_CANCELED, bundle);
            }
        }
    }
}