package com.devsu.library.pushclient.client;

import android.content.Context;

class GcmPushClientBuilder extends ProviderPushClientBuilder {

  GcmPushClientBuilder(Context context, String senderId) {
    super(context, GcmPushClient.getProvider(), senderId);
  }

  @Override
  public void initialize() {
    CloudMessagingClient instance;
    synchronized (GcmPushClient.class) {
      instance = new GcmMessagingClient();
      instance.mContext = context;
      instance.mSenderId = senderId;
      instance.mInitCallback = initCallback;
      instance.mPushDelegate = pushDelegate;
      GcmPushClient.sInstance = instance;
      GcmPushClient.sInstance.start();
    }
  }
}
