package org.springframework.bytebuddy;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.EndpointApiBuilder;
import org.springframework.bytebuddy.bytecode.ReactiveHandlerBuilder;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.intercept.EndpointApiInvocationHandler;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * http://bytebuddy.net/#/tutorial
 * http://xiangshouxiyang.iteye.com/blog/2377664
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class ByteBuddy_ReactiveHandler_Test {

	
	@Test
	public void testProxy() throws Exception {

		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();

		Builder<EndpointApi> builder = new ReactiveHandlerBuilder<EndpointApi>()
				.monoMethod("sayHello", new MvcBound("100212"))
				.fluxMethod("sayHello2", new MvcBound("100212"))
				.bind(new MvcBound("12014"))
				.proxy(invocationHandler)
				.then()
				.defineField("uid", String.class, Modifier.PROTECTED)
				.method(ElementMatchers.nameStartsWith("sayHello")).intercept(InvocationHandlerAdapter.of(invocationHandler))
				/**
				 * 原文：
				 * By default, a ByteBuddy configuration does not predefine any annotations for a dynamically 
				 * created type or type member. However, this behavior can be altered by providing a default 
				 * TypeAttributeAppender, MethodAttributeAppender or FieldAttributeAppender. 
				 * Note that such default appenders are not additive but replace their former values.
				 * 翻译：
				 * 默认情况下，ByteBuddy配置不会为动态创建的类型或类型成员预定义任何注释。 
				 * 但是，可以通过提供默认的TypeAttributeAppender，MethodAttributeAppender或FieldAttributeAppender来更改此行为。 
				 * 请注意，此类默认附加程序不是附加的，而是替换它们以前的值。
				 */
				.attribute(MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER);
		

		Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();

		System.out.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.out.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}

		Object ctObject = clazz.newInstance();
		
		System.out.println("=========Methods======================");
		
		Method sayHello = clazz.getMethod("sayHello", ServerRequest.class);
		sayHello.invoke(ctObject,  new Object[] {} );
		Method sayHello2 = clazz.getMethod("sayHello2", ServerRequest.class);
		sayHello2.invoke(ctObject,  new Object[] {} );

	}
	
	//@Test
	public void test2() throws Exception {

		Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
				.newMethod("sayHello", "say/{word}", RequestMethod.POST, MediaType.APPLICATION_JSON_VALUE, new MvcBound("100212"),
						 new MvcParam<String>(String.class, "text"))
				/*
				.newMethod(ResponseEntity.class,
						new MvcMethod("sayHello2", new String[] { "say2/{word}", "say22/{word}" }, RequestMethod.POST,
								RequestMethod.GET),
						new MvcBound("100212"), new MvcParam<String>(String.class, "word", MvcParamFrom.PATH))
*/
				// 添加 @Controller 注解
				.restController()
				//.delegate(EndpointApiInterceptor.class)
				.then()
				.defineField("xxx", String.class);
		
		DynamicType.Unloaded<?> unloadedType = builder.make();
		
		// 在 Maven 项目中，写类文件在 target/classes/cc/unmi/UserRepository.class 中
		unloadedType.saveIn(new File("target/classes"));
		
		Class<?> clazz = unloadedType.load(getClass().getClassLoader()).getLoaded();

		System.out.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.out.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}

		Object ctObject = clazz.newInstance();

		System.out.println("=========Methods======================");
		
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 " );

	}

}
