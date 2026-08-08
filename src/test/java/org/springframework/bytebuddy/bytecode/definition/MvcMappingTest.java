package org.springframework.bytebuddy.bytecode.definition;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests for {@link MvcMapping}.
 */
public class MvcMappingTest {

    @Test
    public void shouldCreateWithArrayConstructor() {
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET, RequestMethod.POST);
        assertArrayEquals(new String[]{"/api"}, mapping.getPath());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET, RequestMethod.POST}, mapping.getMethod());
    }

    @Test
    public void shouldCreateWithFullConstructor() {
        MvcMapping mapping = new MvcMapping(
                "testMapping",
                new String[]{"/api/test"},
                new RequestMethod[]{RequestMethod.GET},
                new String[]{"param1=val1"},
                new String[]{"Header=val"},
                new String[]{"application/json"},
                new String[]{"application/json"}
        );
        assertEquals("testMapping", mapping.getName());
        assertArrayEquals(new String[]{"/api/test"}, mapping.getPath());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, mapping.getMethod());
        assertArrayEquals(new String[]{"param1=val1"}, mapping.getParams());
        assertArrayEquals(new String[]{"Header=val"}, mapping.getHeaders());
        assertArrayEquals(new String[]{"application/json"}, mapping.getConsumes());
        assertArrayEquals(new String[]{"application/json"}, mapping.getProduces());
    }

    @Test
    public void shouldSetAndGetName() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setName("newName");
        assertEquals("newName", mapping.getName());
    }

    @Test
    public void shouldSetAndGetMethod() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setMethod(new RequestMethod[]{RequestMethod.POST});
        assertArrayEquals(new RequestMethod[]{RequestMethod.POST}, mapping.getMethod());
    }

    @Test
    public void shouldSetAndGetParams() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setParams(new String[]{"a=b"});
        assertArrayEquals(new String[]{"a=b"}, mapping.getParams());
    }

    @Test
    public void shouldSetAndGetHeaders() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setHeaders(new String[]{"X-Test=val"});
        assertArrayEquals(new String[]{"X-Test=val"}, mapping.getHeaders());
    }

    @Test
    public void shouldSetAndGetConsumes() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setConsumes(new String[]{"text/plain"});
        assertArrayEquals(new String[]{"text/plain"}, mapping.getConsumes());
    }

    @Test
    public void shouldSetAndGetProduces() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setProduces(new String[]{"text/html"});
        assertArrayEquals(new String[]{"text/html"}, mapping.getProduces());
    }

    @Test
    public void shouldHaveDefaultProducesMediaTypeAll() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        assertArrayEquals(new String[]{MediaType.ALL_VALUE}, mapping.getProduces());
    }

    @Test
    public void shouldReturnPathFromGetPath() {
        String[] paths = new String[]{"/a", "/b"};
        MvcMapping mapping = new MvcMapping(paths, RequestMethod.GET);
        assertArrayEquals(paths, mapping.getPath());
    }

}
