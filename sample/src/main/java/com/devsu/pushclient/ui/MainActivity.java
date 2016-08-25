package com.devsu.pushclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.devsu.library.pushclient.client.PushClient;
import com.devsu.pushclient.R;

public class MainActivity extends AppCompatActivity {

    private Button unregisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button unregisterButton = (Button) findViewById(R.id.unregister_button);
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushClient.unregister();
            }
        });
    }
}
