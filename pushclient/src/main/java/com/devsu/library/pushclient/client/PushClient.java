package com.devsu.library.pushclient.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.delegate.SimpleNotificationDelegate;
import com.devsu.library.pushclient.exception.InvalidSenderIdException;
import com.devsu.library.pushclient.exception.PlayServicesNotFoundException;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.devsu.library.pushclient.service.RegistrationIntentService;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * PushClient's main class.
 */
public final class PushClient implements RegistrationResultReceiver.Receiver {

    /**
     * The singleton instance.
     */
    private static PushClient sInstance;

    /**
     * Message received on IOException indicating GCM ID is invalid.
     */
    private static final String INVALID_SENDER = "INVALID_SENDER";

    /**
     * Log TAG.
     */
    private static final String TAG = PushClient.class.getSimpleName();

    /**
     * Application Context.
     */
    private Context mContext;

    /**
     * The GCM ID.
     */
    private String mGcmId;

    /**
     * The GCM instance.
     */
    private GoogleCloudMessaging mGcm;

    /**
     * The retrieved registation ID.
     */
    private String mRegistrationId;

    /**
     * The initialization callback.
     */
    private InitCallback mInitCallback;

    /**
     * The Push delegate.
     */
    private PushDelegate mPushDelegate;

    /**
     * Registation IntentService's Receiver.
     */
    private ResultReceiver mReceiver;

    /**
     * Private constructor.
     */
    private PushClient() {
        mReceiver = new RegistrationResultReceiver(new Handler());
        ((RegistrationResultReceiver)mReceiver).setReceiver(this);
    }

    /**
     * Initialization with 2 params.
     * @param context The application context.
     * @param gcmId The app's GCM ID.
     */
    public static void initialize(Context context, String gcmId) {
        initialize(context, gcmId, Defaults.generateInitCallback(), Defaults.generateDefaultDelegate(context));
    }

    /**
     * Initialization with 3 params.
     * @param context The application context.
     * @param gcmId The app's GCM ID.
     * @param delegate The delegate that will handle how to show push messages.
     */
    public static void initialize(Context context, String gcmId, PushDelegate delegate) {
        initialize(context, gcmId, Defaults.generateInitCallback(), delegate);
    }

    /**
     * Initialization with 3 params.
     * @param context The application context.
     * @param gcmId The app's GCM ID.
     * @param initCallback The initialization callback.
     */
    public static void initialize(Context context, String gcmId, InitCallback initCallback) {
        initialize(context, gcmId, initCallback, Defaults.generateDefaultDelegate(context));
    }

    /**
     * Initialization with 4 params.
     * @param context The application context.
     * @param gcmId The app's GCM ID.
     * @param delegate The delegate that will handle how to show push messages.
     * @param initCallback The initialization callback.
     */
    public static void initialize(Context context, String gcmId, InitCallback initCallback, PushDelegate delegate) {
        if (sInstance == null) {
            synchronized (PushClient.class) {
                sInstance = new PushClient();
            }
        }
        if (sInstance.mContext != null) {
            throw new RuntimeException(new PushClientException(TAG + " already initialized. Please initialize once on Application."));
        }
        sInstance.mContext = context.getApplicationContext();
        sInstance.mGcmId = gcmId;
        sInstance.mInitCallback = initCallback;
        sInstance.mPushDelegate = delegate;
        sInstance.startIntentServiceIfNeeded();
    }

    /**
     * Launches the GCM Registration IntentService if app is not registered.
     */
    private void startIntentServiceIfNeeded() {
        if (!hasPlayServices()) {
            doOnCallbackError(new PlayServicesNotFoundException());
            return;
        }
        mGcm = GoogleCloudMessaging.getInstance(mContext);
        mRegistrationId = loadRegistrationId();
        if (!TextUtils.isEmpty(mRegistrationId)) {
            doOnCallbackSuccess(false);
            return;
        }
        startIntentService();
    }

    /**
     * Launches the GCM Registration IntentService.
     */
    private void startIntentService() {
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.putExtra(RegistrationResultReceiver.TAG, mReceiver);
        intent.putExtra(PrefsConstants.PREF_GCM_ID, mGcmId);
        mContext.startService(intent);
    }

