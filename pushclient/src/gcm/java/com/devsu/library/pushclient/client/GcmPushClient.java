package com.devsu.library.pushclient.client;

import android.content.Context;
import com.devsu.library.pushclient.service.Provider;

/**
 * PushClient's main class for GCM.
 */
public class GcmPushClient extends PushClient {

  /**
   * Private constructor. Does not allow for instancing this class.
   */
  private GcmPushClient() {
    throw new IllegalStateException("Cannot initialize " + this.getClass().getSimpleName());
  }

  public static ProviderPushClientBuilder with(Context context, String senderId) {
    return new GcmPushClientBuilder(context, senderId);
  }

  public static Provider getProvider() {
    return Provider.GCM;
  }

}
