package com.devsu.library.pushclient.client;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.devsu.library.pushclient.constants.BundleConstants;
import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.exception.InvalidSenderIdException;
import com.devsu.library.pushclient.exception.PlayServicesNotFoundException;
import com.devsu.library.pushclient.exception.PushClientException;
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
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        mPushPreferencesName = mContext.getPackageName() + "_" + this.getClass().getSimpleName();
        mTag = this.getClass().getSimpleName();
        startRegistrationIntentService();
      }
    });
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
    String origin = resultData.getString(BundleConstants.BUNDLE_SERVICE_ORIGIN);
    if (TextUtils.isEmpty(origin)) {
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
      String registrationId = resultData.getString(BundleConstants.BUNDLE_REGISTRATION_ID);
      if (TextUtils.isEmpty(registrationId) || !registrationId.equals(mRegistrationId)) {
        mRegistrationId = resultData.getString(BundleConstants.BUNDLE_REGISTRATION_ID);
        doOnCallbackSuccess(true);
      }
      return;
    }
    if (resultCode == Activity.RESULT_CANCELED) {
      IOException e = (IOException) resultData
          .getSerializable(BundleConstants.BUNDLE_REGISTRATION_EXCEPTION);
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
      Log.d(mTag, "Unregistration successful.");
      return;
    }
    if (mReceiver != null && resultCode == Activity.RESULT_CANCELED) {
      IOException e = (IOException) resultData
          .getSerializable(BundleConstants.BUNDLE_REGISTRATION_EXCEPTION);
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

  abstract String loadRegistrationId();

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