    /**
     * Receives the GCM Registration IntentService's result.
     * @param resultCode The IntentService's result code.
     * @param resultData The IntentService's extras.
     */
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null && resultCode == Activity.RESULT_OK) {
            mRegistrationId = resultData.getString(PrefsConstants.PREF_REG_ID);
            storeRegistrationId(mRegistrationId);
            doOnCallbackSuccess(true);
            return;
        }
        if (mReceiver != null && resultCode == Activity.RESULT_CANCELED) {
            IOException e = (IOException) resultData.getSerializable(PrefsConstants.REGISTRATION_EXCEPTION);
            if (e == null)
                return;
            doOnCallbackError(e.getMessage().equalsIgnoreCase(INVALID_SENDER)
                    ? new InvalidSenderIdException(mGcmId) : new PushClientException(e));
        }
    }

    /**
     * Executes an initialization error callback.
     * @param e The exception for the error callback.
     */
    private void doOnCallbackError(final PushClientException e) {
        if (mInitCallback == null) {
            return;
        }
        final InitCallback callback = mInitCallback;
        Handler handler = new Handler(mContext.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                callback.onError(e);
            }

        });
    }

    /**
     * Executes an initialization success callback.
     * @param hasBeenUpdated Indicates whether the GCM Registration IntentService was used or not.
     */
    private void doOnCallbackSuccess(final boolean hasBeenUpdated) {
        if (mInitCallback == null) {
            return;
        }
        final InitCallback callback = mInitCallback;
        Handler handler = new Handler(mContext.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                callback.onSuccess(mRegistrationId, hasBeenUpdated);
            }
        });
    }

    /**
     * Checks if the device has Play Services installed.
     */
    private boolean hasPlayServices() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS;
    }

    /**
     * Checks the app's version code.
     * @return The app's version code.
     */
    private int getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Stores the registration ID on Shared Preferences.
     * @param registrationId The Registration ID to be stored.
     */
    private void storeRegistrationId(String registrationId) {
        SharedPreferences.Editor editor = getPushPreferences().edit();
        editor.putInt(PrefsConstants.PREF_APP_VERSION, getAppVersion());
        editor.putString(PrefsConstants.PREF_REG_ID, registrationId);
        editor.putString(PrefsConstants.PREF_GCM_ID, mGcmId);
        editor.apply();
    }

    /**
     * Loads the registration ID from the Shared Preferences.
     * @return the registration ID.
     */
    private String loadRegistrationId() {
        SharedPreferences prefs = getPushPreferences();
        String registrationId = prefs.getString(PrefsConstants.PREF_REG_ID, null);
        int registeredVersion = prefs.getInt(PrefsConstants.PREF_APP_VERSION, -1);
        String gcmId = prefs.getString(PrefsConstants.PREF_GCM_ID, null);
        if (TextUtils.isEmpty(registrationId) || registeredVersion != getAppVersion()
                || gcmId != null && !gcmId.equals(mGcmId)) {
            return null;
        }
        return registrationId;
    }

    /**
     * Unregisters this device from GCM.
     */
    public static void unregister() {
        if (sInstance.mRegistrationId == null) {
            throw new RuntimeException(TAG + " is not registered");
        }
        try {
            sInstance.mGcm.unregister();
            sInstance.getPushPreferences().edit().clear().apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the SharedPreferences used for Push Settings.
     * @return The SharedPreferences used for Push Settings.
     */
    private SharedPreferences getPushPreferences() {
        return mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /**
     * Sets the delegate that handles the push message display.
     * @param pushDelegate The delegate that handles the push message display.
     */
    public static void setDelegate(PushDelegate pushDelegate) {
        sInstance.mPushDelegate = pushDelegate;

    }

    /**
     * Gets the delegate that handles the push message display.
     * @return The delegate that handles the push message display.
     */
    public static PushDelegate getDelegate() {
        return sInstance.mPushDelegate;
    }

    /**
     * Class default values.
     */
    private static class Defaults {

        private static InitCallback generateInitCallback() {
            return new InitCallback() {
                @Override
                public void onSuccess(String registrationId, boolean hasUpdated) {
                    Log.d(TAG, "RegistrationID: " + registrationId);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
            };
        }

        private static PushDelegate generateDefaultDelegate(Context context) {
            return new SimpleNotificationDelegate(context);
        }
    }
}