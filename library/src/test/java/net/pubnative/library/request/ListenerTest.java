package net.pubnative.library.request;

import android.content.Context;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.models.APIRequestResponseModel;
import net.pubnative.library.models.PubnativeAdModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

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
        APIRequestResponseModel     reponseModel    = mock(APIRequestResponseModel.class);

        request.mListener = listener;

        reponseModel.ads = new ArrayList<PubnativeAdModel>();

        request.apiRequestListener.invokeOnResponse("{'status': 'ok', 'ads': []}");
        verify(listener, times(1)).onPubnativeRequestSuccess(eq(request), eq(reponseModel.ads));
    }

    @Test
    public void testWithValidListenerForFailure() {

        Exception                   error       = mock(Exception.class);
        PubnativeRequest.Listener   listener    = spy(PubnativeRequest.Listener.class);
        PubnativeRequest            request     = spy(new PubnativeRequest(this.applicationContext));

        request.mListener = listener;

        request.apiRequestListener.invokeOnErrorResponse(error);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), eq(error));
    }

    @Test
    public void testWithNoListenerForSuccess() {

        PubnativeRequest.Listener   listener        = spy(PubnativeRequest.Listener.class);
        PubnativeRequest            request         = spy(new PubnativeRequest(this.applicationContext));
        Exception                   error           = mock(Exception.class);
        APIRequestResponseModel     reponseModel    = mock(APIRequestResponseModel.class);

        request.mListener = null;
        reponseModel.ads = new ArrayList<PubnativeAdModel>();

        request.apiRequestListener.invokeOnResponse("{'status': 'ok', 'ads': []}");
        verify(listener, times(0)).onPubnativeRequestFailed(eq(request), eq(error));
    }

    @Test
    public void testWithNoListenerForFailure() {

        PubnativeRequest.Listener   listener    = spy(PubnativeRequest.Listener.class);
        PubnativeRequest            request     = spy(new PubnativeRequest(this.applicationContext));
        Exception                   error       = mock(Exception.class);

        request.mListener = null;

        request.apiRequestListener.invokeOnErrorResponse(error);
        verify(listener, times(0)).onPubnativeRequestFailed(eq(request), eq(error));
    }
}
