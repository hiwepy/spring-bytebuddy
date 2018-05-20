/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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

import java.lang.reflect.InvocationHandler;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.springframework.bytebuddy.bytecode.EndpointApi;

@WebService(serviceName = "sample", // 与接口中指定的name一致
		targetNamespace = "http://ws.cxf.com/"// , // 与接口中的命名空间一致,一般是接口的包名倒
// endpointInterface = "org.apache.cxf.spring.boot.api.JaxwsSample"// 接口地址
)
public class EndpointApiSample extends EndpointApi {

	public EndpointApiSample() {
	}

	public EndpointApiSample(InvocationHandler handler) {
		super(handler);
	}

	/*
	 * action 需要填写完成，有发现使用Soap接口调用无法识别
	 */
	@WebMethod(operationName = "sayHello", action = "http://ws.cxf.com/sayHello/")
	@WebResult(name = "String", targetNamespace = "")
	public String sayHello(@WebParam(name = "userName") String name) {
		
		//getHandler().invoke(this, method, args);
		//Method method = this.getClass().getDeclaredMethod(name, $sig);
		//getHandler().invoke($0, method, $args);
		
		return "Hello ," + name;
	}

	@WebMethod(operationName = "sayHello2", action = "http://ws.cxf.com/sayHello/")
	@WebResult(name = "String", targetNamespace = "")
	public String sayHello2(@WebParam(name = "userName") String name) {
		return "Hello ," + name;
	}
	
	@WebMethod(operationName = "invoke", action = "http://ws.cxf.com/sayHello/")
	@WebResult(name = "String", targetNamespace = "")
	public String invoke(@WebParam(name = "userName") String name) {
		
		//this.getHandler().invoke(this, method, args)
		
		return "Hello ," + name;
	}
	
}