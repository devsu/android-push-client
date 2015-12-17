package com.devsu.library.pushclient.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.delegate.SimpleNotificationDelegate;
import com.devsu.library.pushclient.exception.PlayServicesNotFoundException;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.service.RegistrationIntentService;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public final class PushClient implements RegistrationResultReceiver.Receiver {

    private static PushClient sInstance;

    private static final String TAG = PushClient.class.getSimpleName();
    public static final String PREF_REG_ID = "registration_id";
    private static final String PREF_APP_VERSION = "app_version";
    public static final String PREF_GCM_ID = "gcm_id";

    private static final String INVALID_SENDER = "INVALID_SENDER";

    private Context mContext;
    private String mGcmId;
    private GoogleCloudMessaging mGcm;
    private String mRegistrationId;
    private InitCallback mInitCallback;
    private PushDelegate mPushDelegate;

    private ResultReceiver mReceiver;

    //AVOID INSTANTIATION
    private PushClient() {
        mReceiver = new RegistrationResultReceiver(new Handler());
        ((RegistrationResultReceiver)mReceiver).setReceiver(this);
    }

    public static void initialize(Context context, String gcmId) {
        initialize(context, gcmId, Defaults.generateInitCallback(), Defaults.generateDefaultDelegate(context));
    }

    public static void initialize(Context context, String gcmId, PushDelegate delegate) {
        initialize(context, gcmId, Defaults.generateInitCallback(), delegate);
    }

    public static void initialize(Context context, String gcmId, InitCallback initCallback) {
        initialize(context, gcmId, initCallback, Defaults.generateDefaultDelegate(context));
    }

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
        sInstance.initializeInBackground();
    }

    private void initializeInBackground() {
        if (!hasPlayServices()) {
            if (mInitCallback == null) {
                return;
            }
            final InitCallback callback = mInitCallback;
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    callback.onError(new PlayServicesNotFoundException());
                }

            });
            return;
        }
        mGcm = GoogleCloudMessaging.getInstance(mContext);
        mRegistrationId = loadRegistrationId();
        if (!TextUtils.isEmpty(mRegistrationId)) {
            if (mInitCallback == null) {
                return;
            }
            final InitCallback callback = mInitCallback;
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    callback.onSuccess(mRegistrationId, false);
                }
            });
            return;
        }
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.putExtra(RegistrationResultReceiver.TAG, mReceiver);
        intent.putExtra(PREF_GCM_ID, mGcmId);
        mContext.startService(intent);
    }


    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null && resultCode == Activity.RESULT_OK) {
            mRegistrationId = resultData.getString(PREF_REG_ID);
            storeRegistrationId(mRegistrationId);
            if (mInitCallback == null) {
                return;
            }
            final InitCallback callback = mInitCallback;
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    callback.onSuccess(mRegistrationId, false);
                }
            });
        }
    }


    private boolean hasPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        return resultCode == 0;
    }


    private int getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
        }
        return -1;
    }


    private SharedPreferences getPushPreferences() {
        return mContext.getSharedPreferences(TAG, 0);
    }


    private void storeRegistrationId(String registrationId) {
        SharedPreferences.Editor editor = getPushPreferences().edit();
        editor.putInt(PREF_APP_VERSION, getAppVersion());
        editor.putString(PREF_REG_ID, registrationId);
        editor.putString(PREF_GCM_ID, mGcmId);
        editor.apply();
    }


    private String loadRegistrationId() {
        SharedPreferences prefs = getPushPreferences();
        String registrationId = prefs.getString(PREF_REG_ID, null);
        if (TextUtils.isEmpty(registrationId)) {
            return null;
        }

        int registeredVersion = prefs.getInt(PREF_APP_VERSION, -1);
        if (registeredVersion != getAppVersion()) {
            return null;
        }

        String gcmId = prefs.getString(PREF_GCM_ID, null);
        if ((gcmId != null) && (!gcmId.equals(mGcmId))) {
            return null;
        }

        return registrationId;
    }

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

    public static void setDelegate(PushDelegate pushDelegate) {
        sInstance.mPushDelegate = pushDelegate;

    }

    public static PushDelegate getDelegate() {
        return sInstance.mPushDelegate;
    }

    private static class Defaults {

        private static InitCallback generateInitCallback() {
            return new InitCallback() {
                @Override
                public void onSuccess(String registrationId, boolean hasUpdated) {
                    System.out.println("This is the registrationId: "+ registrationId);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("This is an error: " + throwable.getMessage());
                }
            };
        }

        private static PushDelegate generateDefaultDelegate(Context context) {
            return new SimpleNotificationDelegate(context);
        }
    }
}