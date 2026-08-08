package org.springframework.bytebuddy.bytecode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tests for {@link ReactiveHandlerBuilder}.
 */
public class ReactiveHandlerBuilderTest {

    @Test
    public void shouldCreateWithDefaultConstructor() {
        ReactiveHandlerBuilder<EndpointApi> builder = new ReactiveHandlerBuilder<>();
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithPrefixAndRandomName() {
        ReactiveHandlerBuilder<EndpointApi> builder = new ReactiveHandlerBuilder<>("com.test.", true);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithPrefixWithoutRandomName() {
        ReactiveHandlerBuilder<EndpointApi> builder = new ReactiveHandlerBuilder<>("com.test.", false);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithName() {
        ReactiveHandlerBuilder<EndpointApi> builder = new ReactiveHandlerBuilder<>("com.test.MyHandler");
        assertNotNull(builder.then());
    }

    @Test
    public void shouldCreateWithNamingStrategy() {
        NamingStrategy strategy = new NamingStrategy.AbstractBase() {
            @Override
            protected String name(TypeDescription typeDescription) {
                return "com.test.Handler$" + typeDescription.getSimpleName();
            }
        };
        ReactiveHandlerBuilder<EndpointApi> builder = new ReactiveHandlerBuilder<>(strategy);
        assertNotNull(builder.then());
    }

    @Test
    public void shouldAddAutowiredField() throws Exception {
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
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
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .autowired("myService", EndpointApi.class, true, "qualifierName")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Field field = clazz.getDeclaredField("myService");
        assertNotNull(field.getAnnotation(org.springframework.beans.factory.annotation.Autowired.class));
        assertNotNull(field.getAnnotation(org.springframework.beans.factory.annotation.Qualifier.class));
    }

    @Test
    public void shouldBindWithUidAndJson() throws Exception {
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .bind("uid-1", "{\"key\":\"val\"}")
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation boundAnnot = clazz.getAnnotation(org.springframework.bytebuddy.annotation.WebBound.class);
        assertNotNull("@WebBound annotation should be present", boundAnnot);
    }

    @Test
    public void shouldBindWithMvcBound() throws Exception {
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .bind(new MvcBound("uid-2", "{}"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Annotation boundAnnot = clazz.getAnnotation(org.springframework.bytebuddy.annotation.WebBound.class);
        assertNotNull("@WebBound annotation should be present", boundAnnot);
    }

    @Test
    public void shouldAddMonoMethod() throws Exception {
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .monoMethod("findById", new MvcBound("1", "{}"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("findById", ServerRequest.class);
        assertNotNull(method);
        assertTrue(Mono.class.isAssignableFrom(method.getReturnType()));
    }

    @Test
    public void shouldAddFluxMethod() throws Exception {
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .fluxMethod("findAll", new MvcBound("2", "{}"))
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        Method method = clazz.getMethod("findAll", ServerRequest.class);
        assertNotNull(method);
        assertTrue(Flux.class.isAssignableFrom(method.getReturnType()));
    }

    @Test
    public void shouldAddProxyToMonoAndFluxMethods() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> null;
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .monoMethod("findById", new MvcBound("1"))
                .fluxMethod("findAll", new MvcBound("2"))
                .proxy(handler)
                .then();
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        assertNotNull(clazz);
        assertNotNull(clazz.getMethod("findById", ServerRequest.class));
        assertNotNull(clazz.getMethod("findAll", ServerRequest.class));
    }

    @Test
    public void shouldSupportMethodChaining() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> null;
        Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
                .autowired("service", String.class, true)
                .bind("uid", "{}")
                .monoMethod("getMono", new MvcBound("1"))
                .fluxMethod("getFlux", new MvcBound("2"))
                .proxy(handler)
                .then();
        assertNotNull(builder);
        Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();
        assertNotNull(clazz);
    }

}
