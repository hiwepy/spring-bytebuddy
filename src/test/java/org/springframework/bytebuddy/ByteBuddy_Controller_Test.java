package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.utils.EndpointApiUtils;
import org.springframework.bytebuddy.utils.SwaggerApiUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddy_Controller_Test {

	@Test
	public void testClass() throws Exception {
		
		Builder<EndpointApi> builder = new ByteBuddy()
				// 指定随机命名
				.with(new NamingStrategy.SuffixingRandom("Api212sd"))
				// 继承父类
				.subclass(EndpointApi.class)
				// 添加 @Controller 注解
				.annotateType(EndpointApiUtils.annotController("api"))
				// 定义普通方法
				.defineMethod("sayHello", String.class, Modifier.PUBLIC)
				.intercept(FixedValue.value("Hello World ByteBuddy!"))
				// 定义注解方法：方法注解 + 参数注解
				.defineMethod("sayHello2", String.class, Modifier.PUBLIC)
				.withParameter(String.class, "text")
				.intercept(FixedValue.value("Hello World ByteBuddy!"));

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
		System.out.println("=========Methods======================");
		for (Method element : clazz.getDeclaredMethods()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}

	}
	
	//@Test
	public void testInstance() throws Exception {

		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();

		Class<?> clazz = new ByteBuddy()
				// 指定随机命名
				.with(new NamingStrategy.SuffixingRandom("Api212sd"))
				// 继承父类
				.subclass(EndpointApi.class)
				// 添加 @Controller, @Api 注解
				.annotateType(EndpointApiUtils.annotController("api2018"), SwaggerApiUtils.annotApi( "这是一个测试API"))
				// 定义依赖注入的字段
				.defineField("handler", InvocationHandler.class, Modifier.PROTECTED)
				.annotateField(EndpointApiUtils.annotAutowired(true), EndpointApiUtils.annotQualifier(""))
				// 定义普通属性字段
				.defineProperty("uid", String.class)
				.defineProperty("name", String.class, true)
				// 定义普通方法
				.defineMethod("sayHello", String.class, Modifier.PUBLIC)
				.intercept(FixedValue.value("Hello World ByteBuddy!"))
				// 定义注解方法：方法注解 + 参数注解
				.defineMethod("sayHello2", String.class, Modifier.PUBLIC)
				.withParameter(String.class, "ht")
				.annotateParameter(EndpointApiUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
				.withParameter(String.class, "text")
				.annotateParameter(EndpointApiUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
				.intercept(MethodDelegation.to(EndpointApi.class))
				.annotateMethod(EndpointApiUtils.annotPostMapping("xx", new String[] {"say/{text}"}, null, null, null, null))
				// 查找方法，添加拦截器
				.method(ElementMatchers.isToString()).intercept(SuperMethodCall.INSTANCE)
				.method(ElementMatchers.any()).intercept(InvocationHandlerAdapter.of(invocationHandler))
				
				.make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded();

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
		System.out.println("=========Methods======================");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				System.out.println(method.getName());
				System.out.println("=========Method Annotations======================");
				for (Annotation anno : method.getAnnotations()) {
					System.out.println(anno.toString());
				}
				System.out.println("=========Method Parameter Annotations======================");
				for (Annotation[] anno : method.getParameterAnnotations()) {
					if(anno != null && anno.length > 0) {
						System.out.println(anno[0].toString());
					}
				}
			}
			searchType = searchType.getSuperclass();
		}

		Object ctObject = clazz.newInstance();

		ctObject.toString();
		
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 " );
		 
	}

}
