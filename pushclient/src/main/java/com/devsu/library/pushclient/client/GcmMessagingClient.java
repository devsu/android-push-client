package com.devsu.library.pushclient.client;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import com.devsu.library.pushclient.prefs.PrefsConstants;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.devsu.library.pushclient.service.gcm.GcmIdListenerService;
import com.devsu.library.pushclient.service.gcm.GcmPushListenerService;
import com.devsu.library.pushclient.service.gcm.GcmRegistrationIntentService;
import com.devsu.library.pushclient.service.gcm.GcmUnregistrationIntentService;

class GcmMessagingClient extends CloudMessagingClient {

  /**
   * Log TAG.
   */
  private static final String TAG = GcmMessagingClient.class.getSimpleName();

  GcmMessagingClient() {
    super();
  }

  /**
   * Returns GCM Services
   *
   * @return the GCM Services
   */
  @Override
  @SuppressWarnings("unchecked")
  Class<? extends Service>[] getProviderServices() {
    Class<?>[] services = {GcmIdListenerService.class, GcmPushListenerService.class,
        GcmRegistrationIntentService.class, GcmUnregistrationIntentService.class};
    return (Class<? extends Service>[]) services;
  }

  /**
   * Starts the GcmRegistrationIntentService manually.
   */
  @Override
  void postStartRegistrationIntentService() {
    Intent intent = new Intent(mContext, GcmRegistrationIntentService.class);
    intent.putExtra(RegistrationResultReceiver.TAG, mReceiver);
    intent.putExtra(PrefsConstants.PREF_SENDER_ID, mSenderId);
    mContext.startService(intent);
  }

  /**
   * Returns the GCM Registration Intent Service.
   *
   * @return the GCM Registration Intent Service.
   */
  @Override
  Class<? extends IntentService> getRegistrationIntentService() {
    return GcmRegistrationIntentService.class;
  }

  /**
   * Returns the GCM Unregistration Intent Service.
   *
   * @return the GCM Unregistration Intent Service.
   */
  @Override
  Class<? extends IntentService> getUnregistrationIntentService() {
    return GcmUnregistrationIntentService.class;
  }
}
