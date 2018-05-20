package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;

public abstract class EndpointApi {

	private InvocationHandler handler;
	
	public EndpointApi() {
	}
	
	public EndpointApi(InvocationHandler handler) {
		this.handler = handler;
	}

	public InvocationHandler getHandler() {
		return handler;
	}
	
}