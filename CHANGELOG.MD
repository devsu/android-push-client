# Change Log

## [1.1.0](https://github.com/devsu/gcm-push-client/releases/tag/1.1.0) (2015-12-22)

First release of the GCM Push Client.

+ Initial commit.
+ Added Javadocs.
+ Added Readme and License
+ Added Bintray plugin for gradle.
+ Fixed GCM.unregister() method deprecation by creating an UnregisterService.
+ Fixed GCM.MESSAGE_TYPE_MESSAGE constant deprecation by creating a class that extends GcmListenerService.

## [1.1.1](https://github.com/devsu/gcm-push-client/releases/tag/1.1.1) (2016-08-24)

+ Added FCM support.

## [2.0.0](https://github.com/devsu/gcm-push-client/releases/tag/2.0.0) (2017-10-05)

+ Fixed a seemingly random NullPointerException.
+ Updated dependencies.
+ Added getRegistrationId method.
+ Added color as a delegate property.
+ Added a sample for FCM and a sample for GCM.
+ Separated AndroidManifests for FCM and GCM, no longer registering services on runtime.
+ Fixed a FCM bug where on first install we would get a double registration event happening.
+ Separated FCM and GCM as classifiers for the dependency.