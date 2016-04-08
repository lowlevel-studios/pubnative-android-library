// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.request.model;

import android.content.Context;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testImpressionCallbackWithValidListener() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnImpression(adView);
        verify(listener, times(1)).onPubnativeAdModelImpression(eq(model), eq(adView));
    }

    @Test
    public void testClickCallbackWithValidListener() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnClick(adView);
        verify(listener, times(1)).onPubnativeAdModelClick(eq(model), eq(adView));
    }

    @Test
    public void testOpenOfferCallbackWithValidListener() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        model.mListener = listener;
        model.invokeOnOpenOffer();
        verify(listener, times(1)).onPubnativeAdModelOpenOffer(eq(model));
    }

    @Test
    public void testCallbacksWithNullListener() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        model.mListener = null;
        model.invokeOnOpenOffer();
        model.invokeOnClick(null);
        model.invokeOnImpression(null);
    }

    @Test
    public void testStartTrackingWithClickableView() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        View clickableView = spy(new View(applicationContext));
        model.link = "http://www.google.com";
        model.startTracking(adView, clickableView, listener);
        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithClickableViewForValidClickListener() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        View clickableView = spy(new View(applicationContext));
        model.startTracking(adView, clickableView, listener);
        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithoutClickableView() {

        PubnativeAPIV3AdModel model = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3AdModel.Listener listener = mock(PubnativeAPIV3AdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        model.link = "http://www.google.com";
        model.startTracking(adView, listener);
        verify(adView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }
}
