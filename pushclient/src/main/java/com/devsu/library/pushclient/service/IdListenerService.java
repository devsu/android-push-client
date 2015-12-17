package com.devsu.library.pushclient.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class IdListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}