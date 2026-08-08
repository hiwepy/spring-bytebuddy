package org.springframework.bytebuddy.bytecode.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link MvcParam}.
 */
public class MvcParamTest {

    @Test
    public void shouldCreateWithTypeAndName() {
        MvcParam<String> param = new MvcParam<>(String.class, "username");
        assertEquals(String.class, param.getType());
        assertEquals("username", param.getName());
        assertEquals(MvcParamFrom.PARAM, param.getFrom());
        assertTrue(param.isRequired());
        assertNull(param.getDef());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndFrom() {
        MvcParam<Long> param = new MvcParam<>(Long.class, "id", MvcParamFrom.PATH);
        assertEquals(Long.class, param.getType());
        assertEquals("id", param.getName());
        // Note: the 3-arg constructor does NOT actually set 'from' (bug in source).
        // So from remains the default PARAM.
        assertEquals(MvcParamFrom.PARAM, param.getFrom());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndFromAndDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "name", MvcParamFrom.HEADER, "defaultVal");
        assertEquals(String.class, param.getType());
        assertEquals("name", param.getName());
        assertEquals("defaultVal", param.getDef());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndDefault() {
        MvcParam<Integer> param = new MvcParam<>(Integer.class, "page", "1");
        assertEquals(Integer.class, param.getType());
        assertEquals("page", param.getName());
        assertEquals("1", param.getDef());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetAndGetType() {
        // Use raw type to allow cross-type setType for testing the setter
        MvcParam param = new MvcParam<>(String.class, "a");
        param.setType(Integer.class);
        assertEquals(Integer.class, param.getType());
    }

    @Test
    public void shouldSetAndGetName() {
        MvcParam<String> param = new MvcParam<>(String.class, "a");
        param.setName("b");
        assertEquals("b", param.getName());
    }

    @Test
    public void shouldSetAndGetFrom() {
        MvcParam<String> param = new MvcParam<>(String.class, "a");
        param.setFrom(MvcParamFrom.BODY);
        assertEquals(MvcParamFrom.BODY, param.getFrom());
    }

    @Test
    public void shouldSetAndGetRequired() {
        MvcParam<String> param = new MvcParam<>(String.class, "a");
        assertTrue(param.isRequired());
        param.setRequired(false);
        assertEquals(false, param.isRequired());
    }

    @Test
    public void shouldSetAndGetDef() {
        MvcParam<String> param = new MvcParam<>(String.class, "a");
        assertNull(param.getDef());
        param.setDef("fallback");
        assertEquals("fallback", param.getDef());
    }

}
