package org.springframework.bytebuddy.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.utils.EndpointApiAnnotationDescriptionUtils;
import org.springframework.bytebuddy.utils.SwaggerApiAnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Annotatable;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;

/**
 * 动态构建Controller接口
 */
public class EndpointApiBuilder<T extends EndpointApi>{
	
	// 构建动态类
	protected Builder<? extends EndpointApi> builder = null;
	protected RandomString randomString = new RandomString(8);
	protected static final String PREFIX = "org.springframework.bytebuddy.endpoint.";

	public EndpointApiBuilder() {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			
			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}
			
		})
		// 继承父类
		.subclass(EndpointApi.class);
		
	}

	/**
	 * @param prefix
	 * @param randomName
	 */
	public EndpointApiBuilder(String prefix, boolean randomName) {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return prefix + typeDescription.getSimpleName() + (randomName ? ("$" + randomString.nextString()) : "");
			}

		})
		// 继承父类
		.subclass(EndpointApi.class);

	}

	/**
	 * @param name The fully qualified name of the generated class in a binary format.
	 */
	public EndpointApiBuilder(String name) {
		builder = new ByteBuddy().subclass(EndpointApi.class).name(name);
	}

	/**
	 * 自定义命名策略
	 * @param namingStrategy : The naming strategy to apply when creating a new auxiliary type.
	 */
	public EndpointApiBuilder(final NamingStrategy namingStrategy) {
		builder = new ByteBuddy().with(namingStrategy).subclass(EndpointApi.class);
	}
	
	/**
	 * 添加类注解 @Api
	 * @param name : 接口名称
	 * @param tags : 接口标签名称
	 * @param <T>  : 参数泛型
	 * @return {@link EndpointApiBuilder} instance 
	 */
	public EndpointApiBuilder<T> api(String name, String... tags) {
		builder = builder.annotateType(SwaggerApiAnnotationUtils.annotApi(name,tags));
		return this;
	}

	/**
	 * 添加类注解  @ApiIgnore
	 * @param desc : 忽略说明
	 * @param <T>  : 参数泛型
	 * @return {@link EndpointApiBuilder} instance
	 */
	public EndpointApiBuilder<T> apiIgnore(String desc) {
		builder = builder.annotateType(SwaggerApiAnnotationUtils.annotApiIgnore(desc));
		return this;
	}
	
	/**
	 * 添加类注解 @Controller
	 * @param <T>  : 参数泛型
	 * @return {@link EndpointApiBuilder} instance
	 */
	public EndpointApiBuilder<T> controller() {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotController(""));
		return this;
	}
	
	/**
	 * 添加类注解 @Controller
	 * @param name : Controller映射地址
	 * @param <T>  : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> controller(String name) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotController(name));
		return this;
	}
	
	/**
	 * 添加类注解 @RestController
	 * @param <T>  : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> restController() {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotRestController(""));
		return this;
	}
	
	/**
	 * 添加类注解 @RestController
	 * @param name : Controller映射地址
	 * @param <T>  : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> restController(String name) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotRestController(name));
		return this;
	}
	
	/**
	 * 添加类注解 @RequestMapping
	 * @param mapping			: The {@link MvcMapping} instance
	 * @param <T>  : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> requestMapping(MvcMapping mapping) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotRequestMapping(mapping));
		return this;
	}

	/**
	 * 添加类注解 @RequestMapping
	 * @param path			: The path attribute values of @RequestMapping
	 * @param <T>  : 参数泛型 
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> requestMapping(String path) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotRequestMapping(null, new String[] { path }, null,
				null, null, null, null));
		return this;
	}
	
	/**
	 * 添加类注解 @RequestMapping
	 * @param name 			: The name attribute value of @RequestMapping 
	 * @param path			: The path attribute values of @RequestMapping 
	 * @param method		: The method attribute values of @RequestMapping 
	 * @param params		: The params attribute values of @RequestMapping 
	 * @param headers		: The headers attribute values of @RequestMapping 
	 * @param consumes		: The consumes attribute values of @RequestMapping 
	 * @param produces		: The produces attribute values of @RequestMapping
	 * @param <T>  			: 参数泛型 
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> requestMapping(String name, String[] path, RequestMethod[] method,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotRequestMapping(name, path, method,
				params, headers, consumes, produces));
		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired 实现对象注入
	 * @param name		: The name attribute value of @Autowired 
	 * @param type		: The type attribute value of @Autowired 
	 * @param required 	: Declares whether the annotated dependency is required.
	 * @param <T> 	  	: 参数泛型 
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> autowired(String name, Class<?> type, boolean required) {
		// 定义依赖注入的字段
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationDescriptionUtils.annotAutowired(required));
		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired @Qualifier 实现对象注入
	 * @param type		: The type attribute value of @Autowired 
	 * @param name		: The name attribute value of @Autowired 
	 * @param required 	: Declares whether the annotated dependency is required.
	 * @param qualifier : The qualifier attribute value of @Autowired 
	 * @param <T> 	   : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> autowired( String name, Class<?> type, boolean required, String qualifier) {
		// 定义依赖注入的字段
		builder = builder.defineField(name, type, Modifier.PROTECTED).annotateField(EndpointApiAnnotationDescriptionUtils.annotAutowired(required),
				EndpointApiAnnotationDescriptionUtils.annotQualifier(qualifier));
		return this;
	}
	
	/**
	 * 添加handler字段注解 @Autowired 实现对象注入
	 * @param required 	: Declares whether the annotated dependency is required.
	 * @param qualifier : The qualifier attribute value of @Autowired
	 * @param <T>  		: 参数泛型 
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> autowiredHandler(boolean required, String qualifier) {
		// 查找依赖注入的字段
		builder = builder.field(ElementMatchers.fieldType(InvocationHandler.class))
				.annotateField(EndpointApiAnnotationDescriptionUtils.annotAutowired(required), EndpointApiAnnotationDescriptionUtils.annotQualifier(qualifier));
		return this;
	}
	
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param uid			: The value of uid
	 * @param json			: The value of json
	 * @param <T>  : 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param bound	: The {@link MvcBound} instance
	 * @param <T>  	: 参数泛型
	 * @return {@link Builder} instance
	 */
	public EndpointApiBuilder<T> bind(final MvcBound bound) {
		builder = builder.annotateType(EndpointApiAnnotationDescriptionUtils.annotBound(bound));
		return this;
	}
 
	/**
	 * @param name		   	: 方法名称
	 * @param path   		: 发布地址
	 * @param method 		: 请求方式(GET/POST)
	 * @param consumes	 	: 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	 * @param bound			: 数据绑定对象
	 * @param params		: 参数信息
	 * @param <T>  		: 参数泛型
	 * @return {@link EndpointApiBuilder} instance
	 */
	public EndpointApiBuilder<T> newMethod(String name, String path, RequestMethod method, String consumes,
			MvcBound bound, MvcParam<?>... params) {
        // 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
		AnnotationDescription mapping =  EndpointApiAnnotationDescriptionUtils.annotMethodMapping(new MvcMethod(name, StringUtils.tokenizeToStringArray(path, ","),
        		true, null, StringUtils.tokenizeToStringArray(consumes, ","), method));
		// 定义注解方法: 方法注解 + 参数注解
		Initial<? extends EndpointApi> initial = builder.defineMethod(name, Object.class, Modifier.PUBLIC);
		Annotatable<? extends EndpointApi> annotatable = null;
		for (int i = 0; i < params.length; i++) {
			annotatable = initial.withParameter(params[i].getType(), params[i].getName())
					.annotateParameter(EndpointApiAnnotationDescriptionUtils.annotParam(params[i]));
		}
		builder = annotatable.throwing(Throwable.class).intercept(StubMethod.INSTANCE)
			.annotateMethod(mapping, EndpointApiAnnotationDescriptionUtils.annotBound(bound));
		return this;
	}
		
	/**
	 * 根据参数构造一个新的方法
	 * @param rtClass : 返回对象类型
	 * @param method : 方法注释信息
	 * @param bound  	: 方法绑定数据信息
	 * @param params 	: 参数信息
	 * @param <T> 	   : 参数泛型
	 * @return {@link EndpointApiBuilder} instance
	 */ 
	public EndpointApiBuilder<T> newMethod(final Class<?> rtClass, final MvcMethod method, final MvcBound bound, MvcParam<?>... params) {

		// 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
		AnnotationDescription mapping =  EndpointApiAnnotationDescriptionUtils.annotMethodMapping(method);
		// 定义注解方法: 方法注解 + 参数注解
		Initial<? extends EndpointApi> initial = builder.defineMethod(method.getName(), rtClass != null ? rtClass : Void.class, Modifier.PUBLIC);
		Annotatable<? extends EndpointApi> annotatable = null;
		for (int i = 0; i < params.length; i++) {
			annotatable = initial.withParameter(params[i].getType(), params[i].getName())
					.annotateParameter(EndpointApiAnnotationDescriptionUtils.annotParam(params[i]));
		}
		builder = annotatable.throwing(Throwable.class).intercept(StubMethod.INSTANCE)
			.annotateMethod(mapping, EndpointApiAnnotationDescriptionUtils.annotBound(bound));
		return this;
	}
	
	/**
	 * 为动态方法添加代理实现
	 * @param handler  	: 代理实现对象
	 * @param <T>  		: 参数泛型
	 * @return {@link EndpointApiBuilder} instance
	 */
	public EndpointApiBuilder<T> intercept(final InvocationHandler handler) {
		builder = builder.method(ElementMatchers.isAnnotatedWith(RequestMapping.class)
						.or(ElementMatchers.isAnnotatedWith(GetMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PostMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PutMapping.class))
						.or(ElementMatchers.isAnnotatedWith(DeleteMapping.class))
						.or(ElementMatchers.isAnnotatedWith(PatchMapping.class)))
				.intercept(InvocationHandlerAdapter.of(handler));
		return this;
	}

	@SuppressWarnings("unchecked")
	public Builder<T> build() {
		return (Builder<T>) builder;
	}

}