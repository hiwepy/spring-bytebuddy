package org.springframework.bytebuddy.bytecode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Tests for {@link EndpointApi}.
 */
public class EndpointApiTest {

    @Test
    public void shouldBeAbstract() {
        assertTrue("EndpointApi should be abstract", Modifier.isAbstract(EndpointApi.class.getModifiers()));
    }

    @Test
    public void shouldNotBeInterface() {
        assertFalse("EndpointApi should not be an interface", EndpointApi.class.isInterface());
    }

    @Test
    public void shouldHaveDefaultPackage() {
        assertNotNull(EndpointApi.class.getPackage());
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

}
