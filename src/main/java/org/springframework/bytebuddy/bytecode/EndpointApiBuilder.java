package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils;
import org.springframework.bytebuddy.utils.SwaggerAnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Annotatable;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;

/**
 * A builder for dynamically constructing Spring MVC controller interfaces
 * at runtime using ByteBuddy. This builder generates subclasses of
 * {@link EndpointApi} annotated with the appropriate Spring MVC annotations
 * such as {@code @Controller}, {@code @RestController},
 * {@code @RequestMapping}, and HTTP method-specific mapping annotations.
 *
 * <p>Usage example:
 * <pre>{@code
 * Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
 *     .restController("/api")
 *     .newMethod("findById", "/{id}", RequestMethod.GET,
 *         "application/json", new MvcBound("1"),
 *         new MvcParam<>(Long.class, "id", MvcParamFrom.PATH))
 *     .proxy(invocationHandler)
 *     .then();
 *
 * Class<?> clazz = builder.make()
 *     .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
 *     .getLoaded();
 * }</pre>
 *
 * @param <T> the type parameter bounded to {@link EndpointApi}
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see EndpointApi
 * @see ReactiveHandlerBuilder
 */
public class EndpointApiBuilder<T extends EndpointApi>{

	/** The ByteBuddy dynamic type builder for the generated class. */
	protected Builder<? extends EndpointApi> builder = null;

	/** Generator for random strings used in class naming. */
	protected RandomString randomString = new RandomString(8);

	/** The default package prefix for generated endpoint classes. */
	protected static final String PREFIX = "org.springframework.bytebuddy.endpoint.";

