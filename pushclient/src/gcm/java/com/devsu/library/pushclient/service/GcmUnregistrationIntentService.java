package com.devsu.library.pushclient.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.devsu.library.pushclient.client.GcmPushClient;
import com.devsu.library.pushclient.constants.BundleConstants;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;

/**
 * The GCM Unregistration IntentService.
 */
public class GcmUnregistrationIntentService extends IntentService {

    /**
     * Log TAG.
     */
    public static final String TAG = GcmUnregistrationIntentService.class.getSimpleName();

    /**
     * Default constructor.
     */
    public GcmUnregistrationIntentService() {
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
            receiver = GcmPushClient.getReceiver();
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.BUNDLE_SERVICE_ORIGIN, TAG);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            instanceID.deleteInstanceID();
            receiver.send(Activity.RESULT_OK, bundle);
        } catch (IOException e) {
            bundle.putSerializable(BundleConstants.BUNDLE_REGISTRATION_EXCEPTION, e);
            Log.e(TAG, "Error occurred when using UnregistrationIntentService", e);
            if (receiver != null) {
                receiver.send(Activity.RESULT_CANCELED, bundle);
            }
        }
    }
}