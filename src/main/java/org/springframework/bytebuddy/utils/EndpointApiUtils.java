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

import java.lang.reflect.InvocationHandler;
import java.nio.file.Path;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.vindell.javassist.bytecode.CtAnnotationBuilder;
import com.github.vindell.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class EndpointApiUtils {

	/**
	 * 构造 @Api 注解
	 */
	public static Annotation annotApi(ConstPool constPool, String... tags) {

		tags = ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags;
		return CtAnnotationBuilder.create(io.swagger.annotations.Api.class, constPool).addStringMember("tags", tags).build();

	} 
	
	/**
	 * 构造 @ApiIgnore 注解
	 */
	public static Annotation annotApiIgnore(ConstPool constPool, String desc) {
		return CtAnnotationBuilder.create(springfox.documentation.annotations.ApiIgnore.class, constPool).addStringMember("value", desc).build();
	}

	/**
	 * 构造 @Configuration 注解
	 */
	public static Annotation annotConfiguration(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Configuration.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @Bean 注解
	 */
	public static Annotation annotBean(ConstPool constPool, String[] name, Autowire autowire
			,String initMethod,String destroyMethod) {
		return CtAnnotationBuilder.create(Bean.class, constPool).addStringMember("name", name)
				.addEnumMember("autowire", autowire)
				.addStringMember("initMethod", initMethod)
				.addStringMember("destroyMethod", destroyMethod).build();
	}

	/**
	 * 构造 @Lazy 注解
	 */
	public static Annotation annotLazy(ConstPool constPool, boolean lazy) {
		return CtAnnotationBuilder.create(Lazy.class, constPool).addBooleanMember("value", lazy).build();
	}

	/**
	 * 构造 @Scope 注解
	 */
	public static Annotation annotScope(ConstPool constPool, String scopeName, ScopedProxyMode proxyMode) {
		return CtAnnotationBuilder.create(Scope.class, constPool).addStringMember("scopeName", scopeName)
				.addEnumMember("proxyMode", proxyMode).build();
	}
	
	/**
	 * 构造 @Controller 注解
	 */
	public static Annotation annotController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Controller.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @RestController 注解
	 */
	public static Annotation annotRestController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(RestController.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, RequestMapping.class, mapping);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static Annotation annotGetMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, GetMapping.class, mapping);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static Annotation annotPostMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PostMapping.class, mapping);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static Annotation annotPutMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PutMapping.class, mapping);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, DeleteMapping.class, mapping);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PatchMapping.class, mapping);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 */
	private static Annotation annotHttpMethod(ConstPool constPool, 
			Class<? extends java.lang.annotation.Annotation> annotation,
			MvcMapping mapping) {

		String name = StringUtils.hasText(mapping.getName()) ? mapping.getName() : "";
		String[] path = ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {};
		RequestMethod[] method = ArrayUtils.isNotEmpty(mapping.getMethod()) ? mapping.getMethod() : new RequestMethod[] {};
		String[] params = ArrayUtils.isNotEmpty(mapping.getParams()) ? mapping.getParams() : new String[] {};
		String[] headers = ArrayUtils.isNotEmpty(mapping.getHeaders()) ? mapping.getHeaders() : new String[] {};
		String[] consumes = ArrayUtils.isNotEmpty(mapping.getConsumes()) ? mapping.getConsumes() : new String[] {};
		String[] produces = ArrayUtils.isNotEmpty(mapping.getProduces()) ? mapping.getProduces() : new String[] {};

		return CtAnnotationBuilder.create(annotation, constPool)
				.addStringMember("name", name)
				.addStringMember("path", path)
				.addEnumMember("method", method)
				.addStringMember("params", params)
				.addStringMember("headers", headers)
				.addStringMember("consumes", consumes)
				.addStringMember("produces", produces).build();

	}
	
	/**
	 * 构造 @RequestMapping 注解
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * 构造 @GetMapping 注解
	 */
	public static Annotation annotGetMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, GetMapping.class, name, path, method, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 */
	public static Annotation annotPostMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PostMapping.class, name, path, method, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 */
	public static Annotation annotPutMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PutMapping.class, name, path, method, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, DeleteMapping.class, name, path, method, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PatchMapping.class, name, path, method, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 */
	private static Annotation annotHttpMethod(ConstPool constPool, Class<? extends java.lang.annotation.Annotation> annotation,
			String name, String[] path,	RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {

		name = StringUtils.hasText(name) ? name : "";
		path = ArrayUtils.isNotEmpty(path) ? path : new String[] {};
		method = ArrayUtils.isNotEmpty(method) ? method : new RequestMethod[] {};
		params = ArrayUtils.isNotEmpty(params) ? params : new String[] {};
		headers = ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
		consumes = ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
		produces = ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};

		return CtAnnotationBuilder.create(annotation, constPool)
				.addStringMember("name", name)
				.addStringMember("path", path)
				.addEnumMember("method", method)
				.addStringMember("params", params)
				.addStringMember("headers", headers)
				.addStringMember("consumes", consumes)
				.addStringMember("produces", produces).build();

	}
	
	public static CtClass makeClass(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeClass(classname);
		}
		
		/*
		 * 当 ClassPool.doPruning=true的时候，Javassist 在CtClass object被冻结时，会释放存储在ClassPool对应的数据。
		 * 这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		 */
		declaring.stopPruning(true);

		return declaring;
	}
	
	public static CtConstructor defaultConstructor(CtClass declaring) throws CannotCompileException   {
		// 默认添加无参构造器  
		CtConstructor cons = new CtConstructor(null, declaring);  
		cons.setBody("{}");  
    	return cons;
	}
	
	public static CtConstructor makeConstructor(ClassPool pool, CtClass declaring) throws NotFoundException, CannotCompileException  {

		// 添加有参构造器，注入回调接口
    	CtClass[] parameters = new CtClass[] {pool.get(InvocationHandler.class.getName())};
    	CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
    	return CtNewConstructor.make(parameters, exceptions, "{super($1);}", declaring);
    	
	}

	public static CtClass makeInterface(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeInterface(classname);
		}

		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass
		// object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		declaring.stopPruning(true);

		return declaring;
	}
	
	public static <T> void setSuperclass(ClassPool pool, CtClass declaring, Class<T> clazz)
			throws Exception {

		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superclass = pool.get(clazz.getName());
		declaring.setSuperclass(superclass);

	}
	
	public static CtClass[] makeParams(ClassPool pool, MvcParam<?>... params) throws NotFoundException {
		// 无参
		if(params == null || params.length == 0) {
			return null;
		}
		// 方法参数
		CtClass[] parameters = new CtClass[params.length];
		for(int i = 0;i < params.length; i++) {
			parameters[i] = pool.get(params[i].getType().getName());
		}

		return parameters;
	}
	
	/**
	 * 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @param ctMethod
	 * @param constPool
	 * @param path
	 * @param method
	 * @param contentType
	 * @param bound
	 * @param params
	 */
	public static void methodAnnotations(CtMethod ctMethod, ConstPool constPool, String path, RequestMethod method,
			String contentType, MvcBound bound, MvcParam<?>[] params) {

		// 获取方法属性对象
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        
        // 添加 @WebBound 注解
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
        
        // 添加 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, path, method, contentType));
        
        methodInfo.addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);
            
        }
        
	}
	
	/**
	 * 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param ctMethod
	 * @param constPool
	 * @param result
	 * @param method
	 * @param bound
	 * @param params
	 */
	public static <T> void methodAnnotations(CtMethod ctMethod, ConstPool constPool, MvcMethod method, MvcBound bound, MvcParam<?>... params) {
		
		// 获取方法属性对象
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        // 添加 @WebBound 注解
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
        
        // 添加 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, method));
        
        // 添加 @ResponseBody 注解
        if(method.isResponseBody()) {
        	Annotation annot = new Annotation(ResponseBody.class.getName(), constPool);
        	methodAttr.addAnnotation(annot);
        }
        
        methodInfo.addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);
            
        }
        
	}
	
	/**
	 * 设置方法体
	 * @throws CannotCompileException 
	 */
	public static void methodBody(CtMethod ctMethod, MvcMethod method) throws CannotCompileException {
        methodBody(ctMethod, method.getName());
	}
	
	/**
	 * 设置方法体
	 * @throws CannotCompileException 
	 */
	public static void methodBody(CtMethod ctMethod, String methodName) throws CannotCompileException {
		
		// 构造方法体
		StringBuilder body = new StringBuilder(); 
        body.append("{\n");
        	body.append("if(getHandler() != null){\n");
        		body.append("Method method = this.getClass().getDeclaredMethod(\"" + methodName + "\", $sig);");
        		body.append("return ($r)getHandler().invoke($0, method, $args);");
        	body.append("}\n"); 
	        body.append("return null;\n");
        body.append("}"); 
        // 将方法的内容设置为要写入的代码，当方法被 abstract修饰时，该修饰符被移除。
        ctMethod.setBody(body.toString());
        
	}
	
	/**
	 * 设置方法异常捕获逻辑
	 * @throws NotFoundException 
	 * @throws CannotCompileException 
	 */
	public static void methodCatch(ClassPool pool, CtMethod ctMethod) throws NotFoundException, CannotCompileException {
		
		// 构造异常处理逻辑
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
        
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Annotation annotWebBound(ConstPool constPool, MvcBound bound) {

		CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebBound.class, constPool).
			addStringMember("uid", bound.getUid());
		if (StringUtils.hasText(bound.getJson())) {
			builder.addStringMember("json", bound.getJson());
        }
		return builder.build();
		
	}
	
	/**
	 * 根据参数 构造   @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, MvcMethod method) {
		
		Annotation annot = null;
		// 多种支持请求方法
		if(method.getMethod().length > 1) {
			annot = annotGetMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
					method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		// 仅支持一种请求方式
		switch (method.getMethod()[0]) {
			case GET:{
				annot = annotGetMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case POST:{
				annot = annotPostMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PUT:{
				annot = annotPutMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case DELETE:{
				annot = annotDeleteMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PATCH:{
				annot = annotPatchMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			default:{
				annot = annotGetMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
		}
		
		return annot;
	}
	
	/**
	 * 根据参数 构造  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, String path, RequestMethod method,
			String contentType) {

		Annotation annot = null;
		// 仅支持一种请求方式
		switch (method) {
			case GET: {
				annot = CtAnnotationBuilder.create(GetMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case POST: {
				annot = CtAnnotationBuilder.create(PostMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case PUT: {
				annot = CtAnnotationBuilder.create(PutMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case DELETE: {
				annot = CtAnnotationBuilder.create(DeleteMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case PATCH: {
				annot = CtAnnotationBuilder.create(PatchMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			default: {
				annot = CtAnnotationBuilder.create(GetMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
		}

		return annot;
	}
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
	 */
	public static <T> Annotation[][] annotParams(ConstPool constPool, MvcParam<?>... params) {

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
	}
	
}