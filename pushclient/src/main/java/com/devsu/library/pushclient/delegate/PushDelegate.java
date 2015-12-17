package com.devsu.library.pushclient.delegate;

import android.content.Context;
import android.content.Intent;

public interface PushDelegate {

    void onReceive(Context context, Intent intent);

}
