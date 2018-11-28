package org.springframework.bytebuddy.bytecode;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ReactiveHandler {
	
	public Mono<ServerResponse> mono(ServerRequest request){
		return Mono.empty();
	}
	
	public Flux<ServerResponse> flux(ServerRequest request){
		return Flux.empty();
	}
	
}