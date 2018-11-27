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
package org.springframework.bytebuddy.intercept;

import java.util.List;
import java.util.concurrent.Callable;

public class LoggingMemoryDatabase extends MemoryDatabase {

	private class LoadMethodSuperCall implements Callable {

		private final String info;

		private LoadMethodSuperCall(String info) {
			this.info = info;
		}

		@Override
		public Object call() throws Exception {
			return LoggingMemoryDatabase.super.load(info);
		}
	}

	@Override
	public List<String> load(String info) {
		return LoggerInterceptor.log(new LoadMethodSuperCall(info));
	}
}