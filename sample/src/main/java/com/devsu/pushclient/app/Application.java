package com.devsu.pushclient.app;

import com.devsu.library.pushclient.client.PushClient;

/**
 * Created by rion18 on 17-Dec-15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushClient.initialize(this, "380536757239");
    }
}
