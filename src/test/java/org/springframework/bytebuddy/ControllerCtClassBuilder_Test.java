package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.ConstructorUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bytebuddy.utils.EndpointApiUtils;
import org.springframework.stereotype.Controller;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class ControllerCtClassBuilder_Test {

	// @Test
	public void testClass() throws Exception {

		Builder<Object> builder = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("Api212sd"))
				.subclass(Object.class).annotateType(new Controller() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return Controller.class;
					}

					@Override
					public String value() {
						return "";
					}
				});

		Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();

		System.err.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}

		System.err.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========Methods======================");
		for (Method element : clazz.getDeclaredMethods()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========sayHello======================");
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ConstructorUtils.invokeConstructor(clazz, null), " hi Hello ");

	}

	@Test
	public void testInstance() throws Exception {

		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();
		
		Builder<Object> builder = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("Api212sd"))
				.subclass(Object.class)
				.defineField("id", String.class, Modifier.PROTECTED).annotateField(new Autowired() {
					
					@Override
					public Class<? extends Annotation> annotationType() {
						return Autowired.class;
					}
					
					@Override
					public boolean required() {
						return false;
					}
				})
				.defineProperty("uid", String.class)
				.defineProperty("name", String.class, true);
		
		builder.defineMethod("sayHello", String.class, Modifier.PUBLIC)
			.withParameter(String.class, "text")
			.throwing(Exception.class);
		
		builder.defineMethod("sayHello2", String.class, Modifier.PUBLIC)
			.withParameter(String.class, "text")
			.withParameter(String.class, "ht")
			.throwing(Exception.class);
		
		builder.defineConstructor(Modifier.PROTECTED).withParameter(String.class);
		
		builder = EndpointApiUtils.annotController(builder, "api2018");
		builder = EndpointApiUtils.annotApi(builder, "这是一个测试API");
		
		  
		builder = builder.method(ElementMatchers.named("toString"))
		  .intercept(FixedValue.value("Hello World!"));
		 
		builder = builder.method(ElementMatchers.any())
				.intercept(InvocationHandlerAdapter.of(invocationHandler));
		
		Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();

		
		System.err.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}

		System.err.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========Methods======================");
		for (Method method : clazz.getDeclaredMethods()) {
			System.out.println(method.getName());
			System.err.println("=========Method Annotations======================");
			for (Annotation anno : method.getAnnotations()) {
				System.out.println(anno.toString());
			}
			System.err.println("=========Method Parameter Annotations======================");
			for (Annotation[] anno : method.getParameterAnnotations()) {
				if(anno.length > 0) {
					System.out.println(anno[0].toString());
				}
			}
		}
		
		Object ctObject = clazz.newInstance();
		
		ctObject.toString();
		
		/*System.err.println("=========sayHello======================");
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject, " hi Hello ");
		System.err.println("=========sayHello2======================");
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject, " hi Hello2 ");*/
	}

}
