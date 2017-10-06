package com.devsu.library.pushclient.client;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.exception.InvalidSenderIdException;
import com.devsu.library.pushclient.exception.PlayServicesNotFoundException;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.io.IOException;

abstract class CloudMessagingClient implements RegistrationResultReceiver.Receiver {

  /**
   * Message received on IOException indicating Sender ID is invalid.
   */
  private static final String INVALID_SENDER = "INVALID_SENDER";

  /**
   * The application Context.
   */
  Context mContext;

  /**
   * The Sender ID.
   */
  String mSenderId;

  /**
   * The retrieved registration ID.
   */
  String mRegistrationId;

  /**
   * The initialization callback.
   */
  InitCallback mInitCallback;

  /**
   * The Push delegate.
   */
  PushDelegate mPushDelegate;

  /**
   * Registration IntentService's Receiver.
   */
  ResultReceiver mReceiver;

  /**
   * Name of the Push Preferences for this instance.
   */
  String mPushPreferencesName;

  /**
   * Log Tag
   */
  String mTag;

  CloudMessagingClient() {
    resetReceiver();
  }

  /**
   * Start provider services
   */
  void start() {
    mPushPreferencesName = mContext.getPackageName() + "_" + this.getClass().getSimpleName();
    mTag = this.getClass().getSimpleName();
    enableProviderServices();
    startRegistrationIntentService();
  }

  /**
   * Returns the services associated with the provider.
   *
   * @return The services associated with the provider.
   */
  abstract Class<? extends Service>[] getProviderServices();

