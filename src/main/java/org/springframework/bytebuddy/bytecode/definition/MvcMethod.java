package org.springframework.bytebuddy.bytecode.definition;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMethod;

public class MvcMethod {

	/**
	 * Java 方法的名称
	 */
	private final String name;

	/**
	 * In a Servlet environment only: the path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do"). At the
	 * method level, relative paths (e.g. "edit.do") are supported within the
	 * primary mapping expressed at the type level. Path mapping URIs may contain
	 * placeholders (e.g. "/${connect}")
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
	 * 指定request中必须包含某些参数值是，才让该方法处理
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
	 * 指定request中必须包含某些指定的header值，才能让该方法处理请求
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
	 * 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
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
	 * 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
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
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param method		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, RequestMethod method) {
		this(name, path, true, method, null, null, null, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param methods		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, RequestMethod[] methods) {
		this(name, path, true, methods, null, null, null, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param method		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method) {
		this(name, path, responseBody, method, null, null, null, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param methods		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods) {
		this(name, path, responseBody, methods, null, null, null, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param method		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces) {
		this(name, path, responseBody, method, null, null, produces, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 * @param methods		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces) {
		this(name, path, responseBody, methods, null, null, produces, null);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param method		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 * @param consumes		: 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces, String[] consumes) {
		this(name, path, responseBody, method, null, null, produces, consumes);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 * @param consumes		: 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	 * @param methods		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces, String[] consumes) {
		this(name, path, responseBody, methods, null, null, produces, consumes);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param method		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 * @param params		: 指定request中必须包含某些参数值是，才让该方法处理
	 * @param headers		: 指定request中必须包含某些指定的header值，才能让该方法处理请求
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 * @param consumes		: 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] params, String[] headers,
			String[] produces, String[] consumes) {
		this(name, path, responseBody, new RequestMethod[] { method }, params, headers, produces, consumes);
	}
	
	/**
	 * @param name 			: 方法名称
	 * @param path			: 指定请求的实际地址， 比如 /action/info之类。
	 * @param responseBody	: 指定是否添加 @ResponseBody 注解
	 * @param methods		: 指定请求的method类型， GET、POST、PUT、DELETE等
	 * @param params		: 指定request中必须包含某些参数值是，才让该方法处理
	 * @param headers		: 指定request中必须包含某些指定的header值，才能让该方法处理请求
	 * @param produces		: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
	 * @param consumes		: 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
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

	public RequestMethod[] getMethod() {
		return method;
	}

	public void setMethod(RequestMethod[] method) {
		this.method = method;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public String[] getConsumes() {
		return consumes;
	}

	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	public String[] getProduces() {
		return produces;
	}

	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	public boolean isResponseBody() {
		return responseBody;
	}

	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	public String getName() {
		return name;
	}

	public String[] getPath() {
		return path;
	}

}
