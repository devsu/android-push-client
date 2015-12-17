package com.devsu.library.pushclient.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by rion18 on 17-Dec-15.
 */
public class RegistrationResultReceiver extends ResultReceiver {

    public final static String TAG = RegistrationResultReceiver.class.getSimpleName();

    private Receiver mReceiver;

    public RegistrationResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
