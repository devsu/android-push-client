package com.devsu.pushclient.fcm.app;

import com.devsu.library.pushclient.client.FcmPushClient;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    FcmPushClient.with(this, "1015075478581").initialize();
  }
}
