/*
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
package org.springframework.bytebuddy.intercept;

import java.lang.reflect.Method;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;

public class FindOneInterceptor {

	// 通过 method 和 arguments 可获得原方法引用与实际传入参数
	// 如果不关心它们或其中某个，可在声明 intercept() 方法时省略。也能加更多参数，如 @SuperCall Callable<?> call
	static String intercept(@Origin Method method, @AllArguments Object[] arguments) {
		return "http://unmi.cc/?p=" + arguments[0];
	}
			
}
