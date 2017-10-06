package com.devsu.pushclient.gcm.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.devsu.library.pushclient.client.GcmPushClient;
import com.devsu.pushclient.gcm.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GCM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button unregisterButton = (Button) findViewById(R.id.unregister_button);
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GcmPushClient.unregister();
            }
        });

        final TextView registrationIdText = (TextView) findViewById(R.id.registration_id_text);

        Button registrationIdButton = (Button) findViewById(R.id.registration_id_button);
        registrationIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registrationId = GcmPushClient.getRegistrationId();
                if (TextUtils.isEmpty(registrationId)) {
                    registrationIdText.setText("Not registered to GCM. Please try again.");
                    return;
                }
                Log.i(TAG, registrationId);
                registrationIdText.setText("GCM RegistrationID: " + registrationId);
            }
        });
    }
}
