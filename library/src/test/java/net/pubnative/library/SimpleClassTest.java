package net.pubnative.library;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 11/08/15.
 */
public class SimpleClassTest
{
    @Test
    public void simpleTest()
    {
        SimpleClass simpleSpy = spy(SimpleClass.class);
        assertThat(simpleSpy.isTest()).isTrue();
    }

    @Test
    public void simpleTest2()
    {
        SimpleClass simpleMock = mock(SimpleClass.class);
        assertThat(simpleMock.isTest()).isFalse();
    }
}
