package com.devsu.library.pushclient.delegate;

import android.content.Context;
import android.os.Bundle;

/**
 * Interface that defines a Push Delegate.
 */
public interface PushDelegate {

    /**
     * Defines the behavior when a push message has arrived.
     * @param context The context.
     * @param extras The intent extras that were produced by the Broadcast Receiver.
     */
    void handleNotification(Context context, Bundle extras);
}
