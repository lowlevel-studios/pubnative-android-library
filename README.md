![ScreenShot](PNLogo.png)

[![Circle CI](https://circleci.com/gh/pubnative/pubnative-android-library.svg?style=shield)](https://circleci.com/gh/pubnative/pubnative-android-library) ![Version](https://img.shields.io/badge/version-2.0.3-blue.svg) ![Gradle](https://img.shields.io/badge/gradle-net.pubnative:library:2.0.3-green.svg) ![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)

PubNative is an API-based publisher platform dedicated to native advertising which does not require the integration of an Library.

Through PubNative, publishers can request over 20 parameters to enrich their ads and thereby create any number of combinations for unique and truly native ad units.

#pubnative-android-library

pubnative-android-library is a collection of Open Source tools to implement API based native ads in Android.

##Contents

* [Requirements](#requirements)
* [Install](#install)
 * [Gradle](#install_gradle)
 * [Manual](#install_manual)
* [Usage](#usage)
 *  [Request](#usage_request)
 *  [Track](#usage_track)
 *  [Interstitial](#usage_interstitial)
* [Misc](#misc)
 * [Proguard](#misc_proguard)
 * [Dependencies](#misc_dependencies)
 * [License](#misc_license)
 * [Contributing](#misc_contributing)

<a name="requirements"></a>
# Requirements

* Android API 10
* An App Token provided in PubNative Dashboard.

Add the following permissions to your application `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

Optionally but not necessary to improve user targeting:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

<a name="install"></a>
# Install

<a name="install_gradle"></a>
### Gradle

Add the following line to your module dependencies

```
compile 'net.pubnative:library:2.0.3'
```

<a name="install_manual"></a>
### Manual

Clone the repository and import the `:library` module into your project

<a name="usage"></a>
# Usage

PubNative library is a lean yet complete library that allows you to request and show ads.

Basic integration steps are:

1. [Request](#usage_request): Using `PubnativeRequest`
2. [Track](#usage_track): Using `PubnativeAdModel` builtin `startTracking` and `stopTracking`

<a name="usage_request"></a>
### 1) Request

You will need to create a `PubnativeRequest`, add all the required parameters to it and start it with a listener for the results specifying which endpoint you want to request to. Right now only `NATIVE` is available.

For simplier usage we're providing an interface `PubnativeRequest.Parameters` that contains all valid parameters for a request.

```java
PubnativeRequest request = new PubnativeRequest();
request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, "----YOUR_APP_TOKEN_HERE---");
request.start(CONTEXT, new PubnativeRequest.Listener() {
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {
        // TODO Auto-generated method stub
    }
});
```

##### Testing Mode

For enabling testing mode, call `setTestMode` method at the time of making request

```
request.setTestMode(true);
```

##### Timeout

To set timeout, call `setTimeout` method. Default timeout is 4000ms

```
request.setTimeout(<timeoutinmillis>);
```

<a name="usage_track"></a>
### 2) Track

For confirming impressions, and track clicks, call `ad.startTracking` and provide a valid listener if you want to be in track of what's going on with the ad tracking process

```java
ad.startTracking(visibleView, new PubnativeAdModel.Listener() {

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {
        // Called whenever the impression is confirmed
    }
    
    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {
        // Called when the ad was clicked
    }
    
    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {
        // Called when the leaving the application because the offer is being opened
    }
});
```

If at some point you want to stop the view and click tracking, just call the ad `stopTracking()` method.

<a name="usage_interstitial"></a>
### 4) Interstitial
![ScreenShot](interstitial.png)

#### 4.1 Make a Request

   [Request](#usage_request): Using `PubnativeRequest`
    
#### 4.2 Show fullscreen Ad

   Call `show` method of `PubnativeInterstitial` with `context` & object of `PubnativeAdModel`

```
PubnativeInterstitial.show(CONTEXT, <ad>);
```

<a name="misc"></a>
# Misc

<a name="misc_custom_loading"></a>
### Custom loading

The `PubnativeAdModel` class comes with a default loading view that overlaps the entire current actvity view in order to have the easiest integration, however, you might want to create your own loading view while the click redirection is calculated.

To do this, simply disabe the click loader an develop our own using the following line **before the onPubnativeAdModelClick method ends**
 
```java
ad.setUseClickLoader(false);
```

There are also some people that don't like the idea of having a background click redirection, for this case, you can deactivate using the following method

```java
ad.setUseBackgroundClick(false)
```

<a name="misc_proguard"></a>
### Proguard

If you are using Proguard, add these lines to your Proguard file
```
-keepattributes Signature
-keep class net.pubnative.** { *; }
```

<a name="misc_dependencies"></a>
### Dependencies

This repository holds the following dependencies

* [GSON](https://github.com/google/gson): for json parsing
* [url_driller](https://github.com/pubnative/url-driller): to follow clicks redirections on background
* [advertising_id_client](https://github.com/pubnative/advertising-id-client): to retrieve [android advertising id](http://developer.android.com/intl/es/google/play-services/id.html) without using google ads library

<a name="misc_license"></a>
### License

This code is distributed under the terms and conditions of the MIT license.

<a name="misc_contributing"></a>
### Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.