	/**
	 * Creates a new {@code EndpointApiBuilder} with default naming strategy.
	 * The generated class name will be prefixed with {@value #PREFIX}
	 * followed by the simple class name and a random suffix.
	 */
	public EndpointApiBuilder() {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {

			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}

		})
		.subclass(EndpointApi.class);

	}

	/**
	 * Creates a new {@code EndpointApiBuilder} with a custom package prefix
	 * and optional random name suffix.
	 *
	 * @param prefix     the package prefix for the generated class name
	 * @param randomName whether to append a random suffix to the class name
	 */
	public EndpointApiBuilder(String prefix, boolean randomName) {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return prefix + typeDescription.getSimpleName() + (randomName ? ("$" + randomString.nextString()) : "");
			}

		})
		.subclass(EndpointApi.class);

	}

	/**
	 * Creates a new {@code EndpointApiBuilder} with a fully specified
	 * class name for the generated type.
	 *
	 * @param name the fully qualified name of the generated class in a binary format
	 */
	public EndpointApiBuilder(String name) {
		builder = new ByteBuddy().subclass(EndpointApi.class).name(name);
	}

	/**
	 * Creates a new {@code EndpointApiBuilder} with a custom naming strategy.
	 *
	 * @param namingStrategy the naming strategy to apply when creating a new auxiliary type
	 */
	public EndpointApiBuilder(final NamingStrategy namingStrategy) {
		builder = new ByteBuddy().with(namingStrategy).subclass(EndpointApi.class);
	}

	/**
	 * Adds a Swagger {@code @Api} annotation to the generated class.
	 *
	 * @param name the API name for the Swagger documentation
	 * @param tags the tag names for grouping the API endpoints
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> api(String name, String... tags) {
		builder = builder.annotateType(SwaggerAnnotationUtils.annotApi(name,tags));
		return this;
	}

	/**
	 * Adds a Swagger {@code @ApiIgnore} annotation to the generated class,
	 * indicating that it should be excluded from API documentation.
	 *
	 * @param desc the description of why the API is ignored
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> apiIgnore(String desc) {
		builder = builder.annotateType(SwaggerAnnotationUtils.annotApiIgnore(desc));
		return this;
	}

	/**
	 * Adds a {@code @Controller} annotation to the generated class
	 * with no explicit component name.
	 *
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> controller() {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotController(""));
		return this;
	}

	/**
	 * Adds a {@code @Controller} annotation to the generated class
	 * with the specified component name.
	 *
	 * @param name the suggested component name for the controller
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> controller(String name) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotController(name));
		return this;
	}

	/**
	 * Adds a {@code @RestController} annotation to the generated class
	 * with no explicit component name.
	 *
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> restController() {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotRestController(""));
		return this;
	}

	/**
	 * Adds a {@code @RestController} annotation to the generated class
	 * with the specified component name.
	 *
	 * @param name the suggested component name for the REST controller
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> restController(String name) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotRestController(name));
		return this;
	}

	/**
	 * Adds a {@code @RequestMapping} annotation to the generated class
	 * using the specified {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration containing path, method, and other attributes
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> requestMapping(MvcMapping mapping) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotRequestMapping(mapping));
		return this;
	}

	/**
	 * Adds a {@code @RequestMapping} annotation to the generated class
	 * with the specified path.
	 *
	 * @param path the path mapping URI (e.g. {@code "/api/resource"})
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> requestMapping(String path) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotRequestMapping(null, new String[] { path }, null,
				null, null, null, null));
		return this;
	}

	/**
	 * Adds a {@code @RequestMapping} annotation to the generated class
	 * with full mapping configuration.
	 *
	 * @param name      the name attribute value of {@code @RequestMapping}
	 * @param path      the path attribute values of {@code @RequestMapping}
	 * @param method    the HTTP method attribute values of {@code @RequestMapping}
	 * @param params    the params attribute values of {@code @RequestMapping}
	 * @param headers   the headers attribute values of {@code @RequestMapping}
	 * @param consumes  the consumes attribute values of {@code @RequestMapping}
	 * @param produces  the produces attribute values of {@code @RequestMapping}
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> requestMapping(String name, String[] path, RequestMethod[] method,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotRequestMapping(name, path, method,
				params, headers, consumes, produces));
		return this;
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
	public EndpointApiBuilder<T> autowired(String name, Class<?> type, boolean required) {
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
	public EndpointApiBuilder<T> autowired( String name, Class<?> type, boolean required, String qualifier) {
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
	public EndpointApiBuilder<T> bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}

	/**
	 * Binds data to the generated class by adding a {@code @WebBound}
	 * annotation with the specified {@link MvcBound} configuration.
	 *
	 * @param bound the data binding configuration
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> bind(final MvcBound bound) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Defines a new method on the generated class with the specified
	 * HTTP mapping annotation and parameters. The method return type
	 * defaults to {@link Object}.
	 *
	 * @param name     the method name
	 * @param path     the request path (comma-separated for multiple paths)
	 * @param method   the HTTP request method (GET, POST, etc.)
	 * @param consumes the content type consumed by the method
	 * @param bound    the data binding configuration
	 * @param params   the method parameter definitions
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> newMethod(String name, String path, RequestMethod method, String consumes,
			MvcBound bound, MvcParam<?>... params) {
		MvcMethod mvcMethod = new MvcMethod(name, StringUtils.tokenizeToStringArray(path, ","), true, method, null,
				StringUtils.tokenizeToStringArray(consumes, ","));
		AnnotationDescription mapping =  EndpointApiAnnotationUtils.annotMethodMapping(mvcMethod);
		Initial<? extends EndpointApi> initial = builder.defineMethod(name, Object.class, Modifier.PUBLIC);
		Annotatable<? extends EndpointApi> annotatable = null;
		for (int i = 0; i < params.length; i++) {
			annotatable = initial.withParameter(params[i].getType(), params[i].getName())
					.annotateParameter(EndpointApiAnnotationUtils.annotParam(params[i]));
		}
		builder = annotatable.throwing(Throwable.class)
			.intercept(StubMethod.INSTANCE)
			.annotateMethod(mapping, EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Defines a new method on the generated class with a specified
	 * return type, method configuration, and parameters.
	 *
	 * @param rtClass the return type class for the method
	 * @param method  the MVC method configuration
	 * @param bound   the data binding configuration
	 * @param params  the method parameter definitions
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> newMethod(final Class<?> rtClass, final MvcMethod method, final MvcBound bound, MvcParam<?>... params) {

		AnnotationDescription mapping =  EndpointApiAnnotationUtils.annotMethodMapping(method);
		Initial<? extends EndpointApi> initial = builder.defineMethod(method.getName(), rtClass != null ? rtClass : Void.class, Modifier.PUBLIC);
		Annotatable<? extends EndpointApi> annotatable = null;
		for (int i = 0; i < params.length; i++) {
			annotatable = initial.withParameter(params[i].getType(), params[i].getName())
					.annotateParameter(EndpointApiAnnotationUtils.annotParam(params[i]));
		}
		builder = annotatable.throwing(Throwable.class)
			.intercept(StubMethod.INSTANCE)
			.annotateMethod(mapping, EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}

	/**
	 * Adds an {@link InvocationHandler}-based proxy implementation to all
	 * methods annotated with {@code @RequestMapping} or any HTTP method
	 * mapping annotation ({@code @GetMapping}, {@code @PostMapping}, etc.).
	 *
	 * @param handler the invocation handler that implements the method logic
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> proxy(final InvocationHandler handler) {
		builder = builder.method(ElementMatchers.isAnnotatedWith(RequestMapping.class)
						.or(ElementMatchers.isAnnotatedWith(GetMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PostMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PutMapping.class))
						.or(ElementMatchers.isAnnotatedWith(DeleteMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PatchMapping.class)))
				.intercept(InvocationHandlerAdapter.of(handler));
		return this;
	}

	/**
	 * Adds a method delegation-based implementation to all methods annotated
	 * with {@code @RequestMapping} or any HTTP method mapping annotation.
	 * Method calls are delegated to the specified handler class.
	 *
	 * @param handler the class to which method calls are delegated
	 * @return this builder instance for method chaining
	 */
	public EndpointApiBuilder<T> delegate(final Class<?> handler) {
		builder = builder.method(ElementMatchers.isAnnotatedWith(RequestMapping.class)
						.or(ElementMatchers.isAnnotatedWith(GetMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PostMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PutMapping.class))
						.or(ElementMatchers.isAnnotatedWith(DeleteMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PatchMapping.class)))
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
