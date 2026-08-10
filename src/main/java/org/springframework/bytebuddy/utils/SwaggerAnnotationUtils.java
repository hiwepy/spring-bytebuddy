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
package org.springframework.bytebuddy.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.util.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Utility class for constructing ByteBuddy {@link AnnotationDescription}
 * instances that mirror Swagger/Springfox annotations. These annotation
 * descriptions are used to annotate dynamically generated controller
 * classes and methods with Swagger documentation metadata.
 *
 * <p>Supports the following Swagger annotations:
 * <ul>
 *   <li>{@code @Api} - class-level API documentation</li>
 *   <li>{@code @ApiIgnore} - exclude from documentation</li>
 *   <li>{@code @ApiOperation} - method-level operation documentation</li>
 *   <li>{@code @ApiImplicitParam} / {@code @ApiImplicitParams} - parameter documentation</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see EndpointApiAnnotationUtils
 * @see org.springframework.bytebuddy.bytecode.EndpointApiBuilder
 */
public class SwaggerAnnotationUtils {

	/**
	 * Constructs a {@code @Api} annotation description with full configuration.
	 *
	 * @param name           implicitly sets a tag for the operations (legacy support)
	 * @param tags           a list of tags for API documentation control
	 * @param produces       corresponds to the produces field of operations under this resource
	 * @param consumes       corresponds to the consumes field of operations under this resource
	 * @param protocols      sets specific protocols (schemes) for operations (http, https, ws, wss)
	 * @param authorizations corresponds to the security field of the Operation Object
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApi(String name, String[] tags, String produces,
			String consumes, String protocols, String[] authorizations) {
		return AnnotationDescription.Builder.ofType(Api.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.defineArray("tags", ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags)
				.define("produces", StringUtils.hasText(produces) ? produces : "")
				.define("consumes", StringUtils.hasText(consumes) ? consumes : "")
				.define("protocols", StringUtils.hasText(protocols) ? protocols : "")
				.build();
	}

	/**
	 * Constructs a {@code @Api} annotation description with name and tags only.
	 *
	 * @param name implicitly sets a tag for the operations (legacy support)
	 * @param tags a list of tags for API documentation control
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApi(String name, String... tags) {
		return AnnotationDescription.Builder.ofType(Api.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.defineArray("tags", ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags)
				.build();
	}

	/**
	 * Constructs an {@code @ApiIgnore} annotation description to exclude
	 * the annotated element from Swagger documentation.
	 *
	 * @param desc a brief description of why this element is ignored
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApiIgnore(String desc) {
		return AnnotationDescription.Builder.ofType(ApiIgnore.class)
				.define("value", StringUtils.hasText(desc) ? desc : "")
				.build();
	}

	/**
	 * Constructs an {@code @ApiOperation} annotation description for
	 * documenting a handler method.
	 *
	 * @param summary a brief summary of the operation
	 * @param notes   detailed notes about the operation
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApiOperation(String summary, String notes) {
		return AnnotationDescription.Builder.ofType(ApiOperation.class)
				.define("value", StringUtils.hasText(summary) ? summary : "")
				.define("notes", StringUtils.hasText(notes) ? notes : "")
				.build();
	}

	/**
	 * Constructs an {@code @ApiImplicitParams} annotation description
	 * containing multiple {@code @ApiImplicitParam} entries.
	 *
	 * @param params the parameter descriptions
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApiImplicitParams(MvcParam<?>... params) {
		AnnotationDescription[] paramAnnots = new AnnotationDescription[params.length];
		for (int i = 0; i < params.length; i++) {
			paramAnnots[i] = annotApiImplicitParam(params[i]);
		}
		AnnotationDescription.Builder builder = AnnotationDescription.Builder.ofType(ApiImplicitParams.class)
				.defineAnnotationArray("value", TypeDescription.ForLoadedType.of(ApiImplicitParam.class), paramAnnots);
		return	builder.build();
	}

	/**
	 * Constructs an {@code @ApiImplicitParam} annotation description for
	 * a single method parameter. The parameter type (path, query, body,
	 * header, or form) is automatically derived from the parameter's
	 * {@link MvcParam#getFrom()} value.
	 *
	 * @param param the parameter description
	 * @return the constructed {@link AnnotationDescription}
	 */
	public static AnnotationDescription annotApiImplicitParam(MvcParam<?> param) {
		String paramType = "query";
		switch (param.getFrom()) {
			case PATH: {
				paramType = "path";
			};break;
			case BODY: {
				paramType = "body";
			};break;
			case HEADER: {
				paramType = "header";
			};break;
			case PARAM: {
				paramType = "query";
			};break;
			default: {
				paramType = "form";
			};break;
		}
		return AnnotationDescription.Builder.ofType(ApiImplicitParam.class)
				.define("paramType", paramType)
				.define("name", param.getName())
				.define("value", "Param " + param.getName())
				.define("dataType", param.getType().getName())
				.define("dataTypeClass", param.getType())
				.define("defaultValue", StringUtils.hasText(param.getDef()) ? param.getDef() : "")
				.define("required", param.isRequired())
				.build();
	}

}
