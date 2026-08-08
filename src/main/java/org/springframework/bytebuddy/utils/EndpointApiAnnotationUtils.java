/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.bytebuddy.utils;


import jakarta.validation.Valid;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import net.bytebuddy.description.annotation.AnnotationDescription;

/**
 * Utility class for constructing ByteBuddy {@link AnnotationDescription}
 * instances that mirror Spring MVC and Spring Framework annotations.
 * These annotation descriptions are used to annotate dynamically generated
 * controller classes and their methods, fields, and parameters.
 *
 * <p>Supports the following Spring annotations:
 * <ul>
 *   <li>Class-level: {@code @Configuration}, {@code @Controller}, {@code @RestController}</li>
 *   <li>Mapping: {@code @RequestMapping}, {@code @GetMapping}, {@code @PostMapping},
 *       {@code @PutMapping}, {@code @DeleteMapping}, {@code @PatchMapping}</li>
 *   <li>Dependency injection: {@code @Autowired}, {@code @Qualifier}, {@code @Bean},
 *       {@code @Lazy}, {@code @Scope}</li>
 *   <li>Parameter binding: {@code @RequestParam}, {@code @PathVariable},
 *       {@code @RequestBody}, {@code @RequestHeader}, {@code @CookieValue},
 *       {@code @MatrixVariable}, {@code @RequestAttribute}, {@code @RequestPart}</li>
 *   <li>Custom: {@code @WebBound}</li>
 * </ul>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.springframework.bytebuddy.bytecode.EndpointApiBuilder
 * @see org.springframework.bytebuddy.bytecode.ReactiveHandlerBuilder
 */
public class EndpointApiAnnotationUtils {

