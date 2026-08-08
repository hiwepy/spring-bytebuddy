package org.springframework.bytebuddy.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;

import net.bytebuddy.description.annotation.AnnotationDescription;

/**
 * Tests for {@link SwaggerAnnotationUtils}.
 */
public class SwaggerAnnotationUtilsTest {

    @Test
    public void shouldCreateAnnotApiWithFullParameters() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi(
                "Test API", new String[]{"tag1"}, "application/json", "application/xml",
                "http", new String[]{"auth1"});
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiWithEmptyParameters() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi(
                "", new String[]{}, "", "", "", new String[]{});
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiWithNullParameters() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi(
                null, null, null, null, null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiWithNameAndTags() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi("Test", "tag1", "tag2");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiWithNameOnly() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi("Test");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiWithEmptyNameAndEmptyTags() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApi("", (String[]) null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiIgnore() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiIgnore("Hidden endpoint");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiIgnoreWithEmpty() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiIgnore("");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiIgnoreWithNull() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiIgnore(null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiOperation() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiOperation("Summary", "Notes");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiOperationWithEmpty() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiOperation("", "");
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiOperationWithNull() {
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiOperation(null, null);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParams() {
        MvcParam<String> p1 = new MvcParam<>(String.class, "name");
        MvcParam<Integer> p2 = new MvcParam<>(Integer.class, "age");
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParams(p1, p2);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamsWithSingleParam() {
        MvcParam<String> p = new MvcParam<>(String.class, "id");
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParams(p);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithPath() {
        MvcParam<String> param = new MvcParam<>(String.class, "id");
        param.setFrom(MvcParamFrom.PATH);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithBody() {
        MvcParam<String> param = new MvcParam<>(String.class, "body");
        param.setFrom(MvcParamFrom.BODY);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithHeader() {
        MvcParam<String> param = new MvcParam<>(String.class, "token");
        param.setFrom(MvcParamFrom.HEADER);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithParam() {
        MvcParam<String> param = new MvcParam<>(String.class, "query");
        param.setFrom(MvcParamFrom.PARAM);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithCookie() {
        MvcParam<String> param = new MvcParam<>(String.class, "cookie");
        param.setFrom(MvcParamFrom.COOKIE);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithMatrix() {
        MvcParam<String> param = new MvcParam<>(String.class, "matrix");
        param.setFrom(MvcParamFrom.MATRIX);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithAttr() {
        MvcParam<String> param = new MvcParam<>(String.class, "attr");
        param.setFrom(MvcParamFrom.ATTR);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithPart() {
        MvcParam<String> param = new MvcParam<>(String.class, "file");
        param.setFrom(MvcParamFrom.PART);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithDefault() {
        // Default case (PARAM) is already the default from MvcParamFrom
        MvcParam<String> param = new MvcParam<>(String.class, "q");
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamWithDefaultAndDef() {
        MvcParam<String> param = new MvcParam<>(String.class, "q", "defaultVal");
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

    @Test
    public void shouldCreateAnnotApiImplicitParamNotRequired() {
        MvcParam<String> param = new MvcParam<>(String.class, "opt");
        param.setRequired(false);
        AnnotationDescription desc = SwaggerAnnotationUtils.annotApiImplicitParam(param);
        assertNotNull(desc);
    }

}
