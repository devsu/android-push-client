package com.devsu.library.pushclient.service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * The Receiver for the registration IntentService's result.
 */
@SuppressLint("ParcelCreator")
public class RegistrationResultReceiver extends ResultReceiver {

    /**
     * Log TAG.
     */
    public static final String TAG = RegistrationResultReceiver.class.getSimpleName();

    /**
     * Internal receiver.
     */
    private Receiver mReceiver;

    /**
     * Single param constructor
     * @param handler The handler.
     */
    public RegistrationResultReceiver(Handler handler) {
        super(handler);
    }

    /**
     * Sets the receiver.
     * @param receiver The receiver.
     */
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    /**
     * Executes the receiver's callback.
     * @param resultCode The result code.
     * @param resultData The result extras.
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    /**
     * Interface that allows receiving an IntentService.
     */
    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