  /**
   * Enables services according to the provider.
   */
  private void enableProviderServices() {
    Class<? extends Service>[] services = getProviderServices();
    PackageManager pm = mContext.getPackageManager();
    for (Class<?> service : services) {
      ComponentName component = new ComponentName(mContext, service);
      pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP);
    }
  }

  /**
   * Launches the correct Registration IntentService if app is not registered.
   */
  private void startRegistrationIntentService() {
    if (!hasPlayServices()) {
      doOnCallbackError(new PlayServicesNotFoundException());
      return;
    }
    mRegistrationId = loadRegistrationId();
    if (!TextUtils.isEmpty(mRegistrationId)) {
      doOnCallbackSuccess(false);
      return;
    }
    postStartRegistrationIntentService();
  }

  /**
   * Execute an action after {@link #startRegistrationIntentService}
   */
  abstract void postStartRegistrationIntentService();

  /**
   * Receives any IntentService's result, and handles Registration and Unregistration events.
   *
   * @param resultCode The IntentService's result code.
   * @param resultData The IntentService's extras.
   */
  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
    if (resultData == null) {
      return;
    }
    String origin = resultData.getString(PrefsConstants.SERVICE_ORIGIN);
    if (origin == null) {
      return;
    }
    if (origin.equals(getRegistrationIntentService().getSimpleName())) {
      onReceiveRegistrationResult(resultCode, resultData);
    } else if (origin.equals(getUnregistrationIntentService().getSimpleName())) {
      onReceiveUnregistrationResult(resultCode, resultData);
    }
  }

  /**
   * Returns the unregistration intent service associated with the provider.
   *
   * @return The unregistration intent service associated with the provider.
   */
  abstract Class<? extends IntentService> getRegistrationIntentService();

  /**
   * Receives the Provider's Registration IntentService's result.
   *
   * @param resultCode The IntentService's result code.
   * @param resultData The IntentService's extras.
   */
  private void onReceiveRegistrationResult(int resultCode, Bundle resultData) {
    if (mReceiver == null) {
      return;
    }
    if (resultCode == Activity.RESULT_OK) {
      mRegistrationId = resultData.getString(PrefsConstants.PREF_REG_ID);
      storeRegistrationId(mRegistrationId);
      doOnCallbackSuccess(true);
      return;
    }
    if (resultCode == Activity.RESULT_CANCELED) {
      IOException e = (IOException) resultData
          .getSerializable(PrefsConstants.REGISTRATION_EXCEPTION);
      if (e == null) {
        return;
      }
      doOnCallbackError(e.getMessage().equalsIgnoreCase(INVALID_SENDER)
          ? new InvalidSenderIdException(mSenderId) : new PushClientException(e));
      return;
    }
    if (resultCode == Activity.RESULT_FIRST_USER) {
      Log.w(mTag, "Registration from " + this.getClass().getSimpleName() + " was null or empty.");
    }
  }

  /**
   * Receives the GCM Unregistration IntentService's result.
   *
   * @param resultCode The IntentService's result code.
   * @param resultData The IntentService's extras.
   */
  private void onReceiveUnregistrationResult(int resultCode, Bundle resultData) {
    if (mReceiver != null && resultCode == Activity.RESULT_OK) {
      mRegistrationId = null;
      getPushPreferences().edit().clear().apply();
      Log.d(mTag, "Unregistration successful.");
      return;
    }
    if (mReceiver != null && resultCode == Activity.RESULT_CANCELED) {
      IOException e = (IOException) resultData
          .getSerializable(PrefsConstants.REGISTRATION_EXCEPTION);
      if (e == null) {
        return;
      }
      Log.e(mTag, e.getMessage());
    }
  }

  /**
   * Executes an initialization error callback.
   *
   * @param e The exception for the error callback.
   */
  private void doOnCallbackError(final PushClientException e) {
    if (mInitCallback == null) {
      return;
    }
    final InitCallback callback = mInitCallback;
    Handler handler = new Handler(mContext.getMainLooper());
    handler.post(new Runnable() {

      @Override
      public void run() {
        callback.onError(e);
      }

    });
  }

  /**
   * Executes an initialization success callback.
   *
   * @param hasBeenUpdated Indicates whether the GCM Registration IntentService was used or not.
   */
  private void doOnCallbackSuccess(final boolean hasBeenUpdated) {
    if (mInitCallback == null) {
      return;
    }
    final InitCallback callback = mInitCallback;
    Handler handler = new Handler(mContext.getMainLooper());
    handler.post(new Runnable() {

      @Override
      public void run() {
        callback.onSuccess(mRegistrationId, hasBeenUpdated);
      }
    });
  }

  /**
   * Checks if the device has Play Services installed.
   */
  private boolean hasPlayServices() {
    return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
        == ConnectionResult.SUCCESS;
  }

  /**
   * Checks the app's version code.
   *
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
   *
   * @param registrationId The Registration ID to be stored.
   */
  private void storeRegistrationId(String registrationId) {
    SharedPreferences.Editor editor = getPushPreferences().edit();
    editor.putInt(PrefsConstants.PREF_APP_VERSION, getAppVersion());
    editor.putString(PrefsConstants.PREF_REG_ID, registrationId);
    editor.putString(PrefsConstants.PREF_SENDER_ID, mSenderId);
    editor.apply();
  }

  /**
   * Loads the registration ID from the Shared Preferences.
   *
   * @return the registration ID.
   */
  private String loadRegistrationId() {
    SharedPreferences prefs = getPushPreferences();
    String registrationId = prefs.getString(PrefsConstants.PREF_REG_ID, null);
    int registeredVersion = prefs.getInt(PrefsConstants.PREF_APP_VERSION, -1);
    String senderId = prefs.getString(PrefsConstants.PREF_SENDER_ID, null);
    if (TextUtils.isEmpty(registrationId) || registeredVersion != getAppVersion()
        || senderId != null && !senderId.equals(mSenderId)) {
      return null;
    }
    return registrationId;
  }

  /**
   * Unregisters this device from the provider.
   */
  void unregister() {
    if (mRegistrationId == null) {
      throw new PushClientException("Device is not registered");
    }
    startUnregistrationIntentService();
  }

  /**
   * Returns the unregistration intent service associated with the provider.
   *
   * @return The unregistration intent service associated with the provider.
   */
  abstract Class<? extends IntentService> getUnregistrationIntentService();

  /**
   * Launches the Provider's Unregistration IntentService.
   */
  void startUnregistrationIntentService() {
    Intent intent = new Intent(mContext, getUnregistrationIntentService());
    intent.putExtra(RegistrationResultReceiver.TAG, mReceiver);
    mContext.startService(intent);
  }

  /**
   * Resets the receiver.
   */
  private void resetReceiver() {
    mReceiver = new RegistrationResultReceiver(new Handler());
    ((RegistrationResultReceiver) mReceiver).setReceiver(this);
  }

  /**
   * Retrieves the SharedPreferences used for Push Settings.
   *
   * @return The SharedPreferences used for Push Settings.
   */
  private SharedPreferences getPushPreferences() {
    return mContext.getSharedPreferences(mPushPreferencesName, Context.MODE_PRIVATE);
  }

  /**
   * Gets the receiver.
   *
   * @return The receiver.
   */
  public ResultReceiver getReceiver() {
    if (mReceiver == null) {
      resetReceiver();
    }
    return mReceiver;
  }
}
