package com.devsu.library.pushclient.client;

import android.app.IntentService;
import android.app.Service;
import com.devsu.library.pushclient.service.fcm.FcmIdListenerService;
import com.devsu.library.pushclient.service.fcm.FcmPushListenerService;
import com.devsu.library.pushclient.service.fcm.FcmRegistrationIntentService;
import com.devsu.library.pushclient.service.fcm.FcmUnregistrationIntentService;

class FcmMessagingClient extends CloudMessagingClient {

  /**
   * Log TAG.
   */
  private static final String TAG = FcmMessagingClient.class.getSimpleName();

  FcmMessagingClient() {
    super();
  }

  /**
   * Returns FCM Services
   *
   * @return the FCM Services
   */
  @Override
  @SuppressWarnings("unchecked")
  Class<? extends Service>[] getProviderServices() {
    Class<?>[] services = {FcmIdListenerService.class, FcmPushListenerService.class,
        FcmRegistrationIntentService.class, FcmUnregistrationIntentService.class};
    return (Class<? extends Service>[]) services;
  }

  /**
   * FCM Doesn't do anything after {@link #startRegistrationIntentService()}.
   */
  @Override
  void postStartRegistrationIntentService() {
    // DO NOTHING
  }

  /**
   * Returns the FCM Registration Intent Service.
   *
   * @return the FCM Registration Intent Service.
   */
  @Override
  Class<? extends IntentService> getRegistrationIntentService() {
    return FcmRegistrationIntentService.class;
  }

  /**
   * Returns the FCM Unregistration Intent Service.
   */
  @Override
  Class<? extends IntentService> getUnregistrationIntentService() {
    return FcmUnregistrationIntentService.class;
  }
}
