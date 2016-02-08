package net.pubnative.library.request;

import android.content.Context;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class ListenerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testWithValidListenerForSuccess() {

        PubnativeRequest.Listener   listener        = spy(PubnativeRequest.Listener.class);
        PubnativeRequest            request         = spy(new PubnativeRequest(this.applicationContext));

        request.mListener = listener;

        request.invokeOnPubnativeRequestSuccess(mock(ArrayList.class));
        verify(listener, times(1)).onPubnativeRequestSuccess(eq(request), any(List.class));
    }

    @Test
    public void testWithValidListenerForFailure() {

        Exception                   error       = mock(Exception.class);
        PubnativeRequest.Listener   listener    = spy(PubnativeRequest.Listener.class);
        PubnativeRequest            request     = spy(new PubnativeRequest(this.applicationContext));

        request.mListener = listener;

        request.invokeOnPubnativeRequestFailure(error);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), eq(error));
    }

    @Test
    public void testWithNoListenerForSuccess() {

        PubnativeRequest            request         = spy(new PubnativeRequest(this.applicationContext));

        request.mListener = null;

        request.invokeOnPubnativeRequestSuccess(mock(ArrayList.class));
    }

    @Test
    public void testWithNoListenerForFailure() {

        PubnativeRequest            request     = spy(new PubnativeRequest(this.applicationContext));
        Exception                   error       = mock(Exception.class);

        request.mListener = null;

        request.invokeOnPubnativeRequestFailure(error);
    }
}
