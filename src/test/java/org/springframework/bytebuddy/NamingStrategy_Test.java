/**
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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

public class NamingStrategy_Test {
	public static void main(String[] args) {
		DynamicType.Unloaded<?> dynamicType = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return "i.love.ByteBuddy." + typeDescription.getSimpleName();
			}

		}).subclass(Object.class).make();
		DynamicType.Unloaded<?> dynamicType1 = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return "i.love.ByteBuddy." + typeDescription.getSimpleName();
			}

		}).subclass(Object.class).make();
		System.out.println(dynamicType.getTypeDescription().getCanonicalName());
		System.out.println(dynamicType1.getTypeDescription().getCanonicalName());

		ByteBuddy byteBuddy = new ByteBuddy();
		byteBuddy.with(new NamingStrategy.SuffixingRandom("suffix"));
		DynamicType.Unloaded<?> dynamicType3 = byteBuddy.subclass(Object.class).make();
		System.out.println(dynamicType3.getTypeDescription().getCanonicalName());

		ByteBuddy byteBuddy1 = new ByteBuddy();
		DynamicType.Unloaded<?> dynamicType4 = byteBuddy1.with(new NamingStrategy.SuffixingRandom("suffix"))
				.subclass(Object.class).make();
		System.out.println(dynamicType4.getTypeDescription().getCanonicalName());

		ByteBuddy byteBuddy3 = new ByteBuddy();
		ByteBuddy suffix = byteBuddy3.with(new NamingStrategy.SuffixingRandom("suffix"));
		DynamicType.Unloaded<?> dynamicType5 = suffix.subclass(Object.class).make();
		System.out.println(dynamicType5.getTypeDescription().getCanonicalName());
	}
}
