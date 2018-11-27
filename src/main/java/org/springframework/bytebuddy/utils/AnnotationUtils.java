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
package org.springframework.bytebuddy.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

import org.apache.harmony.lang.annotation.AnnotationFactory;
import org.apache.harmony.lang.annotation.AnnotationMember;

public class AnnotationUtils {

	/*
	 * Provides a new annotation instance.
	 * @param annotationType the annotation type definition
	 * @param elements name-value pairs representing elements of the annotation
	 * @return a new annotation instance
	 */
	public static Annotation create(Class<? extends Annotation> annotationType, AnnotationMember[] members) {
		AnnotationFactory antn = new AnnotationFactory(annotationType, members);
		return (Annotation) Proxy.newProxyInstance(annotationType.getClassLoader(), annotationType.getInterfaces(), antn);
	}

}
