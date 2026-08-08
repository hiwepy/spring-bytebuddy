package org.springframework.bytebuddy.bytecode.definition;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Configuration object describing a Spring MVC handler method for
 * dynamic code generation. Encapsulates the method name, request path,
 * HTTP methods, content negotiation, parameter/header constraints,
 * and whether a {@code @ResponseBody} annotation should be applied.
 *
 * <p>Used by {@link org.springframework.bytebuddy.bytecode.EndpointApiBuilder}
 * to construct annotated methods on dynamically generated controller classes.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see MvcMapping
 * @see MvcParam
 * @see org.springframework.bytebuddy.bytecode.EndpointApiBuilder#newMethod(Class, MvcMethod, MvcBound, MvcParam...)
 */
public class MvcMethod {

	/** The Java method name. */
	private final String name;

	/**
	 * The path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do"). At the
	 * method level, relative paths (e.g. "edit.do") are supported within
	 * the primary mapping expressed at the type level. Path mapping URIs may
	 * contain placeholders (e.g. "/${connect}")
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this primary mapping,
	 * narrowing it for a specific handler method.
	 *
	 * @see org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	 * @since 4.2
	 */
	private final String[] path;
	/**
	 * The HTTP request methods to map to, narrowing the primary mapping: GET, POST,
	 * HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this HTTP method
	 * restriction (i.e. the type-level restriction gets checked before the handler
	 * method is even resolved).
	 */
	private RequestMethod[] method = RequestMethod.values();

	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * <p>
	 * Same format for any environment: a sequence of "myParam=myValue" style
	 * expressions, with a request only mapped if each such parameter is found to
	 * have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "myParam!=myValue". "myParam" style expressions are also supported,
	 * with such parameters having to be present in the request (allowed to have any
	 * value). Finally, "!myParam" style expressions indicate that the specified
	 * parameter is <i>not</i> supposed to be present in the request.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this parameter
	 * restriction (i.e. the type-level restriction gets checked before the handler
	 * method is even resolved).
	 * <p>
	 * Parameter mappings are considered as restrictions that are enforced at the
	 * type level. The primary path mapping (i.e. the specified URI value) still has
	 * to uniquely identify the target handler, with parameter mappings simply
	 * expressing preconditions for invoking the handler.
	 */
	private String[] params = new String[] {};

	/**
	 * The headers of the mapped request, narrowing the primary mapping.
	 * <p>
	 * Same format for any environment: a sequence of "My-Header=myValue" style
	 * expressions, with a request only mapped if each such header is found to have
	 * the given value. Expressions can be negated by using the "!=" operator, as in
	 * "My-Header!=myValue". "My-Header" style expressions are also supported, with
	 * such headers having to be present in the request (allowed to have any value).
	 * Finally, "!My-Header" style expressions indicate that the specified header is
	 * <i>not</i> supposed to be present in the request.
	 * <p>
	 * Also supports media type wildcards (*), for headers such as Accept and
	 * Content-Type. For instance,
	 *
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 *
	 * will match requests with a Content-Type of "text/html", "text/plain", etc.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this header restriction
	 * (i.e. the type-level restriction gets checked before the handler method is
	 * even resolved).
	 *
	 * @see org.springframework.http.MediaType
	 */
	private String[] headers = new String[] {};

	/**
	 * The consumable media types of the mapped request, narrowing the primary
	 * mapping.
	 * <p>
	 * The format is a single media type or a sequence of media types, with a
	 * request only mapped if the {@code Content-Type} matches one of these media
	 * types. Examples:
	 *
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * </pre>
	 *
	 * Expressions can be negated by using the "!" operator, as in "!text/plain",
	 * which matches all requests with a {@code Content-Type} other than
	 * "text/plain".
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings override this consumes
	 * restriction.
	 *
	 * @see org.springframework.http.MediaType
	 * @see jakarta.servlet.http.HttpServletRequest#getContentType()
	 */
	private String[] consumes = new String[] {};

	/**
	 * The producible media types of the mapped request, narrowing the primary
	 * mapping.
	 * <p>
	 * The format is a single media type or a sequence of media types, with a
	 * request only mapped if the {@code Accept} matches one of these media types.
	 * Examples:
	 *
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = "application/json; charset=UTF-8"
	 * </pre>
	 * <p>
	 * It affects the actual content type written, for example to produce a JSON
	 * response with UTF-8 encoding, {@code "application/json; charset=UTF-8"}
	 * should be used.
	 * <p>
	 * Expressions can be negated by using the "!" operator, as in "!text/plain",
	 * which matches all requests with a {@code Accept} other than "text/plain".
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings override this produces
	 * restriction.
	 *
	 * @see org.springframework.http.MediaType
	 */
	private String[] produces = new String[] {};

	/**
	 * Annotation that indicates a method return value should be bound to the web
	 * response body. Supported for annotated handler methods in Servlet
	 * environments.
	 */
	private boolean responseBody = false;

