package org.springframework.bytebuddy.registry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.MethodIntrospector;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class DefaultDynamicControllerRegistry extends DefaultDynamicBeanDefinitionRegistry
		implements DynamicControllerRegistry {

	protected static final Logger LOG = LoggerFactory.getLogger(DefaultDynamicControllerRegistry.class);

	// RequestMappingHandlerMapping
	protected static Method detectHandlerMethodsMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"detectHandlerMethods", Object.class);
	protected static Method getMappingForMethodMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"getMappingForMethod", Method.class, Class.class);

	protected static Field mappingRegistryField = ReflectionUtils.findField(RequestMappingHandlerMapping.class,
			"mappingRegistry");

	protected static Field injectionMetadataCacheField = ReflectionUtils
			.findField(AutowiredAnnotationBeanPostProcessor.class, "injectionMetadataCache");

	private Map<String, Long> scriptLastModifiedMap = new ConcurrentHashMap<String, Long>();// in millis

	@Autowired
	protected RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	static {
		detectHandlerMethodsMethod.setAccessible(true);
		getMappingForMethodMethod.setAccessible(true);
		// urlMapField.setAccessible(true);
		mappingRegistryField.setAccessible(true);
		injectionMetadataCacheField.setAccessible(true);
	}

	public DefaultDynamicControllerRegistry() {
		this(-1L);
	}

	public DefaultDynamicControllerRegistry(Long scriptCheckInterval) {
		if (scriptCheckInterval > 0L) {
			startScriptModifiedCheckThead(scriptCheckInterval);
		}
	}

	@Override
	public void registerController(Class<?> controllerClass) {
		this.registerController(controllerClass, BeanDefinition.SCOPE_SINGLETON);
	}

	@Override
	public void registerController(Class<?> controllerClass, String scope) {
		this.registerController(controllerClass, scope, false);
	}

	@Override
	public void registerController(Class<?> controllerClass, String scope, boolean lazyInit) {
		this.registerController(controllerClass, scope, lazyInit, true);
	}

	@Override
	public void registerController(Class<?> controllerClass, String scope, boolean lazyInit,
			boolean autowireCandidate) {
		this.registerController(controllerClass.getName(), controllerClass, scope, lazyInit, autowireCandidate);
	}

	@Override
	public void registerController(String beanName, Class<?> controllerClass) {
		this.registerController(beanName, controllerClass, BeanDefinition.SCOPE_SINGLETON);
	}

	@Override
	public void registerController(String beanName, Class<?> controllerClass, String scope) {
		this.registerController(beanName, controllerClass, scope, false);
	}

	@Override
	public void registerController(String beanName, Class<?> controllerClass, String scope, boolean lazyInit) {
		this.registerController(beanName, controllerClass, scope, lazyInit, true);
	}

	@Override
	public void registerController(String beanName, Class<?> controllerClass, String scope, boolean lazyInit,
			boolean autowireCandidate) {

		Assert.notNull(controllerClass, "register controller bean class must not null");
		if (!WebApplicationContext.class.isAssignableFrom(getApplicationContext().getClass())) {
			throw new IllegalArgumentException("applicationContext must be WebApplicationContext type");
		}

		// 构造Controller BeanDefinition
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(controllerClass);
		bd.setScope(scope);
		bd.setLazyInit(lazyInit);
		bd.setAutowireCandidate(autowireCandidate);

		this.registerController(beanName, bd);

	}

	@Override
	public void registerController(String beanName, BeanDefinition beanDefinition) {

		Assert.notNull(beanDefinition, "beanDefinition must not null");
		if (!WebApplicationContext.class.isAssignableFrom(getApplicationContext().getClass())) {
			throw new IllegalArgumentException("applicationContext must be WebApplicationContext type");
		}

		beanName = StringUtils.isEmpty(beanName) ? beanDefinition.getBeanClassName() : beanName;

		// 1、如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(beanName);
		// 2、注册新的Controller
		getBeanFactory().registerBeanDefinition(beanName, beanDefinition);
		// 3、注册新的RequestMapping
		registerRequestMappingIfNecessary(beanName);

	}

	@Override
	public void removeController(String controllerBeanName) throws IOException {
		// 如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(controllerBeanName);
	}

	@Override
	public void registerGroovyController(String scriptLocation) throws IOException {

		if (scriptNotExists(scriptLocation)) {
			throw new IllegalArgumentException("script not exists : " + scriptLocation);
		}
		scriptLastModifiedMap.put(scriptLocation, scriptLastModified(scriptLocation));

		// Create script factory bean definition.
		GroovyScriptFactory groovyScriptFactory = new GroovyScriptFactory(scriptLocation);
		groovyScriptFactory.setBeanFactory(getBeanFactory());
		groovyScriptFactory.setBeanClassLoader(getBeanFactory().getBeanClassLoader());
		Object controller = groovyScriptFactory
				.getScriptedObject(new ResourceScriptSource(getApplicationContext().getResource(scriptLocation)));

		String controllerBeanName = scriptLocation;

		// 1、如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(controllerBeanName);
		if (getBeanFactory().containsBean(controllerBeanName)) {
			getBeanFactory().destroySingleton(controllerBeanName); // 移除单例bean
			// 移除注入缓存 否则Caused by: java.lang.IllegalArgumentException: object is not an
			// instance of declaring class
			getInjectionMetadataCache().remove(controller.getClass().getName());
		}

		// 2、注册新的GroovyController
		getBeanFactory().registerSingleton(controllerBeanName, controller); // 注册单例bean
		getBeanFactory().autowireBean(controller); // 自动注入

		// 3、注册新的RequestMapping
		registerRequestMappingIfNecessary(controllerBeanName);
	}

	@Override
	public void removeGroovyController(String scriptLocation, String controllerBeanName) throws IOException {

		if (scriptNotExists(scriptLocation)) {
			throw new IllegalArgumentException("script not exists : " + scriptLocation);
		}

		// 如果RequestMapping存在则移除
		removeRequestMappingIfNecessary(scriptLocation);
		if (getBeanFactory().containsBean(scriptLocation)) {
			getBeanFactory().destroySingleton(scriptLocation); // 移除单例bean
			// 移除注入缓存 否则Caused by: java.lang.IllegalArgumentException: object is not an
			// instance of declaring class
			getInjectionMetadataCache().remove(controllerBeanName);
		}

	}

	@SuppressWarnings("unchecked")
	protected void removeRequestMappingIfNecessary(String controllerBeanName) {

		if (!getBeanFactory().containsBean(controllerBeanName)) {
			return;
		}

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();

		// remove old
		Class<?> handlerType = getApplicationContext().getType(controllerBeanName);
		final Class<?> userType = ClassUtils.getUserClass(handlerType);

		/*
		 * Map<RequestMappingInfo, HandlerMethod> handlerMethods =
		 * requestMappingHandlerMapping.getHandlerMethods(); 返回只读集合：
		 * 特别说明：因requestMappingHandlerMapping.getHandlerMethods()方法获取到的结果是只读集合，不能进行移除操作，
		 * 所以需要采用反射方式获取目标对象
		 */
		Object mappingRegistry = ReflectionUtils.getField(mappingRegistryField, requestMappingHandlerMapping);
		Method getMappingsMethod = ReflectionUtils.findMethod(mappingRegistry.getClass(), "getMappings");
		getMappingsMethod.setAccessible(true);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = (Map<RequestMappingInfo, HandlerMethod>) ReflectionUtils
				.invokeMethod(getMappingsMethod, mappingRegistry);

		/*
		 * 查找URL映射：解决 Ambiguous handler methods mapped for HTTP path “” 问题
		 */
		Field urlLookupField = ReflectionUtils.findField(mappingRegistry.getClass(), "urlLookup");
		urlLookupField.setAccessible(true);
		MultiValueMap<String, RequestMappingInfo> urlMapping = (MultiValueMap<String, RequestMappingInfo>) ReflectionUtils
				.getField(urlLookupField, mappingRegistry);

		final RequestMappingHandlerMapping innerRequestMappingHandlerMapping = requestMappingHandlerMapping;
		Set<Method> methods = MethodIntrospector.selectMethods(userType, new ReflectionUtils.MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return ReflectionUtils.invokeMethod(getMappingForMethodMethod, innerRequestMappingHandlerMapping,
						method, userType) != null;
			}
		});

		for (Method method : methods) {

			RequestMappingInfo requestMappingInfo = (RequestMappingInfo) ReflectionUtils
					.invokeMethod(getMappingForMethodMethod, requestMappingHandlerMapping, method, userType);

			handlerMethods.remove(requestMappingInfo);

			PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
			Set<String> patterns = patternsCondition.getPatterns();
			// (Set<String>) ReflectionUtils.invokeMethod(getMappingPathPatternsMethod,
			// requestMappingHandlerMapping, mapping);

			PathMatcher pathMatcher = requestMappingHandlerMapping.getPathMatcher();
			// (PathMatcher) ReflectionUtils.invokeMethod(getPathMatcherMethod,
			// requestMappingHandlerMapping);

			for (String pattern : patterns) {
				if (!pathMatcher.isPattern(pattern)) {
					urlMapping.remove(pattern);
				}
			}
		}

	}

	protected void registerRequestMappingIfNecessary(String controllerBeanName) {

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();
		// spring 3.1 开始
		ReflectionUtils.invokeMethod(detectHandlerMethodsMethod, requestMappingHandlerMapping, controllerBeanName);

	}

	private void startScriptModifiedCheckThead(final Long scriptCheckInterval) {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {

						Thread.sleep(scriptCheckInterval);

						Map<String, Long> copyMap = new HashMap<String, Long>(scriptLastModifiedMap);
						for (String scriptLocation : copyMap.keySet()) {

							if (scriptNotExists(scriptLocation)) {
								scriptLastModifiedMap.remove(scriptLocation);
								// TODO remove handler mapping ?
							}
							if (copyMap.get(scriptLocation) != scriptLastModified(scriptLocation)) {
								registerGroovyController(scriptLocation);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						// ignore
					}
				}
			}
		}.start();
	}

	@SuppressWarnings("unchecked")
	protected Map<String, InjectionMetadata> getInjectionMetadataCache() {

		AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = getApplicationContext()
				.getBean(AutowiredAnnotationBeanPostProcessor.class);

		Map<String, InjectionMetadata> injectionMetadataMap = (Map<String, InjectionMetadata>) ReflectionUtils
				.getField(injectionMetadataCacheField, autowiredAnnotationBeanPostProcessor);

		return injectionMetadataMap;
	}

	protected RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		try {
			if(requestMappingHandlerMapping != null){
				return requestMappingHandlerMapping;
			}
			return getApplicationContext().getBean(RequestMappingHandlerMapping.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("applicationContext must has RequestMappingHandlerMapping");
		}
	}

	protected long scriptLastModified(String scriptLocation) {
		try {
			return getApplicationContext().getResource(scriptLocation).getFile().lastModified();
		} catch (Exception e) {
			return -1;
		}
	}

	protected boolean scriptNotExists(String scriptLocation) {
		return !getApplicationContext().getResource(scriptLocation).exists();
	}

}