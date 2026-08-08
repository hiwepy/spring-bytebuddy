package org.springframework.bytebuddy.bytecode.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests for {@link MvcBound}.
 */
public class MvcBoundTest {

    @Test
    public void shouldCreateWithUidOnly() {
        MvcBound bound = new MvcBound("uid-1");
        assertEquals("uid-1", bound.getUid());
        assertEquals("", bound.getJson());
    }

    @Test
    public void shouldCreateWithUidAndJson() {
        MvcBound bound = new MvcBound("uid-2", "{\"key\":\"value\"}");
        assertEquals("uid-2", bound.getUid());
        assertEquals("{\"key\":\"value\"}", bound.getJson());
    }

    @Test
    public void shouldSetUid() {
        MvcBound bound = new MvcBound("old");
        bound.setUid("new");
        assertEquals("new", bound.getUid());
    }

    @Test
    public void shouldSetJson() {
        MvcBound bound = new MvcBound("uid");
        bound.setJson("{\"a\":1}");
        assertEquals("{\"a\":1}", bound.getJson());
    }

    @Test
    public void shouldHaveDefaultEmptyValues() {
        // Reflective check on default constructor-less path via getUid/getJson
        // after construction with uid only
        MvcBound bound = new MvcBound("test");
        assertNotNull(bound.getUid());
        assertNotNull(bound.getJson());
        assertEquals("", bound.getJson());
    }

}
