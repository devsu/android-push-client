package com.devsu.library.pushclient.client;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.devsu.library.pushclient.delegate.PushDelegate;
import com.devsu.library.pushclient.delegate.SimpleNotificationDelegate;
import com.devsu.library.pushclient.exception.PushClientException;
import com.devsu.library.pushclient.service.Provider;

public abstract class ProviderPushClientBuilder {

  protected Context context;
  protected Provider provider;
  protected String senderId;
  protected InitCallback initCallback;
  protected PushDelegate pushDelegate;

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

  /**
   * Initializing depends on the Provider.
   */
  public abstract void initialize();

  /**
   * Default values for this class.
   */
  private static class Defaults {

    private static InitCallback generateInitCallback() {
      return new InitCallback() {
        @Override
        public void onSuccess(String registrationId, boolean hasUpdated) {
          Log.d(PushClient.TAG, "RegistrationID: " + registrationId);
        }

        @Override
        public void onError(Throwable throwable) {
          Log.e(PushClient.TAG, throwable.getMessage());
        }
      };
    }

    private static PushDelegate generateDefaultDelegate(Context context) {
      return new SimpleNotificationDelegate(context);
    }
  }
}