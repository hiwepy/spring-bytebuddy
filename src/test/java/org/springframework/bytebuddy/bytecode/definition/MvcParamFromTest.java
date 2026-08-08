package org.springframework.bytebuddy.bytecode.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests for {@link MvcParamFrom}.
 */
public class MvcParamFromTest {

    @Test
    public void shouldContainAllExpectedValues() {
        MvcParamFrom[] values = MvcParamFrom.values();
        assertEquals(8, values.length);
    }

    @Test
    public void shouldResolveCookie() {
        assertEquals(MvcParamFrom.COOKIE, MvcParamFrom.valueOf("COOKIE"));
    }

    @Test
    public void shouldResolveMatrix() {
        assertEquals(MvcParamFrom.MATRIX, MvcParamFrom.valueOf("MATRIX"));
    }

    @Test
    public void shouldResolvePath() {
        assertEquals(MvcParamFrom.PATH, MvcParamFrom.valueOf("PATH"));
    }

    @Test
    public void shouldResolveAttr() {
        assertEquals(MvcParamFrom.ATTR, MvcParamFrom.valueOf("ATTR"));
    }

    @Test
    public void shouldResolveBody() {
        assertEquals(MvcParamFrom.BODY, MvcParamFrom.valueOf("BODY"));
    }

    @Test
    public void shouldResolveHeader() {
        assertEquals(MvcParamFrom.HEADER, MvcParamFrom.valueOf("HEADER"));
    }

    @Test
    public void shouldResolveParam() {
        assertEquals(MvcParamFrom.PARAM, MvcParamFrom.valueOf("PARAM"));
    }

    @Test
    public void shouldResolvePart() {
        assertEquals(MvcParamFrom.PART, MvcParamFrom.valueOf("PART"));
    }

    @Test
    public void shouldHaveNonNullEnumConstants() {
        for (MvcParamFrom value : MvcParamFrom.values()) {
            assertNotNull(value);
        }
    }

}
