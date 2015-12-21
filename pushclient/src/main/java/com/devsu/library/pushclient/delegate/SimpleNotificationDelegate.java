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

public class SimpleNotificationDelegate implements PushDelegate {

    /**
     * The Activity that is opened when clicking the notification.
     */
    private Class<? extends Activity> mDefaultActivity;

    /**
     * The small icon's drawable resource ID.
     */
    private int mSmallIconDrawableResId;

    /**
     * The large icon's drawable resource ID.
     */
    private int mLargeIconDrawableResId;

    /**
     * The title key for the GCM message.
     */
    private String mTitleKey;

    /**
     * The message key for the GCM message.
     */
    private String mMessageKey;

    /**
     * The vibration pattern: Index 0 indicates the delay. Odd indices indicate vibration time. Even indices indicate sleep time.
     */
    private long[] mVibrationPattern;

    /**
     * The lights pattern. It must be of size 2. Index 0 indicates ON time. Index 1 indicates OFF time.
     */
    private int[] mLightsPattern;

    /**
     * If the devices supports it, sets the device's notification light color.
     */
    private int mLightColor;

    /**
     * Notification's autocancel.
     */
    private boolean mAutoCancel;

    /**
     * Notification's sound.
     */
    private Uri mSoundUri;

    /**
     * Single param constructor.
     * @param context The application context.
     */
    public SimpleNotificationDelegate(Context context) {
        setDefaultValues(context);
    }

    /**
     * Sets the default values for all fields.
     * @param context The application context
     */
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

    /**
     * Handles the Notification message. It displays the Push Message using a Notification object.
     * @param context The context.
     * @param extras The intent extras that were produced by the Broadcast Receiver.
     */
    @Override
    public void handleNotification(Context context, Bundle extras) {
        String title = extras.getString(mTitleKey, Defaults.getDefaultTitle(context));
        String message = extras.getString(mMessageKey, null);
        if (message == null) {
            return;
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int currentTimeStamp = (int) System.currentTimeMillis();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(mAutoCancel)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(getContentIntent(context, currentTimeStamp));

        if (mVibrationPattern != null) {
            mBuilder.setVibrate(mVibrationPattern);
        }

        if (mSoundUri != null) {
            mBuilder.setSound(mSoundUri);
        }

        if (mLightsPattern != null) {
            mBuilder.setLights(mLightColor, mLightsPattern[0], mLightsPattern[1]);
        }

        if (mSmallIconDrawableResId != 0) {
            mBuilder.setSmallIcon(mSmallIconDrawableResId);
        }

        if (mLargeIconDrawableResId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mLargeIconDrawableResId);
            mBuilder.setLargeIcon(bitmap);
        }

        mNotificationManager.notify(currentTimeStamp, mBuilder.build());
    }

    /**
     * Retrieves the contentIntent for the default activity.
     * @param context The context.
     * @param currentTimeStamp The time of Notification.
     * @return
     */
    private PendingIntent getContentIntent(Context context, int currentTimeStamp) {
        Intent intent = new Intent();
        intent.setComponent(getComponentName(context));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, currentTimeStamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * The component's name for the default activity. If no default activity is set, it opens the app.
     * @param context The context.
     * @return The component's name for the activity.
     */
    private ComponentName getComponentName(Context context) {
        Class<? extends Activity> activity = this.mDefaultActivity;
        if (activity != null) {
            return new ComponentName(context, activity);
        }
        return Defaults.getDefaultComponent(context);
    }

    /**
     * Sets the default activity.
     * @param defaultActivity The default activity.
     */
    public void setDefaultActivity(Class<? extends Activity> defaultActivity) {
        this.mDefaultActivity = defaultActivity;
    }

    /**
     * Sets the drawable resource ID for the small icon.
     * @param smallIconDrawableResId The drawable resource ID for the small icon.
     */
    public void setSmallIconDrawableResId(int smallIconDrawableResId) {
        this.mSmallIconDrawableResId = smallIconDrawableResId;
    }

    /**
     * Sets the drawable resource ID for the large icon.
     * @param largeIconDrawableResId The drawable resource ID for the large icon.
     */
    public void setLargeIconDrawableResId(int largeIconDrawableResId) {
        this.mLargeIconDrawableResId = largeIconDrawableResId;
    }

    /**
     * Sets the GCM Title key.
     * @param titleKey The GCM Title key.
     */
    public void setTitleKey(String titleKey) {
        this.mTitleKey = titleKey;
    }

    /**
     * Sets the GCM Message key.
     * @param messageKey The GCM Message key.
     */
    public void setMessageKey(String messageKey) {
        this.mMessageKey = messageKey;
    }

    /**
     * Sets the vibration pattern.
     * @param vibrationPattern The vibration pattern.
     */
    public void setVibrationPattern(long[] vibrationPattern) {
        this.mVibrationPattern = vibrationPattern;
    }

    /**
     * Sets the lights pattern when a Push Message is received.
     * @param lightsPattern The lights pattern when a Push Message is received.
     * @throws InvalidLightsPatternException Exception thrown when an invalid array is used.
     *                                      <i>lightsPattern</i> has to be of size 2 and have positive values.
     */
    public void setLightsPattern(int[] lightsPattern) throws InvalidLightsPatternException {
        if (lightsPattern.length != 2 || lightsPattern[0] < 0 || lightsPattern[1] < 0){
            throw new InvalidLightsPatternException();
        }
        this.mLightsPattern = lightsPattern;
    }

    /**
     * Sets the light color
     * @param lightColor The light color.
     */
    public void setLightColor(int lightColor) {
        this.mLightColor = lightColor;
    }

    /**
     * Sets the autocancel parameter for the Notification.
     * @param autoCancel The autocancel parameter for the Notification.
     */
    public void setAutoCancel(boolean autoCancel) {
        this.mAutoCancel = autoCancel;
    }

    /**
     * Sets the sound Uri.
     * @param soundUri The sound Uri.
     */
    public void setSoundUri(Uri soundUri) {
        this.mSoundUri = soundUri;
    }

    /**
     * Class default values.
     */
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