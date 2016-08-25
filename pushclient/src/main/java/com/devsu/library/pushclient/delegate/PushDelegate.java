package com.devsu.library.pushclient.delegate;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Interface that defines a Push Delegate.
 */
public interface PushDelegate {

    /**
     * Defines the behavior when a push message has arrived via GCM.
     * @param context The context.
     * @param extras The intent extras that were produced by the Broadcast Receiver.
     */
    void handleNotification(Context context, Bundle extras);

    /**
     * Defines the behavior when a push message has arrived via FCM.
     * @param context The context.
     * @param map The map that were produced by the Broadcast Receiver.
     */
    void handleNotification(Context context, Map<String, String> map);
}
