package org.springframework.bytebuddy.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMethod;

import net.bytebuddy.description.annotation.AnnotationDescription;

/**
 * Tests for {@link EndpointApiAnnotationUtils}.
 */
public class EndpointApiAnnotationUtilsTest {

    @Test
    public void shouldCreateAnnotConfiguration() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotConfiguration("myConfig");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotConfigurationWithEmptyName() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotConfiguration("");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotConfigurationWithNullName() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotConfiguration(null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotQualifier() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotQualifier("myQualifier");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotQualifierWithEmpty() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotQualifier("");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotQualifierWithNull() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotQualifier(null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotAutowiredRequired() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotAutowired(true);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotAutowiredNotRequired() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotAutowired(false);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotBean() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotBean(
                new String[]{"bean1"}, "init", "destroy", true);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotBeanWithEmptyNames() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotBean(
                new String[]{}, "", "", false);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotBeanWithNullNames() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotBean(
                null, null, null, true);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotLazyTrue() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotLazy(true);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotLazyFalse() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotLazy(false);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotController() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotController("myCtrl");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotControllerWithEmptyName() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotController("");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRestController() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRestController("myRest");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRestControllerWithEmptyName() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRestController("");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestMappingFromMapping() {
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestMapping(mapping);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestMappingFull() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestMapping(
                "test", new String[]{"/api"}, new RequestMethod[]{RequestMethod.GET},
                new String[]{"p=v"}, new String[]{"H=V"}, new String[]{"application/json"}, new String[]{"text/html"});
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotGetMapping() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotGetMapping(
                "test", new String[]{"/api"}, new String[]{}, new String[]{}, new String[]{}, new String[]{});
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPostMapping() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPostMapping(
                "test", new String[]{"/api"}, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPutMapping() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPutMapping(
                "test", new String[]{"/api"}, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotDeleteMapping() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotDeleteMapping(
                "test", new String[]{"/api"}, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPatchMapping() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPatchMapping(
                "test", new String[]{"/api"}, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotBound() {
        MvcBound bound = new MvcBound("uid", "{\"a\":1}");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotBound(bound);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotBoundWithEmptyValues() {
        MvcBound bound = new MvcBound("", "");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotBound(bound);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSingleGet() {
        MvcMethod method = new MvcMethod("get", new String[]{"/get"}, RequestMethod.GET);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSinglePost() {
        MvcMethod method = new MvcMethod("post", new String[]{"/post"}, RequestMethod.POST);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSinglePut() {
        MvcMethod method = new MvcMethod("put", new String[]{"/put"}, RequestMethod.PUT);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSingleDelete() {
        MvcMethod method = new MvcMethod("del", new String[]{"/del"}, RequestMethod.DELETE);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSinglePatch() {
        MvcMethod method = new MvcMethod("patch", new String[]{"/patch"}, RequestMethod.PATCH);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithSingleHead() {
        MvcMethod method = new MvcMethod("head", new String[]{"/head"}, RequestMethod.HEAD);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMethodMappingWithMultipleMethods() {
        MvcMethod method = new MvcMethod("multi", new String[]{"/multi"},
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMethodMapping(method);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotCookieValue() {
        MvcParam<String> param = new MvcParam<>(String.class, "cookieVal");
        param.setFrom(MvcParamFrom.COOKIE);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotCookieValue(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotCookieValueWithDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "cookieVal", MvcParamFrom.COOKIE, "defaultCookie");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotCookieValue(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMatrixVariable() {
        MvcParam<String> param = new MvcParam<>(String.class, "matrixVal");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMatrixVariable(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotMatrixVariableWithDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "matrixVal", "def");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotMatrixVariable(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPathVariable() {
        MvcParam<String> param = new MvcParam<>(String.class, "pathVar");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPathVariable(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestAttribute() {
        MvcParam<String> param = new MvcParam<>(String.class, "attr");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestAttribute(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestBody() {
        MvcParam<String> param = new MvcParam<>(String.class, "body");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestBody(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestBodyNotRequired() {
        MvcParam<String> param = new MvcParam<>(String.class, "body");
        param.setRequired(false);
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestBody(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestHeader() {
        MvcParam<String> param = new MvcParam<>(String.class, "header");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestHeader(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestHeaderWithDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "header", "defaultHeader");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestHeader(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestPart() {
        MvcParam<String> param = new MvcParam<>(String.class, "part");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestPart(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestParam() {
        MvcParam<String> param = new MvcParam<>(String.class, "param");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestParamWithDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "param", "defaultVal");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotParamFromCookie() {
        MvcParam<String> param = new MvcParam<>(String.class, "c", MvcParamFrom.COOKIE);
        param.setFrom(MvcParamFrom.COOKIE);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromMatrix() {
        MvcParam<String> param = new MvcParam<>(String.class, "m");
        param.setFrom(MvcParamFrom.MATRIX);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromPath() {
        MvcParam<String> param = new MvcParam<>(String.class, "p");
        param.setFrom(MvcParamFrom.PATH);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromAttr() {
        MvcParam<String> param = new MvcParam<>(String.class, "a");
        param.setFrom(MvcParamFrom.ATTR);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromBody() {
        MvcParam<String> param = new MvcParam<>(String.class, "b");
        param.setFrom(MvcParamFrom.BODY);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromHeader() {
        MvcParam<String> param = new MvcParam<>(String.class, "h");
        param.setFrom(MvcParamFrom.HEADER);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromParam() {
        MvcParam<String> param = new MvcParam<>(String.class, "q");
        param.setFrom(MvcParamFrom.PARAM);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamFromPart() {
        MvcParam<String> param = new MvcParam<>(String.class, "pt");
        param.setFrom(MvcParamFrom.PART);
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotParamDefaultCase() {
        // Default case in switch falls to annotRequestParam
        MvcParam<String> param = new MvcParam<>(String.class, "def");
        assertNotNull(EndpointApiAnnotationUtils.annotParam(param));
    }

    @Test
    public void shouldCreateAnnotValid() {
        MvcParam<String> param = new MvcParam<>(String.class, "validParam");
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotValid(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotRequestMappingWithNullValues() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotRequestMapping(
                null, null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotGetMappingWithNullArrays() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotGetMapping(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPostMappingWithNullArrays() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPostMapping(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPutMappingWithNullArrays() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPutMapping(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotDeleteMappingWithNullArrays() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotDeleteMapping(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotPatchMappingWithNullArrays() {
        AnnotationDescription desc = EndpointApiAnnotationUtils.annotPatchMapping(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

}
