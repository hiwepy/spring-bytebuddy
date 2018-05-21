package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.ConstructorUtils;
import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.utils.EndpointApiUtils;
import org.springframework.stereotype.Controller;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

public class ControllerCtClassBuilder_Test {

	// @Test
	public void testClass() throws Exception {
		Implementation impl = null;
		 DynamicType.Unloaded<Object> c = new ByteBuddy().subclass(Object.class)
				 .method(ElementMatchers.isDeclaredBy(Object.class)).intercept(impl).make();
		 /*
		 
		    Class<IndexMapper> dynamicType = (Class<IndexMapper>) c.load(IndexMapper.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
		    try {
		        return dynamicType.newInstance();
		    } catch (Exception e) {
		        throw new IllegalStateException("Unable to get index mapper for rank " + rank);
		    }
		    */
		    
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
	/*
	 * <E> E someCreateMethod(Class<E> clazz) { Class<? extends E> dynamicType = new
	 * ByteBuddy() .subclass(clazz) .name("NewEntity") .method(named("getNumber"))
	 * .intercept(FixedValue.value(100)) .defineField("stringVal", String.class,
	 * Visibility.PRIVATE) .defineMethod("getStringVal", String.class,
	 * Visibility.PUBLIC) .intercept(FieldAccessor.ofBeanProperty()) .make()
	 * .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
	 * .getLoaded();
	 * 
	 * return dynamicType.newInstance(); }
	 */

	@Test
	public void testInstance() throws Exception {

		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();

		Class<?> clazz = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("Api212sd"))
				.subclass(Object.class)
				.annotateType(EndpointApiUtils.annotController("api2018"), EndpointApiUtils.annotApi( "这是一个测试API"))
		
				// 定义依赖注入的字段
				.defineField("handler", InvocationHandler.class, Modifier.PROTECTED)
				.annotateField(EndpointApiUtils.annotAutowired(true))
				// 定义普通属性字段
				.defineProperty("uid", String.class).defineProperty("name", String.class, true)
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
				
				
				.method(ElementMatchers.isToString()).intercept(SuperMethodCall.INSTANCE)
				.method(ElementMatchers.any()).intercept(InvocationHandlerAdapter.of(invocationHandler))
				
				.make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded();

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
				if (anno.length > 0) {
					System.out.println(anno[0].toString());
				}
			}
		}

		Object ctObject = clazz.newInstance();

		ctObject.toString();

		
		  System.err.println("=========sayHello======================"); Method
		  sayHello = clazz.getMethod("sayHello", String.class);
		  sayHello.invoke(ctObject, " hi Hello ");
		  System.err.println("=========sayHello2======================"); Method
		  sayHello2 = clazz.getMethod("sayHello2", String.class, String.class);
		  sayHello2.invoke(ctObject, " hi Hello2 ", " hi Hello2 ");
		 
	}

}
