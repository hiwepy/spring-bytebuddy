package org.springframework.bytebuddy.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;
import org.springframework.web.bind.annotation.RequestMethod;

import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;

/**
 * Tests for {@link EndpointApiBuilder}.
 */
public class EndpointApiBuilderTest {

    @Test
    public void shouldCreateWithDefaultConstructor() {
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>();
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithPrefixAndRandomName() {
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.test.", true);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithPrefixWithoutRandomName() {
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.test.", false);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithName() {
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.test.MyApi");
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithNamingStrategy() {
        NamingStrategy strategy = new NamingStrategy.AbstractBase() {
            @Override
            protected String name(TypeDescription typeDescription) {
                return "com.test.Custom$" + typeDescription.getSimpleName();
            }
        };
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>(strategy);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldAddApiAnnotation() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .api("Test API", "tag1", "tag2")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation apiAnnot = clazz.getAnnotation(io.swagger.annotations.Api.class);
        assertNotNull("@Api annotation should be present", apiAnnot);
    }

    @Test
    public void shouldAddApiIgnoreAnnotation() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .apiIgnore("Ignored for testing")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation ignoreAnnot = clazz.getAnnotation(springfox.documentation.annotations.ApiIgnore.class);
        assertNotNull("@ApiIgnore annotation should be present", ignoreAnnot);
    }

    @Test
    public void shouldAddControllerAnnotation() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .controller()
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation ctrlAnnot = clazz.getAnnotation(org.springframework.stereotype.Controller.class);
        assertNotNull("@Controller annotation should be present", ctrlAnnot);
    }

    @Test
    public void shouldAddControllerAnnotationWithName() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .controller("myController")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation ctrlAnnot = clazz.getAnnotation(org.springframework.stereotype.Controller.class);
        assertNotNull("@Controller annotation should be present", ctrlAnnot);
    }

    @Test
    public void shouldAddRestControllerAnnotation() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .restController()
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation restAnnot = clazz.getAnnotation(org.springframework.web.bind.annotation.RestController.class);
        assertNotNull("@RestController annotation should be present", restAnnot);
    }

    @Test
    public void shouldAddRestControllerAnnotationWithName() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .restController("myRest")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation restAnnot = clazz.getAnnotation(org.springframework.web.bind.annotation.RestController.class);
        assertNotNull("@RestController annotation should be present", restAnnot);
    }

    @Test
    public void shouldAddRequestMappingWithMvcMapping() throws Exception {
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET);
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .requestMapping(mapping)
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation reqAnnot = clazz.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull("@RequestMapping annotation should be present", reqAnnot);
    }

    @Test
    public void shouldAddRequestMappingWithPath() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .requestMapping("/test")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation reqAnnot = clazz.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull("@RequestMapping annotation should be present", reqAnnot);
    }

    @Test
    public void shouldAddRequestMappingFull() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .requestMapping("test", new String[]{"/api"}, new RequestMethod[]{RequestMethod.GET},
                        new String[]{"p=v"}, new String[]{"H=V"}, new String[]{"application/json"}, new String[]{"text/html"})
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation reqAnnot = clazz.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull("@RequestMapping annotation should be present", reqAnnot);
    }

    @Test
    public void shouldAddAutowiredField() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .autowired("myService", String.class, true)
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Field field = clazz.getDeclaredField("myService");
        assertNotNull(field);
        assertTrue(Modifier.isProtected(field.getModifiers()));
        assertNotNull(field.getAnnotation(org.springframework.beans.factory.annotation.Autowired.class));
    }

    @Test
    public void shouldAddAutowiredFieldWithQualifier() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .autowired("myService", String.class, true, "qualifierName")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Field field = clazz.getDeclaredField("myService");
        assertNotNull(field.getAnnotation(org.springframework.beans.factory.annotation.Autowired.class));
        assertNotNull(field.getAnnotation(org.springframework.beans.factory.annotation.Qualifier.class));
    }

    @Test
    public void shouldBindWithUidAndJson() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .bind("uid-1", "{\"key\":\"val\"}")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation boundAnnot = clazz.getAnnotation(org.springframework.bytebuddy.annotation.WebBound.class);
        assertNotNull("@WebBound annotation should be present", boundAnnot);
    }

    @Test
    public void shouldBindWithMvcBound() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .bind(new MvcBound("uid-2", "{}"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation boundAnnot = clazz.getAnnotation(org.springframework.bytebuddy.annotation.WebBound.class);
        assertNotNull("@WebBound annotation should be present", boundAnnot);
    }

    @Test
    public void shouldAddNewMethod() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod("findById", "/{id}", RequestMethod.GET, "application/json",
                        new MvcBound("1"), new MvcParam<>(String.class, "id"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("findById", String.class);
        assertNotNull(method);
    }

    @Test
    public void shouldAddNewMethodWithReturnClass() throws Exception {
        MvcMethod mvcMethod = new MvcMethod("getData", new String[]{"/data"}, RequestMethod.POST);
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod(String.class, mvcMethod, new MvcBound("2"),
                        new MvcParam<>(String.class, "body", MvcParamFrom.BODY))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("getData", String.class);
        assertNotNull(method);
    }

    @Test
    public void shouldAddNewMethodWithMultiplePaths() throws Exception {
        MvcMethod mvcMethod = new MvcMethod("multi", new String[]{"/a", "/b"},
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod(Object.class, mvcMethod, new MvcBound("3"),
                        new MvcParam<>(String.class, "param1"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("multi", String.class);
        assertNotNull(method);
    }

    @Test
    public void shouldAddNewMethodWithNullReturnClass() throws Exception {
        MvcMethod mvcMethod = new MvcMethod("voidMethod", new String[]{"/void"}, RequestMethod.GET);
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod(null, mvcMethod, new MvcBound("4"),
                        new MvcParam<>(String.class, "param1"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("voidMethod", String.class);
        assertNotNull(method);
    }

    @Test
    public void shouldAddProxy() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> null;
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod("test", "/test", RequestMethod.GET, "application/json",
                        new MvcBound("1"), new MvcParam<>(String.class, "p"))
                .proxy(handler)
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        assertNotNull(clazz);
    }

    @Test
    public void shouldSupportMethodChaining() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .restController()
                .requestMapping("/api")
                .bind("uid", "{}")
                .newMethod("list", "/list", RequestMethod.GET, "application/json", new MvcBound("1"),
                        new MvcParam<>(String.class, "q"))
                .newMethod(String.class,
                        new MvcMethod("get", new String[]{"/get"}, RequestMethod.GET),
                        new MvcBound("2"), new MvcParam<>(Long.class, "id"))
                .autowired("service", String.class, true)
                .proxy((proxy, method, args) -> null)
                .then();
        assertNotNull(builder);
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        assertNotNull(clazz);
    }

    @Test
    public void shouldAddNewMethodWithSingleParam() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod("singleParam", "/single", RequestMethod.POST, "application/json",
                        new MvcBound("1"),
                        new MvcParam<>(String.class, "name"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("singleParam", String.class);
        assertNotNull(method);
    }

    @Test
    public void shouldAddNewMethodWithBodyParam() throws Exception {
        Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
                .newMethod("bodyMethod", "/body", RequestMethod.POST, "application/json",
                        new MvcBound("1"),
                        new MvcParam<>(String.class, "body", MvcParamFrom.BODY))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        assertNotNull(clazz.getMethod("bodyMethod", String.class));
    }

    @Test
    public void shouldCreateBuilderWithPrefixAndNoRandomSuffix() {
        EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.test.", false);
        Builder<EndpointApi> built = builder.then();
        assertNotNull(built);
    }

}
