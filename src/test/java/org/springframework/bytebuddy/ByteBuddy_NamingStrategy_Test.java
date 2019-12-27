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

import org.junit.Test;
import org.springframework.bytebuddy.bytecode.EndpointApi;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.RandomString;

public class ByteBuddy_NamingStrategy_Test {

	RandomString randomString = new RandomString(8);

	@Test
	public void test1() throws InstantiationException, IllegalAccessException {

		// 动态构建
		DynamicType.Unloaded<?> dynamicType = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return "org.springframework.bytebuddy." + "SimpleName" + "$" + randomString.nextString();
			}

		}).subclass(EndpointApi.class).make();

		System.out.println(dynamicType.getTypeDescription().getCanonicalName());

		for (int i = 0; i < 5; i++) {
			// 动态构建
			DynamicType.Unloaded<?> dynamicType1 = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
				@Override
				protected String name(TypeDescription typeDescription) {
					return "org.springframework.bytebuddy." + typeDescription.getSimpleName() + "$"
							+ randomString.nextString();
				}

			}).subclass(EndpointApi.class).make();
			System.out.println(dynamicType1.getTypeDescription().getCanonicalName());
		}

		// 该方式后缀无效
		ByteBuddy byteBuddy = new ByteBuddy();
		byteBuddy.with(new NamingStrategy.PrefixingRandom("EndpointApi"));
		DynamicType.Unloaded<?> dynamicType3 = byteBuddy.subclass(EndpointApi.class).make();
		System.out.println(dynamicType3.getTypeDescription().getCanonicalName());

		// 自定义前缀
		DynamicType.Unloaded<?> dynamicTypex = new ByteBuddy().with(new NamingStrategy.PrefixingRandom("com.test"))
				.subclass(EndpointApi.class).make();
		System.out.println(dynamicTypex.getTypeDescription().getCanonicalName());

		// 自定义后缀
		ByteBuddy byteBuddy1 = new ByteBuddy();
		DynamicType.Unloaded<?> dynamicType4 = byteBuddy1.with(new NamingStrategy.SuffixingRandom("Api212sd"))
				.subclass(EndpointApi.class).make();
		System.out.println(dynamicType4.getTypeDescription().getCanonicalName());

		// 自定义前缀
		ByteBuddy byteBuddy3 = new ByteBuddy();
		ByteBuddy suffix = byteBuddy3.with(new NamingStrategy.PrefixingRandom("com.tx"));
		DynamicType.Unloaded<?> dynamicType5 = suffix.subclass(EndpointApi.class).make();
		System.out.println(dynamicType5.getTypeDescription().getCanonicalName());

	}

}
