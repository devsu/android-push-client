package com.devsu.pushclient.app;

import com.devsu.library.pushclient.client.InitCallback;
import com.devsu.library.pushclient.client.PushClient;

/**
 * Created by rion18 on 17-Dec-15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushClient.initialize(this, "380536757239", new InitCallback() {
            @Override
            public void onSuccess(String registrationId, boolean hasUpdated) {
                System.out.println("This is the registrationId: "+ registrationId);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("This is an error: " + throwable.getMessage());
            }
        });
    }
}
