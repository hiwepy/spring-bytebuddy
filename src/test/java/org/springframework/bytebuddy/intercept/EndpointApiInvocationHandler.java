package org.springframework.bytebuddy.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.stereotype.Controller;

public class EndpointApiInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("=========Method Invoke:" + method.getName() + " ======================");
		
		System.out.println("Declaring Class: " + method.getDeclaringClass());
		Controller controller = proxy.getClass().getAnnotation(Controller.class);
		System.out.println("Controller: " +controller);
		
		// 获取方法上绑定的数据注解
		WebBound bound = method.getAnnotation(WebBound.class);
		if(bound == null) {
			Class<?> declaringClass = method.getDeclaringClass();
			bound = declaringClass.getAnnotation(WebBound.class);
		}
		System.out.println("WebBound: " + bound);
		
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
		System.out.println("=========Method Parameters======================");
		for (Object arg : args) {
			System.out.println(arg.toString());
		}
		
		return null;
		
	}

}
