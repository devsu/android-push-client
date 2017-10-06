package com.devsu.pushclient.fcm.app;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.service.Provider;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    PushClient.with(this, Provider.FCM, "1015075478581").initialize();
  }
}
