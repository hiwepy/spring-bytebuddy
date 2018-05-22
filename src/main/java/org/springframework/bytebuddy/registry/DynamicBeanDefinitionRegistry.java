package org.springframework.bytebuddy.registry;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public interface DynamicBeanDefinitionRegistry extends BeanDefinitionRegistry {

	/**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(Class<?> beanClass);
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(Class<?> beanClass, String scope); 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(Class<?> beanClass, String scope, boolean lazyInit); 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(Class<?> beanClass, String scope, boolean lazyInit,boolean autowireCandidate); 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(String beanName, Class<?> beanClass);
	
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(String beanName, Class<?> beanClass, String scope); 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(String beanName, Class<?> beanClass, String scope, boolean lazyInit); 
    
    /**
     * 动态注册指定类型的Class到Spring上下文
     */
    public void registerBean(String beanName, Class<?> beanClass, String scope, boolean lazyInit,boolean autowireCandidate); 
    
    
}
