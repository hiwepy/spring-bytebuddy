/**
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.xml.ws.Action;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddy_Annotation_Test {

	class AnnotatedMethod {
		@Action
		void bar() {
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface RuntimeDefinition {
	}

	class RuntimeDefinitionImpl implements RuntimeDefinition {
		@Override
		public Class<? extends Annotation> annotationType() {
			return RuntimeDefinition.class;
		}
	}

	@Test
	public void test1() throws InstantiationException, IllegalAccessException {

		new ByteBuddy().subclass(Object.class).annotateType(new RuntimeDefinitionImpl()).make();

	}
	
	@Test
	public void test2() throws InstantiationException, IllegalAccessException {

		new ByteBuddy().subclass(Object.class).annotateType(new RuntimeDefinitionImpl())
				.method(ElementMatchers.named("toString")).intercept(SuperMethodCall.INSTANCE)
				.annotateMethod(new RuntimeDefinitionImpl()).defineField("foo", Object.class)
				.annotateField(new RuntimeDefinitionImpl());

	}

	@Test
	public void test3() throws InstantiationException, IllegalAccessException {

		new ByteBuddy().subclass(AnnotatedMethod.class).method(ElementMatchers.named("bar"))
				.intercept(StubMethod.INSTANCE)
				.attribute(MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER);

	}

}
