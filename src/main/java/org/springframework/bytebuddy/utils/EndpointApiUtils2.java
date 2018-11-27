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

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.annotations.Authorization;

public class EndpointApiUtils2 {

	/**
	 * 构造 @Api 注解
	 */
	public static Annotation annotApi(String... tags) {
		return new io.swagger.annotations.Api() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return io.swagger.annotations.Api.class;
			}

			@Override
			public String value() {
				return "";
			}
			
			@Override
			public String[] tags() {
				return ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags;
			}

			@Override
			public String description() {
				return "";
			}

			@Override
			public String basePath() {
				return "";
			}

			@Override
			public int position() {
				return 0;
			}

			@Override
			public String produces() {
				return "";
			}

			@Override
			public String consumes() {
				return "";
			}

			@Override
			public String protocols() {
				return "";
			}

			@Override
			public Authorization[] authorizations() {
				return new Authorization[] {};
			}

			@Override
			public boolean hidden() {
				return false;
			}
			
		};

	} 
	
	/**
	 * 构造 @ApiIgnore 注解
	 */
	public static Annotation annotApiIgnore(String desc) {
		
		return new springfox.documentation.annotations.ApiIgnore() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return springfox.documentation.annotations.ApiIgnore.class;
			}

			@Override
			public String value() {
				return desc;
			}

 
		};
	}

	/**
	 * 构造 @Configuration 注解
	 */
	public static Annotation annotConfiguration(String name) {
		
		return new Configuration() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Configuration.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		};
	}
	
	/**
	 * 构造 @Autowired 注解
	 */
	public static Annotation annotAutowired(boolean required) {
		
		return new Autowired() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Autowired.class;
			}

			@Override
			public boolean required() {
				return required;
			}
			
		};
	}
	
	
	/**
	 * 构造 @Bean 注解
	 */
	public static Annotation annotBean(String[] name, Autowire autowire
			,String initMethod,String destroyMethod) {
		
		return new Bean() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Bean.class;
			}

			@Override
			public String[] value() {
				return name;
			}

			@Override
			public String[] name() {
				return name;
			}

			@Override
			public Autowire autowire() {
				return autowire;
			}

			@Override
			public String initMethod() {
				return initMethod;
			}

			@Override
			public String destroyMethod() {
				return destroyMethod;
			}

			@Override
			public boolean autowireCandidate() {
				return false;
			}

 
		};
		
	}

	/**
	 * 构造 @Lazy 注解
	 */
	public static Annotation annotLazy(boolean lazy) {
		
		return new Lazy() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Lazy.class;
			}


			@Override
			public boolean value() {
				return lazy;
			}
			
		};
		
	}

	/**
	 * 构造 @Scope 注解
	 */
	public static Annotation annotScope(String scopeName, ScopedProxyMode proxyMode) {
		
		return new Scope() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Scope.class;
			}

			@Override
			public String scopeName() {
				return scopeName;
			}

			@Override
			public ScopedProxyMode proxyMode() {
				return proxyMode;
			}

			@Override
			public String value() {
				return scopeName;
			}
			
		};
		
	}
	
	/**
	 * 构造 @Controller 注解
	 */
	public static Annotation annotController(String name) {
		return new Controller() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Controller.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		};
	}
	
	/**
	 * 构造 @RestController 注解
	 */
	public static Annotation annotRestController(String name) {
		return new RestController() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return RestController.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		};
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static Annotation annotRequestMapping(MvcMapping mapping) {
		return annotHttpMethod(RequestMapping.class, mapping);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static Annotation annotGetMapping(MvcMapping mapping) {
		return annotHttpMethod(GetMapping.class, mapping);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static Annotation annotPostMapping(MvcMapping mapping) {
		return annotHttpMethod(PostMapping.class, mapping);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static Annotation annotPutMapping(MvcMapping mapping) {
		return annotHttpMethod(PutMapping.class, mapping);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static Annotation annotDeleteMapping(MvcMapping mapping) {
		return annotHttpMethod(DeleteMapping.class, mapping);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static Annotation annotPatchMapping(MvcMapping mapping) {
		return annotHttpMethod(PatchMapping.class, mapping);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 */
	private static Annotation annotHttpMethod(
			Class<? extends java.lang.annotation.Annotation> annotation,
			MvcMapping mapping) {
		
		return new RequestMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return RequestMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(mapping.getName()) ? mapping.getName() : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {};
			}

			@Override
			public RequestMethod[] method() {
				return ArrayUtils.isNotEmpty(mapping.getMethod()) ? mapping.getMethod() : new RequestMethod[] {};
			}

			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(mapping.getParams()) ? mapping.getParams() : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(mapping.getHeaders()) ? mapping.getHeaders() : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(mapping.getConsumes()) ? mapping.getConsumes() : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(mapping.getProduces()) ? mapping.getProduces() : new String[] {};
			}

			
		};
		
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static Annotation annotRequestMapping(String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new RequestMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return RequestMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public RequestMethod[] method() {
				return ArrayUtils.isNotEmpty(method) ? method : new RequestMethod[] {};
			}

			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}

			
		};
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static Annotation annotGetMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new GetMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return GetMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}
			
			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}

			
		};
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static Annotation annotPostMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new PostMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return PostMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}
			
			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}

			
		};
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static Annotation annotPutMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new PutMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return PutMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}
			
			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}

			
		};
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static Annotation annotDeleteMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new DeleteMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return DeleteMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}
			
			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}

			
		};
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static Annotation annotPatchMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return new PatchMapping() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return PatchMapping.class;
			}

			@Override
			public String name() {
				return StringUtils.hasText(name) ? name : "";
			}

			@Override
			public String[] value() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] path() {
				return ArrayUtils.isNotEmpty(path) ? path : new String[] {};
			}

			@Override
			public String[] params() {
				return ArrayUtils.isNotEmpty(params) ? params : new String[] {};
			}

			@Override
			public String[] headers() {
				return ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
			}

			@Override
			public String[] consumes() {
				return ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
			}

			@Override
			public String[] produces() {
				return ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
			}
			
		};
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Annotation annotWebBound(MvcBound bound) {

		return new WebBound() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return WebBound.class;
			}

			@Override
			public String uid() {
				return bound.getUid();
			}

			@Override
			public String json() {
				return bound.getJson();
			}
			
		};
		
	}
	
	/**
	 * 根据参数 构造   @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 */
	public static Annotation annotMethodMapping(MvcMethod method) {
		
		Annotation annot = null;
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
	public static <T> Annotation annotCookieValue(MvcParam<T> param) {
		return new CookieValue() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return CookieValue.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

			@Override
			public String defaultValue() {
				return StringUtils.hasText(param.getDef()) ? param.getDef() : "";
			}
		};
	}
	
	/**
	 * 构造 @MatrixVariable 注解
	 */
	public static <T> Annotation annotMatrixVariable(MvcParam<T> param) {
		return new MatrixVariable() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return MatrixVariable.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

			@Override
			public String defaultValue() {
				return StringUtils.hasText(param.getDef()) ? param.getDef() : "";
			}

			@Override
			public String pathVar() {
				return ValueConstants.DEFAULT_NONE;
			}
		};
	}
	
	/**
	 * 构造 @PathVariable 注解
	 */
	public static <T> Annotation annotPathVariable(MvcParam<T> param) {
		return new PathVariable() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return PathVariable.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

		};
	}
	
	
	/**
	 * 构造 @RequestAttribute 注解
	 */
	public static <T> Annotation annotRequestAttribute(MvcParam<T> param) {
		return new RequestAttribute() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestAttribute.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

		};
	}
	
	/**
	 * 构造 @RequestBody 注解
	 */
	public static <T> Annotation annotRequestBody(MvcParam<T> param) {
		return new RequestBody() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestBody.class;
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}
		};
	}
	
	/**
	 * 构造 @RequestHeader 注解
	 */
	public static <T> Annotation annotRequestHeader(MvcParam<T> param) {
		return new RequestHeader() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestHeader.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

			@Override
			public String defaultValue() {
				return StringUtils.hasText(param.getDef()) ? param.getDef() : "";
			}

		};
	}
	
	/**
	 * 构造 @RequestPart 注解
	 */
	public static <T> Annotation annotRequestPart(MvcParam<T> param) {
		return new RequestPart() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestPart.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}
		};
	}
	
	/**
	 * 构造 @RequestParam 注解
	 */
	public static <T> Annotation annotRequestParam(MvcParam<T> param) {
		return new RequestParam() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestParam.class;
			}

			@Override
			public String value() {
				return param.getName();
			}

			@Override
			public boolean required() {
				return param.isRequired();
			}

			@Override
			public String name() {
				return param.getName();
			}

			@Override
			public String defaultValue() {
				return StringUtils.hasText(param.getDef()) ? param.getDef() : "";
			}

		};
	}
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
	 */
	public static <T> Annotation[][] annotParams(MvcParam<?>... params) {

		// 添加参数注解
		if (params != null && params.length > 0) {
			
			Annotation[][] paramArrays = new Annotation[params.length][1];
			Annotation paramAnnot = null;
			for (int i = 0; i < params.length; i++) {
				paramAnnot = null;
				switch (params[i].getFrom()) {
					case COOKIE: {
						paramAnnot = annotCookieValue(params[i]);
					};break;
					case MATRIX: {
						paramAnnot = annotMatrixVariable(params[i]);
					};break;
					case PATH: {
						paramAnnot = annotPathVariable(params[i]);
					};break;
					case ATTR: {
						paramAnnot = annotRequestAttribute(params[i]);
					};break;
					case BODY: {
						paramAnnot = annotRequestBody(params[i]);
					};break;
					case HEADER: {
						paramAnnot = annotRequestHeader(params[i]);
					};break;
					case PARAM: {
						paramAnnot = annotRequestParam(params[i]);
					};break;
					case PART: {
						paramAnnot = annotRequestPart(params[i]);
					};break;
					default: {
						paramAnnot = annotRequestParam(params[i]);
					};break;
				}
				paramArrays[i][0] = paramAnnot;
			}
			
			return paramArrays;

		}
		return null;
	}
	
}