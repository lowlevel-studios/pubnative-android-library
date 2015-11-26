package net.pubnative.library;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 11/08/15.
 */
public class SimpleClassTest
{
    @Test
    public void simpleTest()
    {
        SimpleClass simpleMock = spy(SimpleClass.class);
        assertThat(simpleMock.isTest()).isTrue();
    }
}
