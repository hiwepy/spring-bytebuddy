package org.springframework.bytebuddy.registry;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DefaultDynamicBeanDefinitionRegistry implements DynamicBeanDefinitionRegistry, ApplicationContextAware {

	private DefaultListableBeanFactory beanFactory;
	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (!DefaultListableBeanFactory.class.isAssignableFrom(applicationContext.getAutowireCapableBeanFactory().getClass())) {
            throw new IllegalArgumentException("BeanFactory must be DefaultListableBeanFactory type");
        }
        this.applicationContext = applicationContext;
        this.beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }
	
	public DefaultListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(Class<?> beanClass) {
        registerBean(beanClass, BeanDefinition.SCOPE_SINGLETON);
    }
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
	@Override
	public void registerBean(Class<?> beanClass, String scope) {
		this.registerBean(beanClass, scope, false);
	}
	
	/**
     * 动态注册指定类型的Class到Spring上下文
     */
	@Override
	public void registerBean(Class<?> beanClass, String scope, boolean lazyInit) {
		this.registerBean(beanClass, scope, lazyInit, true);
	}
	
	@Override
	public void registerBean(Class<?> beanClass, String scope, boolean lazyInit, boolean autowireCandidate) {
		this.registerBean(null, beanClass, scope, lazyInit, autowireCandidate);
	} 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(String beanName, Class<?> beanClass) {
    	this.registerBean(beanName, beanClass, BeanDefinition.SCOPE_SINGLETON);
    }
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
	@Override
	public void registerBean(String beanName, Class<?> beanClass, String scope) {
		this.registerBean(beanName, beanClass, scope, false);
	}
	
	/**
     * 动态注册指定类型的Class到Spring上下文
     */
	@Override
	public void registerBean(String beanName, Class<?> beanClass, String scope, boolean lazyInit) {
		this.registerBean(beanName, beanClass, scope, lazyInit, true);
	}

	/**
     * 动态注册指定类型的Class到Spring上下文
     */
	@Override
	public void registerBean(String beanName, Class<?> beanClass, String scope, boolean lazyInit,
			boolean autowireCandidate) {
		Assert.notNull(beanClass, "register bean class must not null");
       
		GenericBeanDefinition bd = new GenericBeanDefinition();
        
		bd.setBeanClass(beanClass);
        bd.setScope(scope);
        bd.setLazyInit(lazyInit);
        bd.setAutowireCandidate(autowireCandidate);
        
        if (StringUtils.hasText(beanName)) {
        	registerBeanDefinition(beanName, bd);
        } else {
            BeanDefinitionReaderUtils.registerWithGeneratedName(bd, beanFactory);
        }
	}

	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {
		getBeanFactory().registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		getBeanFactory().removeBeanDefinition(beanName);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		return getBeanFactory().getBeanDefinition(beanName);
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return getBeanFactory().isBeanNameInUse(beanName);
	}

	@Override
	public void registerAlias(String name, String alias) {
		getBeanFactory().registerAlias(name, alias);
	}

	@Override
	public void removeAlias(String alias) {
		getBeanFactory().removeAlias(alias);
	}

	@Override
	public boolean isAlias(String name) {
		return getBeanFactory().isAlias(name);
	}

	@Override
	public String[] getAliases(String name) {
		return getBeanFactory().getAliases(name);
	} 
    
}
