/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package org.springframework.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.EndpointApiBuilder;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import net.bytebuddy.dynamic.DynamicType.Builder;

public class Bytebuddy_EndpointApiBuilder_Test {

	@Test
	public void testClass() throws Exception {

		InvocationHandler invocationHandler = new EndpointApiInvocationHandler();

		Builder<EndpointApi> builder = new EndpointApiBuilder<EndpointApi>()
				.newMethod("sayHello", "say/{word}", RequestMethod.POST, MediaType.ALL_VALUE, new MvcBound("100212"),
						new MvcParam<String>(String.class, "text"))
				.newMethod(ResponseEntity.class,
						new MvcMethod("sayHello2", new String[] { "say2/{word}", "say22/{word}" }, RequestMethod.POST,
								RequestMethod.GET),
						new MvcBound("100212"), new MvcParam<String>(String.class, "word", MvcParamFrom.PATH))

				// 添加 @Controller 注解
				// .controller("api")
				.intercept(invocationHandler).build().defineField("xxx", String.class);

		Class<?> clazz = builder.make().load(getClass().getClassLoader()).getLoaded();

		System.out.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.out.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}

		Object ctObject = clazz.newInstance();

		System.out.println("=========Methods======================");
		
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 " );

	}

}
