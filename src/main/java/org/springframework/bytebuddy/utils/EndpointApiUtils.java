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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Authorization;
import net.bytebuddy.dynamic.DynamicType.Builder;

public class EndpointApiUtils {

	/**
	 * 构造 @Api 注解
	 */
	public static <T> Builder<T> annotApi(Builder<T> builder, String... tags) {
		return builder.annotateType(new io.swagger.annotations.Api() {

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
			
		});

	} 
	
	/**
	 * 构造 @ApiIgnore 注解
	 */
	public static <T> Builder<T> annotApiIgnore(Builder<T> builder, String desc) {
		
		return builder.annotateType(new springfox.documentation.annotations.ApiIgnore() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return springfox.documentation.annotations.ApiIgnore.class;
			}

			@Override
			public String value() {
				return desc;
			}

 
		});
	}

	/**
	 * 构造 @Configuration 注解
	 */
	public static <T> Builder<T> annotConfiguration(Builder<T> builder, String name) {
		
		return builder.annotateType(new Configuration() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Configuration.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		});
	}
	
	/**
	 * 构造 @Bean 注解
	 */
	public static <T> Builder<T> annotBean(Builder<T> builder, String[] name, Autowire autowire
			,String initMethod,String destroyMethod) {
		
		return builder.annotateType(new Bean() {

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

 
		});
		
	}

	/**
	 * 构造 @Lazy 注解
	 */
	public static <T> Builder<T> annotLazy(Builder<T> builder, boolean lazy) {
		
		return builder.annotateType(new Lazy() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Lazy.class;
			}


			@Override
			public boolean value() {
				return lazy;
			}
			
		});
		
	}

	/**
	 * 构造 @Scope 注解
	 */
	public static <T> Builder<T> annotScope(Builder<T> builder, String scopeName, ScopedProxyMode proxyMode) {
		
		return builder.annotateType(new Scope() {

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
			
		});
		
	}
	
	/**
	 * 构造 @Controller 注解
	 */
	public static <T> Builder<T> annotController(Builder<T> builder, String name) {
		return builder.annotateType(new Controller() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Controller.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		});
	}
	
	/**
	 * 构造 @RestController 注解
	 */
	public static <T> Builder<T> annotRestController(Builder<T> builder, String name) {
		return builder.annotateType(new RestController() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return RestController.class;
			}

			@Override
			public String value() {
				return name;
			}
			
		});
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static <T> Builder<T> annotRequestMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, RequestMapping.class, mapping);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static <T> Builder<T> annotGetMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, GetMapping.class, mapping);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static <T> Builder<T> annotPostMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, PostMapping.class, mapping);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static <T> Builder<T> annotPutMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, PutMapping.class, mapping);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static <T> Builder<T> annotDeleteMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, DeleteMapping.class, mapping);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static <T> Builder<T> annotPatchMapping(Builder<T> builder, MvcMapping mapping) {
		return annotHttpMethod(builder, PatchMapping.class, mapping);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 */
	private static <T> Builder<T> annotHttpMethod(Builder<T> builder, 
			Class<? extends java.lang.annotation.Annotation> annotation,
			MvcMapping mapping) {
		
		return builder.annotateType(new RequestMapping() {

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

			
		});
		
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static <T> Builder<T> annotRequestMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new RequestMapping() {

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

			
		});
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static <T> Builder<T> annotGetMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new GetMapping() {

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

			
		});
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static <T> Builder<T> annotPostMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new PostMapping() {

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

			
		});
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static <T> Builder<T> annotPutMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new PutMapping() {

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

			
		});
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static <T> Builder<T> annotDeleteMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new DeleteMapping() {

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

			
		});
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static <T> Builder<T> annotPatchMapping(Builder<T> builder, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return builder.annotateType(new PatchMapping() {

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
			
		});
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static <T> Builder<T> annotWebBound(Builder<T> builder, MvcBound bound) {

		return builder.annotateType(new WebBound() {

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
			
		});
		
	}
	
	/**
	 * 根据参数 构造   @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 */
	public static <T> Builder<T> annotMethodMapping(Builder<T> builder, MvcMethod method) {
		
		Builder<T> annot = null;
		// 多种支持请求方法
		if(method.getMethod().length > 1) {
			annot = annotGetMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
					method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		// 仅支持一种请求方式
		switch (method.getMethod()[0]) {
			case GET:{
				annot = annotGetMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case POST:{
				annot = annotPostMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PUT:{
				annot = annotPutMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case DELETE:{
				annot = annotDeleteMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PATCH:{
				annot = annotPatchMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			default:{
				annot = annotGetMapping(builder, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
		}
		
		return annot;
	}
	
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
	 
	public static <T> Annotation[][] annotParams(Builder<T> builder, MvcParam<?>... params) {

		// 添加参数注解
		if (params != null && params.length > 0) {
			
			Annotation[][] paramArrays = new Annotation[params.length][1];
			
			Annotation paramAnnot = null;
			boolean defAnnot = false;
			for (int i = 0; i < params.length; i++) {
				paramAnnot = null;
				defAnnot = false;
				switch (params[i].getFrom()) {
					case COOKIE:{
						paramAnnot = new Annotation(CookieValue.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case MATRIX:{
						paramAnnot = new Annotation(MatrixVariable.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PATH:{
						paramAnnot = new Annotation(PathVariable.class.getName(), constPool);
					};break;
					case ATTR:{
						paramAnnot = new Annotation(RequestAttribute.class.getName(), constPool);
					};break;
					case BODY:{
						paramAnnot = new Annotation(RequestBody.class.getName(), constPool);
					};break;
					case HEADER:{
						paramAnnot = new Annotation(RequestHeader.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PARAM:{
						paramAnnot = new Annotation(RequestParam.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PART:{
						paramAnnot = new Annotation(RequestPart.class.getName(), constPool);
					};break;
					default:{
						paramAnnot = new Annotation(RequestParam.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
				}
				
				if(MvcParamFrom.BODY.compareTo(params[i].getFrom()) != 0){
					paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
					if(defAnnot) {
						paramAnnot.addMemberValue("defaultValue", new StringMemberValue(params[i].getDef(), constPool));
					}
				}
				
				paramArrays[i][0] = paramAnnot;
				
			}
			
			return paramArrays;

		}
		return null;
	}*/
	
}