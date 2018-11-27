package org.springframework.bytebuddy.intercept;

public class GreetingInterceptor {
	
	public Object greet(Object argument) {
		return "Hello from " + argument;
	}
	
}