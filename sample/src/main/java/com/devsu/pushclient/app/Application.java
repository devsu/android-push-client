package com.devsu.pushclient.app;

import com.devsu.library.pushclient.client.PushClient;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushClient.initialize(this, "380536757239");
    }
}
