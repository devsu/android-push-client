# GCM Push Client #

GCM Push Client is an Android library that allows receiving Push Messages, without dealing with the hassle of making your Receiver, GCM Registration Service and all of that fun stuff.

## Overview ###

Using GCM Push Client is very simple. You can import GCM Push Client on your build.gradle file (using jCenter or Maven Central):

```
    // Remember to add your GCM Play Services dependency!
    compile 'com.google.android.gms:play-services-gcm:11.4.2'

    // Or if you're using FCM, import the FCM dependency!
    compile 'com.google.firebase:firebase-messaging:11.4.2'

    compile 'com.devsu:pushclient:1.1.2'
```

Add the com.google.gms.google-services plugin, and then, initialize the library on your Application. That's it!

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ...

        // FCM is selected by default
        FirebaseApp.initialize(this);
        PushClient.initialize(this, "MY_GCM_ID");

        // Or GCM
        PushClient.initialize(this, Provider.GCM, "MY_GCM_ID");
    }
}
```

## InitCallback ###

Customize your client's behavior when retrieving your GCM registration ID.

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ...
        PushClient.initialize(this, "MY_GCM_ID", new InitCallback() {
            @Override
            public void onSuccess(String registrationId, boolean hasBeenUpdated) {
                Log.i(TAG, "This is my registrationId: " + registrationId);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "An error occurred :(");
            }
        });
    }
}
```

## Customizing ###

Using the delegation pattern, GCM Push Client is easy to customize. Simply create your own class that implements `PushDelegate`, or change the default settings on `SimpleNotificationDelegate`.

### Custom Delegate ###

Create your own Delegate Class:

```java
public class ToastDelegate implements PushDelegate {

    private String mMessageKey;
    private Context mContext;

    public ToastDelegate(Context context) {
        this.mContext = context;
    }
    ...

    @Override
    public void handleNotification(Context context, Bundle extras) {
        String message = extras.getString(mMessageKey, null);
        if (message == null) {
            return;
        }
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
```

Initialize it!

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ...
        PushClient.initialize(this, Provider.GCM, "MY_GCM_ID", new ToastDelegate(this));
    }
}
```

### Customizing SimpleNotificationDelegate ###

You can change every aspect of your Push Notification!

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ...
        PushClient.initialize(this, Provider.FCM, "MY_GCM_ID");
        SimpleNotificationDelegate delegate = (SimpleNotificationDelegate) PushClient.getDelegate();

        // Change the large icon
        delegate.setLargeIconDrawableResId(R.drawable.ic_icon_large);

        // Change the small icon
        delegate.setSmallIconDrawableResId(R.drawable.ic_icon_small);

        // Change the Activity that opens on Notification click
        delegate.setDefaultActivity(MainActivity.class);

        // Change title and message keys
        delegate.setTitleKey("TITLE_KEY");
        delegate.setMessageKey("MESSAGE_KEY");

        // Change the vibration pattern
        delegate.setVibrationPattern(new long[] {0, 500, 200, 1000});

        // AND MUCH MORE!
    }
}
```

## Authors ##
Feel free to contact Alvaro López at rion18@hotmail.com!

## License ###

Copyright 2015 Devsu Software

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.