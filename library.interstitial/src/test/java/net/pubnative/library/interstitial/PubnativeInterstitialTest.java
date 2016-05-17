package net.pubnative.library.interstitial;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class PubnativeInterstitialTest {

    public static final String FAKE_APP_TOKEN = "1234567890";

    Context mMockContext;
    LayoutInflater mMockInflator;
    RelativeLayout mMockView;

    @Before
    public void setUp() {
        mMockContext = mock(Activity.class);
        mMockInflator = mock(LayoutInflater.class);
        mMockView = mock(RelativeLayout.class);
        PowerMockito.mockStatic(TextUtils.class);

        when(mMockContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mMockInflator);
        when(mMockInflator.inflate(any(Integer.class), any(ViewGroup.class))).thenReturn(mMockView);
    }

    @Test
    public void createInterstitial_withNullContext_returnsObject() {

        PubnativeInterstitial interstitial = new PubnativeInterstitial(null, null);

        Assertions.assertThat(interstitial).isNotNull();

    }

    @Test
    public void loadInterstitial_withNullContext_invokeLoadFail() {

        PubnativeInterstitial interstitial = new PubnativeInterstitial(null, FAKE_APP_TOKEN);
        PubnativeInterstitial spyInterstitial = spy(interstitial);

        final Exception ex = new Exception();
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(spyInterstitial).invokeLoadFail(any(Exception.class));
        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadInterstitial_withEmptyAppToken_invokeLoadFail() {

        when(TextUtils.isEmpty(any(CharSequence.class))).thenReturn(true);

        PubnativeInterstitial interstitial = new PubnativeInterstitial(mMockContext, "");
        PubnativeInterstitial spyInterstitial = spy(interstitial);

        final Exception ex = new Exception();
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(spyInterstitial).invokeLoadFail(any(Exception.class));

        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadIterstitial_whenReady_invokeLoadFinish() {

        Context context = mock(Activity.class);
        LayoutInflater inflater = mock(LayoutInflater.class);
        RelativeLayout view = mock(RelativeLayout.class);

        when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater);
        when(inflater.inflate(any(Integer.class), any(ViewGroup.class))).thenReturn(view);

        PubnativeInterstitial interstitial = new PubnativeInterstitial(context, FAKE_APP_TOKEN);
        PubnativeInterstitial spyInterstitial = spy(interstitial);
        when(spyInterstitial.isReady()).thenReturn(true);

        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFinish();

    }

}
