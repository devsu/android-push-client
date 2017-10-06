package com.devsu.library.pushclient.client;

import android.content.Context;
import android.os.ResultReceiver;
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

  public static ProviderPushClientBuilder with(Context context, Provider provider,
      String senderId) {
    return new ProviderPushClientBuilder(context, provider, senderId);
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

  /**
   * Returns the implementation class of the used client.
   *
   * @return the implementation class of the used client.
   */
  public static Provider getProvider() {
    if (sInstance instanceof FcmMessagingClient) {
      return Provider.FCM;
    }
    if (sInstance instanceof GcmMessagingClient) {
      return Provider.GCM;
    }
    throw new IllegalStateException("Unknown provider found on getProvider()");
  }

  public static class ProviderPushClientBuilder {

    private Context context;
    private Provider provider;
    private String senderId;
    private InitCallback initCallback;
    private PushDelegate pushDelegate;

    ProviderPushClientBuilder(Context context, Provider provider, String senderId) {
      if (provider == null) {
        throw new PushClientException("Provider cannot be null.");
      }
      if (context == null) {
        throw new PushClientException("Application Context cannot be null.");
      }
      if (TextUtils.isEmpty(senderId)) {
        throw new PushClientException("SenderId cannot be null or empty.");
      }
      this.provider = provider;
      this.context = context.getApplicationContext();
      this.senderId = senderId;
      this.initCallback = Defaults.generateInitCallback();
      this.pushDelegate = Defaults.generateDefaultDelegate(context);
    }

    public ProviderPushClientBuilder initCallback(InitCallback initCallback) {
      this.initCallback = initCallback;
      return this;
    }

    public ProviderPushClientBuilder pushDelegate(PushDelegate pushDelegate) {
      if (pushDelegate == null) {
        throw new PushClientException("PushDelegate cannot be null.");
      }
      this.pushDelegate = pushDelegate;
      return this;
    }

    public void initialize() {
      CloudMessagingClient instance;
      synchronized (PushClient.class) {
        switch (provider) {
          case FCM:
            instance = new FcmMessagingClient();
            break;
          case GCM:
            instance = new GcmMessagingClient();
            break;
          default:
            throw new PushClientException("Unknown Provider used.");
        }
        instance.mContext = context;
        instance.mSenderId = senderId;
        instance.mInitCallback = initCallback;
        instance.mPushDelegate = pushDelegate;
        sInstance = instance;
        sInstance.start();
      }
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
    }
  }
}