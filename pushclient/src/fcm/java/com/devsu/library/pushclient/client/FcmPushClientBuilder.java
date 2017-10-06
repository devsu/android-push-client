package com.devsu.library.pushclient.client;

import android.content.Context;

class FcmPushClientBuilder extends ProviderPushClientBuilder {

  FcmPushClientBuilder(Context context, String senderId) {
    super(context, FcmPushClient.getProvider(), senderId);
  }

  @Override
  public void initialize() {
    CloudMessagingClient instance;
    synchronized (FcmPushClientBuilder.class) {
      instance = new FcmMessagingClient();
      instance.mContext = context;
      instance.mSenderId = senderId;
      instance.mInitCallback = initCallback;
      instance.mPushDelegate = pushDelegate;
      FcmPushClient.sInstance = instance;
      FcmPushClient.sInstance.start();
    }
  }
}
