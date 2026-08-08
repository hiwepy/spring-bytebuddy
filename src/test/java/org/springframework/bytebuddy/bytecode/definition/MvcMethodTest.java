package org.springframework.bytebuddy.bytecode.definition;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests for {@link MvcMethod}.
 */
public class MvcMethodTest {

    @Test
    public void shouldCreateWithSingleMethodConstructor() {
        MvcMethod m = new MvcMethod("foo", new String[]{"/foo"}, RequestMethod.GET);
        assertEquals("foo", m.getName());
        assertArrayEquals(new String[]{"/foo"}, m.getPath());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, m.getMethod());
        assertTrue(m.isResponseBody());
    }

    @Test
    public void shouldCreateWithMultipleMethodsConstructor() {
        MvcMethod m = new MvcMethod("bar", new String[]{"/bar"}, new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
        assertEquals("bar", m.getName());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET, RequestMethod.POST}, m.getMethod());
    }

    @Test
    public void shouldCreateWithResponseBodyAndSingleMethod() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, false, RequestMethod.POST);
        assertFalse(m.isResponseBody());
        assertArrayEquals(new RequestMethod[]{RequestMethod.POST}, m.getMethod());
    }

    @Test
    public void shouldCreateWithResponseBodyAndMultipleMethods() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, true, new RequestMethod[]{RequestMethod.PUT, RequestMethod.DELETE});
        assertTrue(m.isResponseBody());
        assertArrayEquals(new RequestMethod[]{RequestMethod.PUT, RequestMethod.DELETE}, m.getMethod());
    }

    @Test
    public void shouldCreateWithSingleMethodAndProduces() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, true, RequestMethod.GET, new String[]{"application/json"});
        assertArrayEquals(new String[]{"application/json"}, m.getProduces());
    }

    @Test
    public void shouldCreateWithMultipleMethodsAndProduces() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, false, new RequestMethod[]{RequestMethod.GET}, new String[]{"text/plain"});
        assertArrayEquals(new String[]{"text/plain"}, m.getProduces());
        assertFalse(m.isResponseBody());
    }

    @Test
    public void shouldCreateWithSingleMethodProducesAndConsumes() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, true, RequestMethod.POST,
                new String[]{"application/json"}, new String[]{"application/xml"});
        assertArrayEquals(new String[]{"application/json"}, m.getProduces());
        assertArrayEquals(new String[]{"application/xml"}, m.getConsumes());
    }

    @Test
    public void shouldCreateWithMultipleMethodsProducesAndConsumes() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, false, new RequestMethod[]{RequestMethod.GET},
                new String[]{"application/json"}, new String[]{"application/xml"});
        assertArrayEquals(new String[]{"application/json"}, m.getProduces());
        assertArrayEquals(new String[]{"application/xml"}, m.getConsumes());
    }

    @Test
    public void shouldCreateWithSingleMethodFullConstructor() {
        MvcMethod m = new MvcMethod("a", new String[]{"/a"}, true, RequestMethod.GET,
                new String[]{"p=v"}, new String[]{"H=V"}, new String[]{"application/json"}, new String[]{"text/html"});
        assertEquals("a", m.getName());
        assertTrue(m.isResponseBody());
        assertArrayEquals(new String[]{"p=v"}, m.getParams());
        assertArrayEquals(new String[]{"H=V"}, m.getHeaders());
        assertArrayEquals(new String[]{"application/json"}, m.getProduces());
        assertArrayEquals(new String[]{"text/html"}, m.getConsumes());
    }

    @Test
    public void shouldCreateWithMultipleMethodsFullConstructor() {
        MvcMethod m = new MvcMethod("b", new String[]{"/b", "/c"}, false,
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST},
                new String[]{"x=1"}, new String[]{"H1=V1"}, new String[]{"text/plain"}, new String[]{"application/xml"});
        assertEquals("b", m.getName());
        assertArrayEquals(new String[]{"/b", "/c"}, m.getPath());
        assertFalse(m.isResponseBody());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET, RequestMethod.POST}, m.getMethod());
        assertArrayEquals(new String[]{"x=1"}, m.getParams());
        assertArrayEquals(new String[]{"H1=V1"}, m.getHeaders());
        assertArrayEquals(new String[]{"text/plain"}, m.getProduces());
        assertArrayEquals(new String[]{"application/xml"}, m.getConsumes());
    }

    @Test
    public void shouldUseDefaultMethodsWhenNullPassed() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, false, (RequestMethod[]) null);
        // Should fall back to RequestMethod.values()
        assertArrayEquals(RequestMethod.values(), m.getMethod());
    }

    @Test
    public void shouldUseDefaultMethodsWhenEmptyArrayPassed() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, false, new RequestMethod[]{});
        assertArrayEquals(RequestMethod.values(), m.getMethod());
    }

    @Test
    public void shouldUseEmptyArraysWhenNullParamsPassed() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, true,
                RequestMethod.GET, null, null, null, null);
        assertArrayEquals(new String[]{}, m.getParams());
        assertArrayEquals(new String[]{}, m.getHeaders());
        assertArrayEquals(new String[]{}, m.getProduces());
        assertArrayEquals(new String[]{}, m.getConsumes());
    }

    @Test
    public void shouldUseEmptyArraysWhenEmptyParamsPassed() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, true,
                RequestMethod.GET, new String[]{}, new String[]{}, new String[]{}, new String[]{});
        assertArrayEquals(new String[]{}, m.getParams());
        assertArrayEquals(new String[]{}, m.getHeaders());
        assertArrayEquals(new String[]{}, m.getProduces());
        assertArrayEquals(new String[]{}, m.getConsumes());
    }

    @Test
    public void shouldSetAndGetMethod() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setMethod(new RequestMethod[]{RequestMethod.DELETE});
        assertArrayEquals(new RequestMethod[]{RequestMethod.DELETE}, m.getMethod());
    }

    @Test
    public void shouldSetAndGetParams() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setParams(new String[]{"key=val"});
        assertArrayEquals(new String[]{"key=val"}, m.getParams());
    }

    @Test
    public void shouldSetAndGetHeaders() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setHeaders(new String[]{"Accept=text/html"});
        assertArrayEquals(new String[]{"Accept=text/html"}, m.getHeaders());
    }

    @Test
    public void shouldSetAndGetConsumes() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setConsumes(new String[]{"application/json"});
        assertArrayEquals(new String[]{"application/json"}, m.getConsumes());
    }

    @Test
    public void shouldSetAndGetProduces() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setProduces(new String[]{"application/xml"});
        assertArrayEquals(new String[]{"application/xml"}, m.getProduces());
    }

    @Test
    public void shouldSetAndGetResponseBody() {
        MvcMethod m = new MvcMethod("a", new String[]{"/"}, RequestMethod.GET);
        m.setResponseBody(true);
        assertTrue(m.isResponseBody());
        m.setResponseBody(false);
        assertFalse(m.isResponseBody());
    }

    @Test
    public void shouldReturnPathFromGetPath() {
        String[] paths = new String[]{"/x", "/y"};
        MvcMethod m = new MvcMethod("a", paths, RequestMethod.GET);
        assertArrayEquals(paths, m.getPath());
    }

    @Test
    public void shouldReturnNameFromGetName() {
        MvcMethod m = new MvcMethod("myMethod", new String[]{"/"}, RequestMethod.GET);
        assertEquals("myMethod", m.getName());
    }

}
