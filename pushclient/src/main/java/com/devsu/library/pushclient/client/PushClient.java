package com.devsu.library.pushclient.client;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.delegate.SimpleNotificationDelegate;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.service.Provider;

/**
 * PushClient's main class.
 */
public final class PushClient {

  /**
   * The singleton instance.
   */
  private static CloudMessagingClient sInstance;

  /**
   * Log TAG.
   */
  private static final String TAG = PushClient.class.getSimpleName();

  /**
   * Private constructor. Does not allow for instancing this class.
   */
  private PushClient() {
    throw new IllegalStateException("Cannot initialize " + this.getClass().getSimpleName());
  }

  /**
   * Initialization with 2 params.
   *
   * @param context The application context.
   * @param senderId The app's sender ID.
   */
  public static void initialize(Context context, String senderId) {
    initialize(context, Defaults.DEFAULT_PROVIDER, senderId, Defaults.generateInitCallback(),
        Defaults.generateDefaultDelegate(context));
  }

  /**
   * Initialization with 3 params.
   *
   * @param context The application context.
   * @param provider The provider (FCM or GCM).
   * @param senderId The app's sender ID.
   */
  public static void initialize(Context context, Provider provider, String senderId) {
    initialize(context, provider, senderId, Defaults.generateInitCallback(),
        Defaults.generateDefaultDelegate(context));
  }

  /**
   * Initialization with 3 params.
   *
   * @param context The application context.
   * @param senderId The app's sender ID.
   * @param delegate The delegate that will handle how to show push messages.
   */
  public static void initialize(Context context, String senderId, PushDelegate delegate) {
    initialize(context, Defaults.DEFAULT_PROVIDER, senderId, Defaults.generateInitCallback(),
        delegate);
  }

  /**
   * Initialization with 4 params.
   *
   * @param context The application context.
   * @param provider The provider (FCM or GCM).
   * @param senderId The app's sender ID.
   * @param delegate The delegate that will handle how to show push messages.
   */
  public static void initialize(Context context, Provider provider, String senderId,
      PushDelegate delegate) {
    initialize(context, provider, senderId, Defaults.generateInitCallback(), delegate);
  }

  /**
   * Initialization with 3 params.
   *
   * @param context The application context.
   * @param senderId The app's sender ID.
   * @param initCallback The initialization callback.
   */
  public static void initialize(Context context, String senderId, InitCallback initCallback) {
    initialize(context, Defaults.DEFAULT_PROVIDER, senderId, initCallback,
        Defaults.generateDefaultDelegate(context));
  }

  /**
   * Initialization with 4 params.
   *
   * @param context The application context.
   * @param provider The provider (FCM or GCM).
   * @param senderId The app's sender ID.
   * @param initCallback The initialization callback.
   */
  public static void initialize(Context context, Provider provider, String senderId,
      InitCallback initCallback) {
    initialize(context, provider, senderId, initCallback, Defaults.generateDefaultDelegate(context));
  }

  /**
   * Initialization with 5 params.
   *
   * @param context The application context.
   * @param provider The provider (FCM or GCM).
   * @param senderId The app's sender ID.
   * @param delegate The delegate that will handle how to show push messages.
   * @param initCallback The initialization callback.
   */
  public static void initialize(Context context, Provider provider, String senderId,
      InitCallback initCallback, PushDelegate delegate) {
    if (provider == null) {
      throw new PushClientException("Provider cannot be null");
    }
    if (sInstance == null) {
      generateNewInstance(provider);
    }
    if (sInstance.mContext != null) {
      throw new PushClientException("PushClient already initialized. Please initialize once on Application.");
    }
    if (context == null) {
      throw new PushClientException("Context cannot be null.");
    }
    if (TextUtils.isEmpty(senderId)) {
      throw new PushClientException("SenderId cannot be null or empty.");
    }
    if (delegate == null) {
      throw new PushClientException("PushDelegate cannot be null.");
    }
    sInstance.mContext = context.getApplicationContext();
    sInstance.mSenderId = senderId;
    sInstance.mInitCallback = initCallback;
    sInstance.mPushDelegate = delegate;
    sInstance.start();
  }

  private static void generateNewInstance(Provider provider) {
    synchronized (PushClient.class) {
      switch(provider) {
        case FCM: sInstance = new FcmMessagingClient();
          break;
        case GCM: sInstance = new GcmMessagingClient();
          break;
      }
    }
  }

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
   * Returns the GCM ID (or Sender ID)
   *
   * @return The GCM ID (or Sender ID)
   */
  public static String getSenderId() {
    return sInstance.mSenderId;
  }

  /**
   * Returns the delegate that handles the push message display.
   *
   * @return The delegate that handles the push message display.
   */
  public static PushDelegate getDelegate() {
    return sInstance.mPushDelegate;
  }

  /**
   * Returns the registration ID for this instance.
   *
   * @return The registration ID for this instance.
   */
  public static String getRegistrationId() {
    return sInstance.mRegistrationId;
  }

  /**
   * Returns the implementation class of the used client.
   *
   * @return the implementation class of the used client.
   */
  public static Class<? extends CloudMessagingClient> getImplementingClass() {
    return sInstance.getClass();
  }

  /**
   * Default values for this class.
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

    private static Provider DEFAULT_PROVIDER = Provider.FCM;
  }
}