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

import java.io.File;

import org.junit.Test;
import org.springframework.bytebuddy.intercept.FindOneInterceptor;
import org.springframework.bytebuddy.intercept.LoggerInterceptor;
import org.springframework.bytebuddy.intercept.MemoryDatabase;
import org.springframework.bytebuddy.intercept.Repository;
import org.springframework.bytebuddy.intercept.Scope;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * https://unmi.cc/leverage-bytebuddy-generate-generic-subclass/#more-7792
 */
public class ByteBuddy_GenericSubclass_Test {

	@Test
	public void test1() throws Exception {
		
		// 泛型类型需要这么声明参数类型
		TypeDescription.Generic genericSuperClass = TypeDescription.Generic.Builder
				.parameterizedType(Repository.class, String.class).build();

		// new ByteBuddy().subclass(Repository.class) //简单非泛型类可以这么做
		DynamicType.Unloaded<?> unloadedType = new ByteBuddy()
				.subclass(genericSuperClass)
				.name(Repository.class.getPackage().getName().concat(".").concat("UserRepository"))
				.method(ElementMatchers.named("findOne")) // ElementMatchers 提供了多种方式找到方法
				// .intercept(FixedValue.value("Yanbin")) //最简单的方式就是返回一个固定值
				.intercept(MethodDelegation.to(FindOneInterceptor.class)) // 使用 FindOneInterCeptor 中的实现，定义在下方
				.annotateType(AnnotationDescription.Builder.ofType(Scope.class).define("value", "Session").build())
				.make();

		// 在 Maven 项目中，写类文件在 target/classes/cc/unmi/UserRepository.class 中
		unloadedType.saveIn(new File("target/classes"));

		// 可以这样生成字节码得到 Class 实例来加载使用
		// Class<?> subClass = unloadedType.load(Main.class.getClassLoader(),
		// ClassLoadingStrategy.Default.WRAPPER).getLoaded();

		Class<Repository<String>> repositoryClass = (Class<Repository<String>>) Class.forName("org.apache.cxf.endpoint.UserRepository");
		System.out.println(repositoryClass.getAnnotation(Scope.class).value()); // 输出 Session

		Repository<String> repository = repositoryClass.newInstance();
		System.out.println(repository.findOne(7792)); // 输出 http://unmi.cc/?p=7792
	}
	
	@Test
	public void test3() throws InstantiationException, IllegalAccessException {
		MemoryDatabase loggingDatabase = new ByteBuddy()
				.subclass(MemoryDatabase.class)
				.method(ElementMatchers.named("load"))
				.intercept(MethodDelegation.to(LoggerInterceptor.class)).make()
				.load(getClass().getClassLoader())
				.getLoaded()
				.newInstance();
	}
	
}