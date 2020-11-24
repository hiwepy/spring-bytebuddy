package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 动态构建Controller接口
 */
public class ReactiveHandlerBuilder<T extends EndpointApi> {
	
	// 构建动态类
	protected Builder<? extends EndpointApi> builder = null;
	protected RandomString randomString = new RandomString(8);
	protected static final String PREFIX = "org.springframework.bytebuddy.endpoint.";

	public ReactiveHandlerBuilder() {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			
			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}
			
		}).subclass(EndpointApi.class);
		
	}

	public ReactiveHandlerBuilder(String prefix, boolean randomName) {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return prefix + typeDescription.getSimpleName() + (randomName ? ("$" + randomString.nextString()) : "");
			}

		}).subclass(EndpointApi.class);

	}

	/**
	 * @param name The fully qualified name of the generated class in a binary format.
	 */
	public ReactiveHandlerBuilder(String name) {
		builder = new ByteBuddy().subclass(EndpointApi.class).name(name);
	}

	/**
	 * 自定义命名策略
	 * @param namingStrategy ： The naming strategy to apply when creating a new auxiliary type.
	 */
	public ReactiveHandlerBuilder(final NamingStrategy namingStrategy) {
		builder = new ByteBuddy().with(namingStrategy).subclass(EndpointApi.class);
	}
	
	/**
	 * 添加字段注解 @Autowired 实现对象注入
	 * @param name		： The name attribute value of @Autowired 
	 * @param type		： The type attribute value of @Autowired 
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> autowired(String name, Class<?> type, boolean required) {
		// 定义依赖注入的字段
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationUtils.annotAutowired(required));
		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired @Qualifier 实现对象注入
	 * @param type		： The type attribute value of @Autowired 
	 * @param name		： The name attribute value of @Autowired 
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param qualifier ： The qualifier attribute value of @Autowired 
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> autowired( String name, Class<T> type, boolean required, String qualifier) {
		// 定义依赖注入的字段
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationUtils.annotAutowired(required),
				EndpointApiAnnotationUtils.annotQualifier(qualifier));
		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param uid			: The value of uid
	 * @param json			: The value of json
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param bound			: The {@link MvcBound} instance
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> bind(final MvcBound bound) {
		builder = builder.annotateType(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}
	
	/**
	 * 构造一个返回类型为{@link reactor.core.publisher.Mono} 新的方法
	 * @param name	  ： 方法名称
	 * @param bound  ：方法绑定数据信息
	 * @return {@link ReactiveHandlerBuilder} instance
	 */ 
	public ReactiveHandlerBuilder<T> monoMethod(final String name, final MvcBound bound) {
		// 定义注解方法：方法注解
		builder = builder.defineMethod(name, Mono.class, Modifier.PUBLIC)
				.withParameter(ServerRequest.class, "request")
				.throwing(Throwable.class) /*统一抛出异常以便外部处理*/ 
				.intercept(StubMethod.INSTANCE)/*根据方法还回类型,自动构建不同类型的return代码*/
				.annotateMethod(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}
	
	/**
	 * 构造一个返回类型为{@link reactor.core.publisher.Flux} 新的方法
	 * @param name	  ： 方法名称
	 * @param bound  ：方法绑定数据信息
	 * @return {@link ReactiveHandlerBuilder} instance
	 */ 
	public ReactiveHandlerBuilder<T> fluxMethod(final String name, final MvcBound bound) {
		// 定义注解方法：方法注解
		builder = builder.defineMethod(name, Flux.class, Modifier.PUBLIC)
				.withParameter(ServerRequest.class, "request")
				.throwing(Throwable.class) /*统一抛出异常以便外部处理*/ 
				.intercept(StubMethod.INSTANCE)/*根据方法还回类型,自动构建不同类型的return代码*/
				.annotateMethod(EndpointApiAnnotationUtils.annotBound(bound));
		return this;
	}
	
	/**
	 * 为动态方法添加代理实现
	 * @param handler  	： 代理实现对象
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> proxy(final InvocationHandler handler) {
		builder = builder.method(ElementMatchers.returns(Mono.class)
						.or(ElementMatchers.returns(Flux.class)))
				.intercept(InvocationHandlerAdapter.of(handler));
		return this;
	}
	
	
	/**
	 * 为动态方法添加代理实现
	 * @param handler  	: 代理实现对象类型
	 * @return {@link ReactiveHandlerBuilder} instance
	 */
	public ReactiveHandlerBuilder<T> delegate(final Class<?> handler) {
		builder = builder.method(ElementMatchers.returns(Mono.class)
				.or(ElementMatchers.returns(Flux.class)))
				.intercept(MethodDelegation.to(handler));
		return this;
	}

	@SuppressWarnings("unchecked")
	public Builder<T> then() {
		return (Builder<T>) builder;
	}

}