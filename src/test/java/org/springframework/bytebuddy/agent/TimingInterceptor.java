package org.springframework.bytebuddy.agent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class TimingInterceptor {
	
	@RuntimeType
	public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
		long start = System.currentTimeMillis();
		try {
			return callable.call();
		} finally {
			System.out.println(method + " took " + (System.currentTimeMillis() - start));
		}
	}
	
}
