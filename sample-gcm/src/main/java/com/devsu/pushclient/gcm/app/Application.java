package com.devsu.pushclient.gcm.app;

import com.devsu.library.pushclient.client.GcmPushClient;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GcmPushClient.with(this, "428325093643").initialize();
  }
}
