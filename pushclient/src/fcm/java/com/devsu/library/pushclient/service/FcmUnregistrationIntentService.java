package com.devsu.library.pushclient.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.devsu.library.pushclient.client.FcmPushClient;
import com.devsu.library.pushclient.constants.BundleConstants;
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
        ResultReceiver receiver = intent.getParcelableExtra(RegistrationResultReceiver.TAG);
        if (receiver == null) {
            receiver = FcmPushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.BUNDLE_SERVICE_ORIGIN, TAG);
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(BundleConstants.BUNDLE_REGISTRATION_EXCEPTION, e);
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }
}