	/**
	 * Creates a new {@code MvcMethod} with a single HTTP method.
	 *
	 * @param name   the method name
	 * @param path   the request path URIs
	 * @param method the HTTP request method
	 */
	public MvcMethod(String name, String[] path, RequestMethod method) {
		this(name, path, true, method, null, null, null, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with multiple HTTP methods.
	 *
	 * @param name    the method name
	 * @param path    the request path URIs
	 * @param methods the HTTP request methods
	 */
	public MvcMethod(String name, String[] path, RequestMethod[] methods) {
		this(name, path, true, methods, null, null, null, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with a single HTTP method and
	 * explicit {@code @ResponseBody} control.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param method       the HTTP request method
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method) {
		this(name, path, responseBody, method, null, null, null, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with multiple HTTP methods and
	 * explicit {@code @ResponseBody} control.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param methods      the HTTP request methods
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods) {
		this(name, path, responseBody, methods, null, null, null, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with a single HTTP method,
	 * {@code @ResponseBody} control, and produced media types.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param method       the HTTP request method
	 * @param produces     the producible media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces) {
		this(name, path, responseBody, method, null, null, produces, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with multiple HTTP methods,
	 * {@code @ResponseBody} control, and produced media types.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param methods      the HTTP request methods
	 * @param produces     the producible media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces) {
		this(name, path, responseBody, methods, null, null, produces, null);
	}

	/**
	 * Creates a new {@code MvcMethod} with a single HTTP method,
	 * {@code @ResponseBody} control, and produced/consumed media types.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param method       the HTTP request method
	 * @param produces     the producible media types
	 * @param consumes     the consumable media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces, String[] consumes) {
		this(name, path, responseBody, method, null, null, produces, consumes);
	}

	/**
	 * Creates a new {@code MvcMethod} with multiple HTTP methods,
	 * {@code @ResponseBody} control, and produced/consumed media types.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param methods      the HTTP request methods
	 * @param produces     the producible media types
	 * @param consumes     the consumable media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces, String[] consumes) {
		this(name, path, responseBody, methods, null, null, produces, consumes);
	}

	/**
	 * Creates a new {@code MvcMethod} with a single HTTP method and
	 * full configuration.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param method       the HTTP request method
	 * @param params       the required request parameters
	 * @param headers      the required request headers
	 * @param produces     the producible media types
	 * @param consumes     the consumable media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] params, String[] headers,
			String[] produces, String[] consumes) {
		this(name, path, responseBody, new RequestMethod[] { method }, params, headers, produces, consumes);
	}

	/**
	 * Creates a new {@code MvcMethod} with multiple HTTP methods and
	 * full configuration.
	 *
	 * @param name         the method name
	 * @param path         the request path URIs
	 * @param responseBody whether to add {@code @ResponseBody} annotation
	 * @param methods      the HTTP request methods
	 * @param params       the required request parameters
	 * @param headers      the required request headers
	 * @param produces     the producible media types
	 * @param consumes     the consumable media types
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] params, String[] headers,
			String[] produces, String[] consumes) {
		this.name = name;
		this.path = path;
		this.responseBody = responseBody;
		this.method = ArrayUtils.isNotEmpty(methods) ? methods : RequestMethod.values();
		this.params = ArrayUtils.isNotEmpty(params) ? params : new String[] {};
		this.headers = ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
		this.produces = ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
		this.consumes = ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
	}

	/**
	 * Returns the HTTP request methods for this method mapping.
	 *
	 * @return the array of request methods
	 */
	public RequestMethod[] getMethod() {
		return method;
	}

	/**
	 * Sets the HTTP request methods for this method mapping.
	 *
	 * @param method the array of request methods to set
	 */
	public void setMethod(RequestMethod[] method) {
		this.method = method;
	}

	/**
	 * Returns the required request parameters.
	 *
	 * @return the array of parameter expressions
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * Sets the required request parameters.
	 *
	 * @param params the array of parameter expressions to set
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	/**
	 * Returns the required request headers.
	 *
	 * @return the array of header expressions
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Sets the required request headers.
	 *
	 * @param headers the array of header expressions to set
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * Returns the consumable media types.
	 *
	 * @return the array of consumed media type strings
	 */
	public String[] getConsumes() {
		return consumes;
	}

	/**
	 * Sets the consumable media types.
	 *
	 * @param consumes the array of consumed media type strings to set
	 */
	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	/**
	 * Returns the producible media types.
	 *
	 * @return the array of produced media type strings
	 */
	public String[] getProduces() {
		return produces;
	}

	/**
	 * Sets the producible media types.
	 *
	 * @param produces the array of produced media type strings to set
	 */
	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	/**
	 * Returns whether {@code @ResponseBody} should be added.
	 *
	 * @return {@code true} if the method should have {@code @ResponseBody}
	 */
	public boolean isResponseBody() {
		return responseBody;
	}

	/**
	 * Sets whether {@code @ResponseBody} should be added.
	 *
	 * @param responseBody {@code true} to add {@code @ResponseBody}
	 */
	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	/**
	 * Returns the Java method name.
	 *
	 * @return the method name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the path mapping URIs.
	 *
	 * @return the array of path strings
	 */
	public String[] getPath() {
		return path;
	}

}
