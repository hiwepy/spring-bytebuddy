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

public class SwaggerAnnotationUtils {

	/**
	 * 构造 @Api 注解
	 * @param name 			: Implicitly sets a tag for the operations, legacy support (read description).
	 * @param tags 			: A list of tags for API documentation control.
	 * @param produces 		: Corresponds to the `produces` field of the operations under this resource.
	 * @param consumes 		: Corresponds to the `consumes` field of the operations under this resource.For example, "application/json, application/xml" would suggest the operations accept JSON and XML input.
	 * @param protocols		: Sets specific protocols (schemes) for the operations under this resource. Possible values: http, https, ws, wss.
	 * @param authorizations: Corresponds to the `security` field of the Operation Object.   
	 * @see {@link @Api}
	 * @return
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
	 * 构造 @Api 注解
	 * @param name : Implicitly sets a tag for the operations, legacy support (read description).
	 * @param tags : A list of tags for API documentation control.
	 * @return
	 */
	public static AnnotationDescription annotApi(String name, String... tags) {
		return AnnotationDescription.Builder.ofType(Api.class)
				.define("value", StringUtils.hasText(name) ? name : "")
				.defineArray("tags", ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags)
				.build();
	} 
	
	/**
	 * 构造 @ApiIgnore 注解
	 */
	public static AnnotationDescription annotApiIgnore(String desc) {
		return AnnotationDescription.Builder.ofType(ApiIgnore.class)
				.define("value", StringUtils.hasText(desc) ? desc : "")
				.build();
	}
	
	/**
	 * 构造 @ApiOperation 注解
	 * @param summary	: 接口概述
	 * @param notes		: 接口注意事项
	 * @return
	 */
	public static AnnotationDescription annotApiOperation(String summary, String notes) {
		return AnnotationDescription.Builder.ofType(ApiOperation.class)
				.define("value", StringUtils.hasText(summary) ? summary : "")
				.define("notes", StringUtils.hasText(notes) ? notes : "")
				.build();
	}
	
	/**
	 * 构造 @ApiImplicitParams 注解
	 * @param summary	: 接口概述
	 * @param notes		: 接口注意事项
	 * @return
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
				//.define("allowableValues", StringUtils.hasText(notes) ? notes : "")
				.define("required", param.isRequired())
				.build();
	}
	
}