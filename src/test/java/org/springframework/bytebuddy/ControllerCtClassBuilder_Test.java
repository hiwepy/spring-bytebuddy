package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.jws.WebParam;

import org.apache.commons.beanutils.ConstructorUtils;
import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApiCtClassBuilder;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ControllerCtClassBuilder_Test {

	//@Test
	public void testClass() throws Exception {
		
		Class<? extends Object> clazz = new EndpointApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCase1")
				.controller("")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.makeMethod("public void sayHello(String txt) { System.out.println(txt); }")
				.build();
		
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
		sayHello.invoke(ConstructorUtils.invokeConstructor(clazz, null),  " hi Hello " );
		
	}
	
	@Test
	public void testInstance() throws Exception{
		
		InvocationHandler handler = new EndpointApiInvocationHandler();
		
		Object ctObject = new EndpointApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCaseV2")
				.controller("")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.newMethod(String.class, "sayHello", new MvcParam(String.class, "text"))
				.newMethod(String.class, "sayHello2", new MvcParam(String.class, "text", WebParam.Mode.OUT))
				.toInstance(handler);
		
		Class clazz = ctObject.getClass();
		
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
				System.out.println(anno[0].toString());
			}
		}
		System.err.println("=========sayHello======================");
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		System.err.println("=========sayHello2======================");
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 " );
	}

}
