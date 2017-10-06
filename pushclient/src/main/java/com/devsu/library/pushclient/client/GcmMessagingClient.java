package com.devsu.library.pushclient.client;

import android.app.IntentService;
import android.content.Intent;
import com.devsu.library.pushclient.constants.BundleConstants;
import com.devsu.library.pushclient.service.RegistrationResultReceiver;
import com.devsu.library.pushclient.service.gcm.GcmRegistrationIntentService;
import com.devsu.library.pushclient.service.gcm.GcmUnregistrationIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

class GcmMessagingClient extends CloudMessagingClient {

  GcmMessagingClient() {
    super();
  }

  /**
   * Starts the GcmRegistrationIntentService manually.
   */
  @Override
  void postStartRegistrationIntentService() {
    Intent intent = new Intent(mContext, GcmRegistrationIntentService.class);
    intent.putExtra(RegistrationResultReceiver.TAG, mReceiver);
    intent.putExtra(BundleConstants.BUNDLE_SENDER_ID, mSenderId);
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

  /**
   * Loads GCM registration ID if it exists.
   *
   * @return the registration id.
   */
  @Override
  String loadRegistrationId() {
    try {
      InstanceID instanceId = InstanceID.getInstance(mContext);
      return instanceId.getToken(mSenderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    } catch (Exception e) {
      return null;
    }
  }
}
