package com.devsu.library.pushclient.delegate;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.devsu.library.pushclient.exception.InvalidLightsPatternException;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SimpleNotificationDelegate implements PushDelegate {

    private Class<? extends Activity> mDefaultActivity;
    private int mSmallIconDrawableResId;
    private int mLargeIconDrawableResId;
    private String mTitleKey;
    private String mMessageKey;
    private long[] mVibrationPattern;
    private int[] mLightsPattern;
    private int mLightColor;
    private boolean mAutoCancel;
    private Uri mSoundUri;

    public SimpleNotificationDelegate(Context context) {
        setDefaultValues(context);
    }

    private void setDefaultValues(Context context) {
        mSmallIconDrawableResId = Defaults.getDefaultIconResId(context);
        mLargeIconDrawableResId = Defaults.getDefaultIconResId(context);
        mTitleKey = Defaults.TITLE_KEY;
        mMessageKey = Defaults.MESSAGE_KEY;
        mVibrationPattern = Defaults.VIBRATION_PATTERN;
        mLightsPattern = Defaults.LIGHTS_PATTERN;
        mLightColor = Defaults.LIGHT_COLOR;
        mAutoCancel = Defaults.AUTO_CANCEL;
        mSoundUri = Defaults.SOUND_DEFAULT;
    }

    @Override
    public void onReceive(Context context, Intent notification) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(notification);
        Bundle extras = notification.getExtras();

        if ((!extras.isEmpty()) && (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))) {
            showNotification(context, extras);
        }
    }

    public void showNotification(Context context, Bundle extras) {
        String title = extras.getString(mTitleKey, Defaults.getDefaultTitle(context));
        String message = extras.getString(mMessageKey, null);
        if (message == null) {
            return;
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mLargeIconDrawableResId);

        int currentTimeStamp = (int)System.currentTimeMillis();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(mAutoCancel)
                .setVibrate(mVibrationPattern)
                .setSound(mSoundUri)
                .setLights(mLightColor, mLightsPattern[0], mLightsPattern[1])
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(mSmallIconDrawableResId)
                .setLargeIcon(bitmap)
                .setContentIntent(getContentIntent(context, currentTimeStamp));

        mNotificationManager.notify(currentTimeStamp, mBuilder.build());
    }

    private PendingIntent getContentIntent(Context context, int currentTimeStamp) {
        Intent intent = new Intent();
        intent.setComponent(getComponentName(context));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, currentTimeStamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ComponentName getComponentName(Context context) {
        Class<? extends Activity> activity = this.mDefaultActivity;
        if (activity != null) {
            return new ComponentName(context, activity);
        }
        return Defaults.getDefaultComponent(context);
    }

    public void setDefaultActivity(Class<? extends Activity> defaultActivity) {
        this.mDefaultActivity = defaultActivity;
    }

    public void setSmallIconDrawableResId(int smallIconDrawableResId) {
        this.mSmallIconDrawableResId = smallIconDrawableResId;
    }

    public void setLargeIconDrawableResId(int largeIconDrawableResId) {
        this.mLargeIconDrawableResId = largeIconDrawableResId;
    }

    public void setTitleKey(String titleKey) {
        this.mTitleKey = titleKey;
    }

    public void setMessageKey(String messageKey) {
        this.mMessageKey = messageKey;
    }

    public void setVibrationPattern(long[] vibrationPattern) {
        this.mVibrationPattern = vibrationPattern;
    }

    public void setLightsPattern(int[] lightsPattern) throws InvalidLightsPatternException {
        if (lightsPattern.length != 2 || lightsPattern[0] < 0 || lightsPattern[1] < 0){
            throw new InvalidLightsPatternException();
        }
        this.mLightsPattern = lightsPattern;
    }

    public void setLightColor(int lightColor) {
        this.mLightColor = lightColor;
    }

    public void setAutoCancel(boolean autoCancel) {
        this.mAutoCancel = autoCancel;
    }

    public void setSoundUri(Uri soundUri) {
        this.mSoundUri = soundUri;
    }

    private static class Defaults {
        private static final String TITLE_KEY = "title";
        private static final String MESSAGE_KEY = "message";
        private static final long[] VIBRATION_PATTERN = {0, 500, 200, 500, 200, 500};
        private static final int[] LIGHTS_PATTERN = {800, 200};
        private static int LIGHT_COLOR = Color.WHITE;
        private static boolean AUTO_CANCEL = true;
        private static Uri SOUND_DEFAULT = Settings.System.DEFAULT_NOTIFICATION_URI;

        private static int getDefaultIconResId(Context context) {
            return context.getApplicationInfo().icon;
        }

        private static String getDefaultTitle(Context context) {
            return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
        }

        private static ComponentName getDefaultComponent(Context context) {
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            return intent.getComponent();
        }
    }
}