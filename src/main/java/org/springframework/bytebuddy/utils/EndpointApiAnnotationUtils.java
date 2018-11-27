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
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.harmony.lang.annotation.AnnotationMember;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.intercept.Scope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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

public class EndpointApiAnnotationUtils {


	/**
	 * 构造 @Configuration 注解
	 */
	public static Annotation annotConfiguration(String name) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(name) ? name : "")
		};
		return AnnotationUtils.create(Configuration.class, members);
	}
	
	/**
	 * 构造 @Qualifier 注解
	 */
	public static Annotation annotQualifier(String name) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(name) ? name : "")
		};
		return AnnotationUtils.create(Qualifier.class, members);
	}
	
	/**
	 * 构造 @Autowired 注解
	 */
	public static Annotation annotAutowired(boolean required) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", required)
		};
		return AnnotationUtils.create(Autowired.class, members);
	}
	
	/**
	 * 构造 @Bean 注解
	 */
	public static Annotation annotBean(String[] name, Autowire autowire, String initMethod, String destroyMethod,
			boolean autowireCandidate) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", ArrayUtils.isEmpty(name) ? new String[] {} : name),
			new AnnotationMember("name", ArrayUtils.isEmpty(name) ? new String[] {} : name),
			new AnnotationMember("autowire", autowire),
			new AnnotationMember("initMethod", StringUtils.hasText(initMethod) ? initMethod : ""),
			new AnnotationMember("destroyMethod", StringUtils.hasText(destroyMethod) ? destroyMethod : ""),
			new AnnotationMember("autowireCandidate", autowireCandidate) 
		};
		return AnnotationUtils.create(Bean.class, members);
	}

	/**
	 * 构造 @Lazy 注解
	 */
	public static Annotation annotLazy(boolean lazy) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", lazy)
		};
		return AnnotationUtils.create(Lazy.class, members);
	}

	/**
	 * 构造 @Scope 注解
	 */
	public static Annotation annotScope(String scopeName, ScopedProxyMode proxyMode) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", scopeName),
			new AnnotationMember("scopeName", scopeName),
			new AnnotationMember("proxyMode", proxyMode)
		};
		return AnnotationUtils.create(Scope.class, members);
	}
	
	/**
	 * 构造 @Controller 注解
	 */
	public static Annotation annotController(String name) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", name)
		};
		return AnnotationUtils.create(Controller.class, members);
	}
	
	/**
	 * 构造 @RestController 注解
	 */
	public static Annotation annotRestController(String name) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", name)
		};
		return AnnotationUtils.create(RestController.class, members);
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
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("name", StringUtils.hasText(mapping.getName()) ? mapping.getName() : ""),
			new AnnotationMember("value", ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {}),
			new AnnotationMember("path", ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {}),
			new AnnotationMember("params", ArrayUtils.isNotEmpty(mapping.getParams()) ? mapping.getParams() : new String[] {}),
			new AnnotationMember("headers", ArrayUtils.isNotEmpty(mapping.getHeaders()) ? mapping.getHeaders() : new String[] {}),
			new AnnotationMember("consumes", ArrayUtils.isNotEmpty(mapping.getConsumes()) ? mapping.getConsumes() : new String[] {}),
			new AnnotationMember("produces", ArrayUtils.isNotEmpty(mapping.getProduces()) ? mapping.getProduces() : new String[] {})
		};
		if(ArrayUtils.isNotEmpty(mapping.getMethod())) {
			List<AnnotationMember> memberList = Arrays.asList(members);
			memberList.add(new AnnotationMember("method", mapping.getMethod()));
			members = memberList.toArray(new AnnotationMember[memberList.size()]);
		}
		return AnnotationUtils.create(annotation, members);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 */
	private static Annotation annotHttpMethod(
			Class<? extends java.lang.annotation.Annotation> annotation,String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("name", StringUtils.hasText(name) ? name : ""),
			new AnnotationMember("value", ArrayUtils.isNotEmpty(path) ? path : new String[] {}),
			new AnnotationMember("path", ArrayUtils.isNotEmpty(path) ? path : new String[] {}),
			new AnnotationMember("params", ArrayUtils.isNotEmpty(params) ? params : new String[] {}),
			new AnnotationMember("headers", ArrayUtils.isNotEmpty(headers) ? headers : new String[] {}),
			new AnnotationMember("consumes", ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {}),
			new AnnotationMember("produces", ArrayUtils.isNotEmpty(produces) ? produces : new String[] {})
		};
		if(method != null) {
			List<AnnotationMember> memberList = Arrays.asList(members);
			memberList.add(new AnnotationMember("method", ArrayUtils.isNotEmpty(method) ? method : new RequestMethod[] {}));
			members = memberList.toArray(new AnnotationMember[memberList.size()]);
		}
		return AnnotationUtils.create(annotation, members);
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static Annotation annotRequestMapping(String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static Annotation annotGetMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(GetMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static Annotation annotPostMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PostMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static Annotation annotPutMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PutMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static Annotation annotDeleteMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(DeleteMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static Annotation annotPatchMapping(String name, String[] path, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(PatchMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Annotation annotBound(MvcBound bound) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("uid", StringUtils.hasText(bound.getUid()) ? bound.getUid() : ""),
			new AnnotationMember("json", StringUtils.hasText(bound.getJson()) ? bound.getJson() : "")
		};
		return AnnotationUtils.create(WebBound.class, members);
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
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired()),
			new AnnotationMember("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
		};
		return AnnotationUtils.create(CookieValue.class, members);
	}
	
	/**
	 * 构造 @MatrixVariable 注解
	 */
	public static <T> Annotation annotMatrixVariable(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired()),
			new AnnotationMember("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE),
			new AnnotationMember("pathVar", ValueConstants.DEFAULT_NONE)
		};
		return AnnotationUtils.create(MatrixVariable.class, members);
	}
	
	/**
	 * 构造 @PathVariable 注解
	 */
	public static <T> Annotation annotPathVariable(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired())
		};
		return AnnotationUtils.create(PathVariable.class, members);
	}
	
	
	/**
	 * 构造 @RequestAttribute 注解
	 */
	public static <T> Annotation annotRequestAttribute(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired())
		};
		return AnnotationUtils.create(RequestAttribute.class, members);
	}
	
	/**
	 * 构造 @RequestBody 注解
	 */
	public static <T> Annotation annotRequestBody(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("required", param.isRequired())
		};
		return AnnotationUtils.create(RequestBody.class, members);
	}
	
	/**
	 * 构造 @RequestHeader 注解
	 */
	public static <T> Annotation annotRequestHeader(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired()),
			new AnnotationMember("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
		};
		return AnnotationUtils.create(RequestHeader.class, members);
	}
	
	/**
	 * 构造 @RequestPart 注解
	 */
	public static <T> Annotation annotRequestPart(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired())
		};
		return AnnotationUtils.create(RequestPart.class, members);
	}
	
	/**
	 * 构造 @RequestParam 注解
	 */
	public static <T> Annotation annotRequestParam(MvcParam<T> param) {
		AnnotationMember[] members = new AnnotationMember[] {
			new AnnotationMember("value", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("name", StringUtils.hasText(param.getName()) ? param.getName() : ""),
			new AnnotationMember("required", param.isRequired()),
			new AnnotationMember("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : ValueConstants.DEFAULT_NONE)
		};
		return AnnotationUtils.create(RequestParam.class, members);
	}
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
	 */
	public static <T> Annotation annotParam(MvcParam<T> param) {
		Annotation paramAnnot = null;
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
	public static <T> Annotation annotValid(MvcParam<T> param) {
		return AnnotationUtils.create(Valid.class, null);
	}
	
}