package org.springframework.bytebuddy.bytecode;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract base handler for reactive endpoint implementations. Provides
 * default (empty) implementations for {@link Mono} and {@link Flux}
 * response methods. Subclasses should override these methods to provide
 * actual reactive request-handling logic.
 *
 * <p>This class is typically used with {@link ReactiveHandlerBuilder}
 * to generate dynamic reactive endpoint classes via ByteBuddy delegation.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ReactiveHandlerBuilder
 * @see EndpointApi
 */
public abstract class ReactiveHandler {

	/**
	 * Handles an incoming request and returns a single reactive response.
	 *
	 * @param request the incoming server request
	 * @return a {@link Mono} emitting the server response, or empty by default
	 */
	public Mono<ServerResponse> mono(ServerRequest request){
		return Mono.empty();
	}

	/**
	 * Handles an incoming request and returns a stream of reactive responses.
	 *
	 * @param request the incoming server request
	 * @return a {@link Flux} emitting server responses, or empty by default
	 */
	public Flux<ServerResponse> flux(ServerRequest request){
		return Flux.empty();
	}

}
