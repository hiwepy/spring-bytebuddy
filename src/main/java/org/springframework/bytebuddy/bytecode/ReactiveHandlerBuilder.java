package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A builder for dynamically constructing reactive endpoint handler classes
 * at runtime using ByteBuddy. This builder generates subclasses of
 * {@link EndpointApi} with methods that return {@link Mono} or {@link Flux}
 * reactive types, suitable for use with Spring WebFlux.
 *
 * <p>Usage example:
 * <pre>{@code
 * Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
 *     .autowired("handler", ReactiveHandler.class, true)
 *     .bind(new MvcBound("uid1", "{}"))
 *     .monoMethod("findById", new MvcBound("uid2", "{}"))
 *     .proxy(invocationHandler)
 *     .then();
 *
 * Class<?> clazz = builder.make()
 *     .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
 *     .getLoaded();
 * }</pre>
 *
 * @param <T> the type parameter bounded to {@link EndpointApi}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see EndpointApi
 * @see ReactiveHandler
 * @see EndpointApiBuilder
 */
public class ReactiveHandlerBuilder<T extends EndpointApi> {

	/** The ByteBuddy dynamic type builder for the generated class. */
	protected Builder<? extends EndpointApi> builder = null;

	/** Generator for random strings used in class naming. */
	protected RandomString randomString = new RandomString(8);

	/** The default package prefix for generated endpoint classes. */
	protected static final String PREFIX = "org.springframework.bytebuddy.endpoint.";

	/**
	 * Creates a new {@code ReactiveHandlerBuilder} with default naming strategy.
	 * The generated class name will be prefixed with {@value #PREFIX}
	 * followed by the simple class name and a random suffix.
	 */
	public ReactiveHandlerBuilder() {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {

			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}

		}).subclass(EndpointApi.class);

	}

	/**
	 * Creates a new {@code ReactiveHandlerBuilder} with a custom package
	 * prefix and optional random name suffix.
	 *
	 * @param prefix     the package prefix for the generated class name
	 * @param randomName whether to append a random suffix to the class name
	 */
	public ReactiveHandlerBuilder(String prefix, boolean randomName) {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return prefix + typeDescription.getSimpleName() + (randomName ? ("$" + randomString.nextString()) : "");
			}

		}).subclass(EndpointApi.class);

	}

	/**
	 * Creates a new {@code ReactiveHandlerBuilder} with a fully specified
	 * class name for the generated type.
	 *
	 * @param name the fully qualified name of the generated class in a binary format
	 */
	public ReactiveHandlerBuilder(String name) {
		builder = new ByteBuddy().subclass(EndpointApi.class).name(name);
	}

	/**
	 * Creates a new {@code ReactiveHandlerBuilder} with a custom naming strategy.
	 *
	 * @param namingStrategy the naming strategy to apply when creating a new auxiliary type
	 */
	public ReactiveHandlerBuilder(final NamingStrategy namingStrategy) {
		builder = new ByteBuddy().with(namingStrategy).subclass(EndpointApi.class);
	}

	/**
	 * Adds a protected field with an {@code @Autowired} annotation
	 * to the generated class for dependency injection.
	 *
	 * @param name     the name of the field to define
	 * @param type     the type of the dependency to inject
	 * @param required whether the dependency is required
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> autowired(String name, Class<?> type, boolean required) {
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationUtils.annotAutowired(required));
		return this;
	}

	/**
	 * Adds a protected field with {@code @Autowired} and {@code @Qualifier}
	 * annotations to the generated class for qualified dependency injection.
	 *
	 * @param name      the name of the field to define
	 * @param type      the type of the dependency to inject
	 * @param required  whether the dependency is required
	 * @param qualifier the qualifier name for narrowing the injection candidate
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> autowired( String name, Class<T> type, boolean required, String qualifier) {
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationUtils.annotAutowired(required),
				EndpointApiAnnotationUtils.annotQualifier(qualifier));
		return this;
	}

	/**
	 * Binds data to the generated class by adding a {@code @WebBound}
	 * annotation with the specified uid and JSON payload.
	 *
	 * @param uid the unique identifier for the data binding
	 * @param json the JSON payload to bind
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}

	/**
	 * Binds data to the generated class by adding a {@code @WebBound}
	 * annotation with the specified {@link MvcBound} configuration.
	 *
	 * @param bound the data binding configuration
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> bind(final MvcBound bound) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Defines a new reactive method on the generated class that returns
	 * a {@link Mono} type. The method accepts a {@link ServerRequest}
	 * parameter and throws {@link Throwable}.
	 *
	 * @param name  the method name
	 * @param bound the data binding configuration to annotate the method with
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> monoMethod(final String name, final MvcBound bound) {
		builder = builder.defineMethod(name, Mono.class, Modifier.PUBLIC)
				.withParameter(ServerRequest.class, "request")
				.throwing(Throwable.class)
				.intercept(StubMethod.INSTANCE)
				.annotateMethod(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Defines a new reactive method on the generated class that returns
	 * a {@link Flux} type. The method accepts a {@link ServerRequest}
	 * parameter and throws {@link Throwable}.
	 *
	 * @param name  the method name
	 * @param bound the data binding configuration to annotate the method with
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> fluxMethod(final String name, final MvcBound bound) {
		builder = builder.defineMethod(name, Flux.class, Modifier.PUBLIC)
				.withParameter(ServerRequest.class, "request")
				.throwing(Throwable.class)
				.intercept(StubMethod.INSTANCE)
				.annotateMethod(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Adds an {@link InvocationHandler}-based proxy implementation to all
	 * methods that return {@link Mono} or {@link Flux} reactive types.
	 *
	 * @param handler the invocation handler that implements the method logic
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> proxy(final InvocationHandler handler) {
		builder = builder.method(ElementMatchers.returns(Mono.class)
						.or(ElementMatchers.returns(Flux.class)))
				.intercept(InvocationHandlerAdapter.of(handler));
		return this;
	}


	/**
	 * Adds a method delegation-based implementation to all methods that
	 * return {@link Mono} or {@link Flux} reactive types. Method calls
	 * are delegated to the specified handler class.
	 *
	 * @param handler the class to which method calls are delegated
	 * @return this builder instance for method chaining
	 */
	public ReactiveHandlerBuilder<T> delegate(final Class<?> handler) {
		builder = builder.method(ElementMatchers.returns(Mono.class)
				.or(ElementMatchers.returns(Flux.class)))
				.intercept(MethodDelegation.to(handler));
		return this;
	}

	/**
	 * Returns the underlying ByteBuddy {@link Builder} for further
	 * customization of the generated class.
	 *
	 * @return the ByteBuddy dynamic type builder
	 */
	@SuppressWarnings("unchecked")
	public Builder<T> then() {
		return (Builder<T>) builder;
	}

}
