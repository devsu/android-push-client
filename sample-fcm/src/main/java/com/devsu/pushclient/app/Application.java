package com.devsu.pushclient.app;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.library.pushclient.service.Provider;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    PushClient.initialize(this, Provider.FCM, "380536757239");
  }
}
