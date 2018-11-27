package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.bytebuddy.annotation.WebBound;

public class EndpointApiInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("=========Method Invoke:" + method.getName() + " ======================");
		
		// 获取方法上绑定的数据注解
		WebBound bound = method.getAnnotation(WebBound.class);
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
		
		for (Object arg : args) {
			System.out.println(arg.toString());
		}
		
		return null;
		
	}

}
