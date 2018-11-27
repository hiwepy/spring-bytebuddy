package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ReactiveHandler extends EndpointApi {

	public ReactiveHandler() {
	}
	
	public ReactiveHandler(InvocationHandler handler) {
		super(handler);
	}
	
	public Mono<ServerResponse> mono(ServerRequest request){
		return Mono.empty();
	}
	
	public Flux<ServerResponse> flux(ServerRequest request){
		return Flux.empty();
	}
	
}