	/**
	 * Constructs a {@code @Configuration} annotation description.
	 *
	 * @param name the explicit bean name for the configuration class,
	 *             or an empty string for auto-generated naming
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotConfiguration(String name) {
		return AnnotationDescription.Builder.ofType(Configuration.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.build();
	}

	/**
	 * Constructs a {@code @Qualifier} annotation description.
	 *
	 * @param name the qualifier name used to narrow the injection candidate
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotQualifier(String name) {
		return AnnotationDescription.Builder.ofType(Qualifier.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.build();
	}

	/**
	 * Constructs an {@code @Autowired} annotation description.
	 *
	 * @param required whether the annotated dependency is required
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotAutowired(boolean required) {
		return AnnotationDescription.Builder.ofType(Autowired.class)
				.define("required", required)
				.build();
	}

	/**
	 * Constructs a {@code @Bean} annotation description.
	 *
	 * @param name              the bean name(s)
	 * @param initMethod        the optional initialization method name
	 * @param destroyMethod     the optional destroy method name
	 * @param autowireCandidate whether this bean is a candidate for autowiring
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotBean(String[] name, String initMethod, String destroyMethod,
			boolean autowireCandidate) {
		return AnnotationDescription.Builder.ofType(Bean.class)
				.defineArray("value", ArrayUtils.isEmpty(name) ? new String[] {} : name)
				.defineArray("name", ArrayUtils.isEmpty(name) ? new String[] {} : name)
				.define("initMethod", StringUtils.hasText(initMethod) ? initMethod : "")
				.define("destroyMethod", StringUtils.hasText(destroyMethod) ? destroyMethod : "")
				.define("autowireCandidate", autowireCandidate)
				.build();
	}

	/**
	 * Constructs a {@code @Lazy} annotation description.
	 *
	 * @param lazy whether lazy initialization should occur
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotLazy(boolean lazy) {
		return AnnotationDescription.Builder.ofType(Lazy.class)
				.define("value", lazy)
				.build();
	}

	/**
	 * Constructs a {@code @Scope} annotation description.
	 *
	 * @param scopeName  the name of the scope to use (e.g. "singleton", "prototype")
	 * @param proxyMode  the scoped proxy mode
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotScope(String scopeName, ScopedProxyMode proxyMode) {
		return AnnotationDescription.Builder.ofType(Scope.class)
				.defineArray("value", StringUtils.hasText(scopeName) ? scopeName : "")
				.defineArray("scopeName", StringUtils.hasText(scopeName) ? scopeName : "")
				.define("proxyMode", proxyMode != null ? proxyMode : ScopedProxyMode.DEFAULT)
				.build();
	}

	/**
	 * Constructs a {@code @Controller} annotation description.
	 *
	 * @param name the suggested component name, or an empty string for default
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotController(String name) {
		if (StringUtils.hasText(name)) {
			return AnnotationDescription.Builder.ofType(Controller.class).define("value", name).build();
		}
		return AnnotationDescription.Builder.ofType(Controller.class).build();
	}

	/**
	 * Constructs a {@code @RestController} annotation description.
	 *
	 * @param name the suggested component name, or an empty string for default
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotRestController(String name) {
		if (StringUtils.hasText(name)) {
			return AnnotationDescription.Builder.ofType(RestController.class).define("value", name).build();
		}
		return AnnotationDescription.Builder.ofType(RestController.class).build();
	}

	/**
	 * Constructs a {@code @RequestMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotRequestMapping(MvcMapping mapping) {
		return annotHttpMethod(RequestMapping.class, mapping);
	}

	/**
	 * Constructs a {@code @GetMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotGetMapping(MvcMapping mapping) {
		return annotHttpMethod(GetMapping.class, mapping);
	}

	/**
	 * Constructs a {@code @PostMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPostMapping(MvcMapping mapping) {
		return annotHttpMethod(PostMapping.class, mapping);
	}

	/**
	 * Constructs a {@code @PutMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPutMapping(MvcMapping mapping) {
		return annotHttpMethod(PutMapping.class, mapping);
	}

	/**
	 * Constructs a {@code @DeleteMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotDeleteMapping(MvcMapping mapping) {
		return annotHttpMethod(DeleteMapping.class, mapping);
	}

	/**
	 * Constructs a {@code @PatchMapping} annotation description from
	 * an {@link MvcMapping} configuration.
	 *
	 * @param mapping the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPatchMapping(MvcMapping mapping) {
		return annotHttpMethod(PatchMapping.class, mapping);
	}

	/**
	 * Constructs an HTTP method mapping annotation description from
	 * an {@link MvcMapping} configuration. Handles the method enumeration
	 * array specially since it requires enum array syntax.
	 *
	 * @param annotation the mapping annotation class to construct
	 * @param mapping    the mapping configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	private static AnnotationDescription annotHttpMethod(
			Class<? extends java.lang.annotation.Annotation> annotation,
			MvcMapping mapping) {

		AnnotationDescription.Builder builder = AnnotationDescription.Builder.ofType(annotation)
				.define("name", StringUtils.hasText(mapping.getName()) ? mapping.getName() : "")
				.defineArray("value", ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {})
				.defineArray("path", ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {})
				.defineArray("params", ArrayUtils.isNotEmpty(mapping.getParams()) ? mapping.getParams() : new String[] {})
				.defineArray("headers", ArrayUtils.isNotEmpty(mapping.getHeaders()) ? mapping.getHeaders() : new String[] {})
				.defineArray("consumes", ArrayUtils.isNotEmpty(mapping.getConsumes()) ? mapping.getConsumes() : new String[] {})
				.defineArray("produces", ArrayUtils.isNotEmpty(mapping.getProduces()) ? mapping.getProduces() : new String[] {});
		if(ArrayUtils.isNotEmpty(mapping.getMethod())) {
			builder = builder.defineEnumerationArray("method", RequestMethod.class, mapping.getMethod());
		}
		return	builder.build();
	}

	/**
	 * Constructs an HTTP method mapping annotation description from
	 * individual attribute values.
	 *
	 * @param annotation the mapping annotation class to construct
	 * @param name       the mapping name
	 * @param path       the path mapping URIs
	 * @param method     the HTTP request methods
	 * @param params     the required request parameters
	 * @param headers    the required request headers
	 * @param consumes   the consumed media types
	 * @param produces   the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	private static AnnotationDescription annotHttpMethod(
			Class<? extends java.lang.annotation.Annotation> annotation,String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {

		AnnotationDescription.Builder builder = AnnotationDescription.Builder.ofType(annotation)
				.define("name", StringUtils.hasText(name) ? name : "")
				.defineArray("value", ArrayUtils.isNotEmpty(path) ? path : new String[] {})
				.defineArray("path", ArrayUtils.isNotEmpty(path) ? path : new String[] {})
				.defineArray("params", ArrayUtils.isNotEmpty(params) ? params : new String[] {})
				.defineArray("headers", ArrayUtils.isNotEmpty(headers) ? headers : new String[] {})
				.defineArray("consumes", ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {})
				.defineArray("produces", ArrayUtils.isNotEmpty(produces) ? produces : new String[] {});
		if(ArrayUtils.isNotEmpty(method)) {
			builder = builder.defineEnumerationArray("method", RequestMethod.class, method);
		}
		return	builder.build();
	}

	/**
	 * Constructs a {@code @RequestMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param method   the HTTP request methods
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotRequestMapping(String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @GetMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotGetMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(GetMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @PostMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPostMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PostMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @PutMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPutMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PutMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @DeleteMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotDeleteMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(DeleteMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @PatchMapping} annotation description from
	 * individual attribute values.
	 *
	 * @param name     the mapping name
	 * @param path     the path mapping URIs
	 * @param params   the required request parameters
	 * @param headers  the required request headers
	 * @param consumes the consumed media types
	 * @param produces the produced media types
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotPatchMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PatchMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Constructs a {@code @WebBound} annotation description from
	 * an {@link MvcBound} configuration.
	 *
	 * @param bound the data binding configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotBound(MvcBound bound) {
		return AnnotationDescription.Builder.ofType(WebBound.class)
				.define("uid", StringUtils.hasText(bound.getUid()) ? bound.getUid() : "")
				.define("json", StringUtils.hasText(bound.getJson()) ? bound.getJson() : "")
				.build();
	}

	/**
	 * Constructs the appropriate HTTP method mapping annotation for a given
	 * {@link MvcMethod}. If the method specifies multiple HTTP methods, a
	 * {@code @RequestMapping} is generated; otherwise, a method-specific
	 * mapping annotation ({@code @GetMapping}, {@code @PostMapping}, etc.)
	 * is generated.
	 *
	 * @param method the MVC method configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotMethodMapping(MvcMethod method) {

		AnnotationDescription annot = null;
		if(method.getMethod().length > 1) {
			annot = annotRequestMapping(method.getName(), method.getPath(),
					method.getMethod(), method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		switch (method.getMethod()[0]) {
			case GET:{
				annot = annotGetMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case POST:{
				annot = annotPostMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PUT:{
				annot = annotPutMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case DELETE:{
				annot = annotDeleteMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PATCH:{
				annot = annotPatchMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			default:{
				annot = annotGetMapping(method.getName(), method.getPath(),
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
		}

		return annot;
	}

	/**
	 * Constructs a {@code @CookieValue} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotCookieValue(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(CookieValue.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				.build();
	}

	/**
	 * Constructs a {@code @MatrixVariable} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotMatrixVariable(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(MatrixVariable.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				.define("pathVar", "")
				.build();
	}

	/**
	 * Constructs a {@code @PathVariable} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotPathVariable(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(PathVariable.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}

	/**
	 * Constructs a {@code @RequestAttribute} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotRequestAttribute(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestAttribute.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}

	/**
	 * Constructs a {@code @RequestBody} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotRequestBody(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestBody.class)
				.define("required", param.isRequired())
				.build();
	}

	/**
	 * Constructs a {@code @RequestHeader} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotRequestHeader(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestHeader.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				.build();
	}

	/**
	 * Constructs a {@code @RequestPart} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotRequestPart(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestPart.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}

	/**
	 * Constructs a {@code @RequestParam} annotation description for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotRequestParam(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestParam.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				.build();
	}

	/**
	 * Constructs the appropriate parameter binding annotation based on
	 * the {@link MvcParamFrom} value of the given parameter. Supports
	 * all Spring MVC parameter binding annotations.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotParam(MvcParam<T> param) {
		AnnotationDescription paramAnnot = null;
		switch (param.getFrom()) {
			case COOKIE: {
				paramAnnot = annotCookieValue(param);
			};break;
			case MATRIX: {
				paramAnnot = annotMatrixVariable(param);
			};break;
			case PATH: {
				paramAnnot = annotPathVariable(param);
			};break;
			case ATTR: {
				paramAnnot = annotRequestAttribute(param);
			};break;
			case BODY: {
				paramAnnot = annotRequestBody(param);
			};break;
			case HEADER: {
				paramAnnot = annotRequestHeader(param);
			};break;
			case PARAM: {
				paramAnnot = annotRequestParam(param);
			};break;
			case PART: {
				paramAnnot = annotRequestPart(param);
			};break;
			default: {
				paramAnnot = annotRequestParam(param);
			};break;
		}
		return paramAnnot;
	}

	/**
	 * Constructs a {@code @Valid} (Jakarta Validation) annotation description
	 * for a method parameter.
	 *
	 * @param <T>   the parameter type
	 * @param param the parameter configuration
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static <T> AnnotationDescription annotValid(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(Valid.class).build();
	}

}
