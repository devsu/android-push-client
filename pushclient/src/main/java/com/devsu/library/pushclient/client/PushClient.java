package com.devsu.library.pushclient.client;

import android.os.ResultReceiver;
import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.exception.PushClientException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

/**
 * PushClient's main class.
 */
class PushClient {

  /**
   * The singleton instance.
   */
  static CloudMessagingClient sInstance;

  /**
   * Log TAG.
   */
  static final String TAG = PushClient.class.getSimpleName();

  /**
   * Unregisters this device from the Provider.
   */
  public static void unregister() {
    if (sInstance == null) {
      throw new PushClientException("PushClient has not been initialized.");
    }
    sInstance.unregister();
  }

  /**
   * Sets the delegate that handles the push message display.
   *
   * @param pushDelegate The delegate that handles the push message display.
   */
  public static void setDelegate(PushDelegate pushDelegate) {
    if (sInstance == null) {
      throw new PushClientException(
          "PushClient has not been initialized. Please initialize once on Application.");
    }
    if (pushDelegate == null) {
      throw new PushClientException("PushDelegate cannot be null.");
    }
    sInstance.mPushDelegate = pushDelegate;
  }

  /**
   * Returns the Sender ID
   *
   * @return the Sender ID
   */
  public static String getSenderId() {
    return sInstance.mSenderId;
  }

  /**
   * Returns the delegate that handles the push message display.
   *
   * @return the delegate that handles the push message display.
   */
  public static PushDelegate getDelegate() {
    return sInstance.mPushDelegate;
  }

  /**
   * Returns the receiver.
   *
   * @return the receiver.
   */
  public static ResultReceiver getReceiver() {
    return sInstance.mReceiver;
  }

  /**
   * Returns the registration ID for this instance.
   *
   * @return The registration ID for this instance.
   */
  public static String getRegistrationId() {
    return sInstance.mRegistrationId;
  }
}