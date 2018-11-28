package org.springframework.bytebuddy.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.bytebuddy.annotation.WebBound;
import org.springframework.stereotype.Controller;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;

public class EndpointApiInterceptor {

	// 通过 method 和 arguments 可获得原方法引用与实际传入参数
	// 如果不关心它们或其中某个，可在声明 intercept() 方法时省略。也能加更多参数，如 @SuperCall Callable<?> call
	public Object intercept(Object proxy, /*@Origin*/ Method method, /*@AllArguments*/ Object[] args) throws Throwable {
		
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
