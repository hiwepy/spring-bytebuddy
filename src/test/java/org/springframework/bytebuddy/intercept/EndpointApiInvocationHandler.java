package org.springframework.bytebuddy.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.stereotype.Controller;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.bind.annotation.Origin;

public class EndpointApiInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(@Origin Object proxy, @Origin Method method, Object[] args) throws Throwable {
		
		System.out.println("=========Method Invoke:" + method.getName() + " ======================");
		
		
		Controller controller = proxy.getClass().getAnnotation(Controller.class);
		System.out.println(controller);
		
		// 获取方法上绑定的数据注解
		WebBound bound = method.getAnnotation(WebBound.class);
		System.out.println(method.getDeclaringClass());
		if(bound == null) {
			Class<?> declaringClass = method.getDeclaringClass();
			bound = declaringClass.getAnnotation(WebBound.class);
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
		System.out.println("=========Method Parameters======================");
		for (Object arg : args) {
			System.out.println(arg.toString());
		}
		
		return null;
		
	}

}
