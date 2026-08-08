package org.springframework.bytebuddy.bytecode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Tests for {@link ReactiveHandler}.
 */
public class ReactiveHandlerTest {

    @Test
    public void shouldBeAbstract() {
        assertTrue("ReactiveHandler should be abstract",
                Modifier.isAbstract(ReactiveHandler.class.getModifiers()));
    }

    @Test
    public void shouldNotBeInterface() {
        assertNotNull(ReactiveHandler.class);
        assertTrue("Should be a class", !ReactiveHandler.class.isInterface());
    }

}
