/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.bytebuddy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for binding data to dynamically generated endpoint classes
 * or methods. When applied to a class or method, it associates a unique
 * identifier ({@link #uid()}) and a JSON payload ({@link #json()}) with
 * the annotated element, enabling data-driven endpoint generation.
 *
 * <p>This annotation is typically used in conjunction with
 * {@code EndpointApiBuilder} to bind request data to dynamically
 * constructed Spring MVC controller classes or handler methods.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.springframework.bytebuddy.bytecode.EndpointApiBuilder
 * @see org.springframework.bytebuddy.bytecode.ReactiveHandlerBuilder
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebBound {

	/**
	 * A unique identifier for the data binding, typically a primary key
	 * or identifier used to look up data in the implementing handler.
	 *
	 * @return the unique identifier string, or an empty string by default
	 */
	String uid() default "";

	/**
	 * A JSON-formatted string containing the data payload to be bound
	 * to the annotated element.
	 *
	 * @return the JSON payload string, or {@code "{}"} by default
	 */
	String json() default "{}";

}
