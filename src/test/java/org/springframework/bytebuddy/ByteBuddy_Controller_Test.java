package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.intercept.EndpointApiInterceptor;
import org.springframework.bytebuddy.intercept.EndpointApiInvocationHandler;
import org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils;
import org.springframework.bytebuddy.utils.SwaggerAnnotationUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * http://bytebuddy.net/#/tutorial
 * http://xiangshouxiyang.iteye.com/blog/2377664
 * @author <a href="https://github.com/vindell">vindell</a>
 */
public class ByteBuddy_Controller_Test {

	
	@Test
	public void testProxy() throws Exception {

		System.out.println("=========Proxy======================");
		
		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();
		DynamicType.Unloaded<?> unloadedType = new ByteBuddy()
				// 指定随机命名
				.with(new NamingStrategy.SuffixingRandom("ApiProxy"))
				// 继承父类
				.subclass(EndpointApi.class)
					// 添加 @Controller, @Api 注解
					.annotateType(EndpointApiAnnotationUtils.annotController("api2018"), 
						SwaggerAnnotationUtils.annotApi( "这是一个测试API"),
						EndpointApiAnnotationUtils.annotBound(new MvcBound("xxsdfasd")))
				//.name("org.springframework.bytebuddy.ProxyTest")
				// 定义依赖注入的字段
				.defineField("handler", InvocationHandler.class, Modifier.PROTECTED)
					.annotateField(EndpointApiAnnotationUtils.annotAutowired(false), 
						EndpointApiAnnotationUtils.annotQualifier("handler"))
				// 定义普通属性字段
				.defineProperty("uid", String.class)
				.defineProperty("name", String.class, true)
				// 定义普通方法
				.defineMethod("sayHello", String.class, Modifier.PUBLIC)
					.withParameter(String.class, "text", ModifierContributor.ForParameter.MASK)
					.throwing(Exception.class)
					.intercept(StubMethod.INSTANCE)
				// 定义注解方法：方法注解 + 参数注解
				.defineMethod("sayHello2", String.class, Modifier.PUBLIC)
					.withParameter(String.class, "ht", ModifierContributor.ForParameter.MASK)
					//.annotateParameter(EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "ht")))
					.withParameter(String.class, "text", ModifierContributor.ForParameter.MASK)
					//.annotateParameter(EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
					.throwing(Exception.class)
					.intercept(StubMethod.INSTANCE)
					.annotateMethod(EndpointApiAnnotationUtils.annotPostMapping("xx", new String[] {"say/{text}"}, null, null, null, null))
					.annotateParameter(0, EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "ht")))
					.annotateParameter(1, EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
				// 查找方法，添加拦截器
				.method(ElementMatchers.isToString()).intercept(SuperMethodCall.INSTANCE)
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
				.attribute(MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER)
				.make();
		
		// 在 Maven 项目中，写类文件在 target/classes/cc/unmi/UserRepository.class 中
		//unloadedType.saveIn(new File("target/classes"));
		
		//Class<?> clazz = Class.forName("org.springframework.bytebuddy.ProxyTest");
		
		Class<?> clazz = unloadedType.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

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
		for (Method method : clazz.getDeclaredMethods()) {
			System.out.println(method.getName());
			for (Annotation anno : method.getAnnotations()) {
				System.out.println(anno.toString());
			}
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
		
		Object ctObject = clazz.newInstance();
		
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		Method sayHello2 = clazz.getMethod("sayHello2", String.class, String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 ", "wsw" );

	}
	
	//@Test
	public void testDelegate() throws Exception {

		System.out.println("=========Delegate======================");
		
		DynamicType.Unloaded<?> unloadedType = new ByteBuddy()
				// 指定随机命名
				.with(new NamingStrategy.SuffixingRandom("ApiDelegate"))
				// 继承父类
				.subclass(EndpointApi.class)
					// 添加 @Controller, @Api 注解
					.annotateType(EndpointApiAnnotationUtils.annotController("api2018"), 
						SwaggerAnnotationUtils.annotApi( "这是一个测试API"),
						EndpointApiAnnotationUtils.annotBound(new MvcBound("xxsdfasd")))
				//.name("org.springframework.bytebuddy.DelegateTest")
				// 定义依赖注入的字段
				.defineField("handler", InvocationHandler.class, Modifier.PROTECTED)
					.annotateField(EndpointApiAnnotationUtils.annotAutowired(false), 
						EndpointApiAnnotationUtils.annotQualifier("handler"))
				// 定义普通属性字段
				.defineProperty("uid", String.class)
				.defineProperty("name", String.class, true)
				// 定义普通方法
				.defineMethod("sayHello", String.class, Modifier.PUBLIC)
					.withParameter(String.class, "text", ModifierContributor.ForParameter.MASK)
					.throwing(Exception.class)
					.intercept(StubMethod.INSTANCE)
				// 定义注解方法：方法注解 + 参数注解
				.defineMethod("sayHello2", String.class, Modifier.PUBLIC)
					.withParameter(String.class, "ht", ModifierContributor.ForParameter.MASK)
					//.annotateParameter(EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "ht")))
					.withParameter(String.class, "text", ModifierContributor.ForParameter.MASK)
					//.annotateParameter(EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
					.throwing(Exception.class)
					.intercept(StubMethod.INSTANCE)
					.annotateMethod(EndpointApiAnnotationUtils.annotPostMapping("xx", new String[] {"say/{text}"}, null, null, null, null))
					.annotateParameter(0, EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "ht")))
					.annotateParameter(1, EndpointApiAnnotationUtils.annotPathVariable(new MvcParam<>(String.class, "text")))
				// 查找方法，添加拦截器
				.method(ElementMatchers.isToString()).intercept(SuperMethodCall.INSTANCE)
				.method(ElementMatchers.nameStartsWith("sayHello"))
				.intercept(MethodDelegation.to(EndpointApiInterceptor.class))
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
				.attribute(MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER)
				.make();
		
		// 在 Maven 项目中，写类文件在 target/classes/cc/unmi/UserRepository.class 中
		//unloadedType.saveIn(new File("target/classes"));
		
		//Class<?> clazz = Class.forName("org.springframework.bytebuddy.DelegateTest");
		
		Class<?> clazz = unloadedType.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

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
