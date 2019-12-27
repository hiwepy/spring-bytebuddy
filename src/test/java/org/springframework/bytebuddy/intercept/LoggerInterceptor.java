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
package org.springframework.bytebuddy.intercept;

import java.util.List;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class LoggerInterceptor {
	
	public static List<String> log(@SuperCall Callable<List<String>> zuper)  {
		System.out.println("Calling database");
		try {
			return zuper.call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			System.out.println("Returned from database");
		}
	}
	
}