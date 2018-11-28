/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowire;
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
import org.springframework.web.bind.annotation.ValueConstants;

import net.bytebuddy.description.annotation.AnnotationDescription;

public class EndpointApiAnnotationUtils {

	/**
	 * 构造 @Configuration 注解
	 */
	public static AnnotationDescription annotConfiguration(String name) {
		return AnnotationDescription.Builder.ofType(Configuration.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.build();
	}
	
	/**
	 * 构造 @Qualifier 注解
	 */
	public static AnnotationDescription annotQualifier(String name) {
		return AnnotationDescription.Builder.ofType(Qualifier.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.build();
	}
	
	/**
	 * 构造 @Autowired 注解
	 */
	public static AnnotationDescription annotAutowired(boolean required) {
		return AnnotationDescription.Builder.ofType(Autowired.class)
				.define("value", required)
				.build();
	}
	
	/**
	 * 构造 @Bean 注解
	 */
	public static AnnotationDescription annotBean(String[] name, Autowire autowire, String initMethod, String destroyMethod,
			boolean autowireCandidate) {
		return AnnotationDescription.Builder.ofType(Bean.class)
				.defineArray("value", ArrayUtils.isEmpty(name) ? new String[] {} : name)
				.defineArray("name", ArrayUtils.isEmpty(name) ? new String[] {} : name)
				.define("autowire", autowire)
				.define("initMethod", StringUtils.hasText(initMethod) ? initMethod : "")
				.define("destroyMethod", StringUtils.hasText(destroyMethod) ? destroyMethod : "")
				.define("autowireCandidate", autowireCandidate)
				.build();
	}

	/**
	 * 构造 @Lazy 注解
	 */
	public static AnnotationDescription annotLazy(boolean lazy) {
		return AnnotationDescription.Builder.ofType(Lazy.class)
				.define("value", lazy)
				.build();
	}

	/**
	 * 构造 @Scope 注解
	 */
	public static AnnotationDescription annotScope(String scopeName, ScopedProxyMode proxyMode) {
		return AnnotationDescription.Builder.ofType(Scope.class)
				.defineArray("value", StringUtils.hasText(scopeName) ? scopeName : "")
				.defineArray("scopeName", StringUtils.hasText(scopeName) ? scopeName : "")
				.define("proxyMode", proxyMode != null ? proxyMode : ScopedProxyMode.DEFAULT)
				.build();
	}
	
	/**
	 * 构造 @Controller 注解
	 */
	public static AnnotationDescription annotController(String name) {
		if (StringUtils.hasText(name)) {
			return AnnotationDescription.Builder.ofType(Controller.class).define("value", name).build();
		}
		return AnnotationDescription.Builder.ofType(Controller.class).build();
	}
	
	/**
	 * 构造 @RestController 注解
	 */
	public static AnnotationDescription annotRestController(String name) {
		if (StringUtils.hasText(name)) {
			return AnnotationDescription.Builder.ofType(RestController.class).define("value", name).build();
		}
		return AnnotationDescription.Builder.ofType(RestController.class).build();
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static AnnotationDescription annotRequestMapping(MvcMapping mapping) {
		return annotHttpMethod(RequestMapping.class, mapping);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static AnnotationDescription annotGetMapping(MvcMapping mapping) {
		return annotHttpMethod(GetMapping.class, mapping);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static AnnotationDescription annotPostMapping(MvcMapping mapping) {
		return annotHttpMethod(PostMapping.class, mapping);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static AnnotationDescription annotPutMapping(MvcMapping mapping) {
		return annotHttpMethod(PutMapping.class, mapping);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static AnnotationDescription annotDeleteMapping(MvcMapping mapping) {
		return annotHttpMethod(DeleteMapping.class, mapping);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static AnnotationDescription annotPatchMapping(MvcMapping mapping) {
		return annotHttpMethod(PatchMapping.class, mapping);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
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
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
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
	 * 构造 @RequestMapping 注解
	 */
	public static AnnotationDescription annotRequestMapping(String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static AnnotationDescription annotGetMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(GetMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static AnnotationDescription annotPostMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PostMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static AnnotationDescription annotPutMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PutMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static AnnotationDescription annotDeleteMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(DeleteMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static AnnotationDescription annotPatchMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PatchMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static AnnotationDescription annotBound(MvcBound bound) {
		return AnnotationDescription.Builder.ofType(WebBound.class)
				.define("uid", StringUtils.hasText(bound.getUid()) ? bound.getUid() : "")
				.define("json", StringUtils.hasText(bound.getJson()) ? bound.getJson() : "")
				.build();
	}
	
	/**
	 * 根据参数 构造   @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 */
	public static AnnotationDescription annotMethodMapping(MvcMethod method) {
		
		AnnotationDescription annot = null;
		// 多种支持请求方法
		if(method.getMethod().length > 1) {
			annot = annotGetMapping(method.getName(), method.getPath(), 
					method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		// 仅支持一种请求方式
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
	 * 构造 @CookieValue 注解
	 */
	public static <T> AnnotationDescription annotCookieValue(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(CookieValue.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
				.build();
	}
	
	/**
	 * 构造 @MatrixVariable 注解
	 */
	public static <T> AnnotationDescription annotMatrixVariable(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(MatrixVariable.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
				.define("pathVar", ValueConstants.DEFAULT_NONE)
				.build();
	}
	
	/**
	 * 构造 @PathVariable 注解
	 */
	public static <T> AnnotationDescription annotPathVariable(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(PathVariable.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}
	
	/**
	 * 构造 @RequestAttribute 注解
	 */
	public static <T> AnnotationDescription annotRequestAttribute(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestAttribute.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}
	
	/**
	 * 构造 @RequestBody 注解
	 */
	public static <T> AnnotationDescription annotRequestBody(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestBody.class)
				.define("required", param.isRequired())
				.build();
	}
	
	/**
	 * 构造 @RequestHeader 注解
	 */
	public static <T> AnnotationDescription annotRequestHeader(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestHeader.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
				.build();
	}
	
	/**
	 * 构造 @RequestPart 注解
	 */
	public static <T> AnnotationDescription annotRequestPart(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestPart.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.build();
	}
	
	/**
	 * 构造 @RequestParam 注解
	 */
	public static <T> AnnotationDescription annotRequestParam(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(RequestParam.class)
				.define("value", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("name", StringUtils.hasText(param.getName()) ? param.getName() : "")
				.define("required", param.isRequired())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
				.build();
	}
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
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
	 * 构造 @Valid 注解
	 */
	public static <T> AnnotationDescription annotValid(MvcParam<T> param) {
		return AnnotationDescription.Builder.ofType(Valid.class).build();
	}
	
}
