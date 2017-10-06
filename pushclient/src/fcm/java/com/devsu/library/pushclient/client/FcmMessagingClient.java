package com.devsu.library.pushclient.client;

import android.app.IntentService;
import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.IOException;
import com.devsu.library.pushclient.service.FcmRegistrationIntentService;
import com.devsu.library.pushclient.service.FcmUnregistrationIntentService;

class FcmMessagingClient extends CloudMessagingClient {

  FcmMessagingClient() {
    super();
  }

  /**
   * FCM tries to delete instance, in order to refresh a token. This only happens at first install,
   * or when token is deleted)
   */
  @Override
  void postStartRegistrationIntentService() {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        try {
          FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
          Log.e(mTag, "Error deleting instanceId for FCM", e);
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Force a token refresh.
        FirebaseInstanceId.getInstance().getToken();
      }
    }.execute();
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
   *
   * @return the FCM Unregistration Intent Service.
   */
  @Override
  Class<? extends IntentService> getUnregistrationIntentService() {
    return FcmUnregistrationIntentService.class;
  }

  /**
   * Loads FCM registration ID if it exists.
   *
   * @return the registration id.
   */
  @Override
  String loadRegistrationId() {
    try {
      return FirebaseInstanceId.getInstance().getToken();
    } catch (Exception e) {
      return null;
    }
  }
}
