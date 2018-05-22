package org.springframework.bytebuddy.registry;

import java.io.IOException;

import org.springframework.beans.factory.config.BeanDefinition;

public interface DynamicControllerRegistry extends DynamicBeanDefinitionRegistry {

	/**
     * 动态注册SpringMVC Controller到Spring上下文
     */
	public void registerController(Class<?> controllerClass);
	
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(Class<?> controllerClass, String scope); 
    
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(Class<?> controllerClass, String scope, boolean lazyInit); 
    
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(Class<?> controllerClass, String scope, boolean lazyInit,boolean autowireCandidate); 
	
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
	public void registerController(String beanName,Class<?> controllerClass);
	
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(String beanName,Class<?> controllerClass, String scope); 
    
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(String beanName,Class<?> controllerClass, String scope, boolean lazyInit); 
    
    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(String beanName,Class<?> controllerClass, String scope, boolean lazyInit,boolean autowireCandidate); 

    /**
     * 动态注册SpringMVC Controller到Spring上下文
     */
    public void registerController(String beanName, BeanDefinition beanDefinition);
    
    /**
     * 动态从Spring上下文删除SpringMVC Controller
     */
    public void removeController(String controllerBeanName) throws IOException;
    
	/**
     * 动态注册Groovy Controller到Spring上下文
     */
	public void registerGroovyController(String scriptLocation) throws IOException;
	
	/**
     * 动态从Spring上下文删除Groovy Controller
     */
	public void removeGroovyController(String scriptLocation,String controllerBeanName) throws IOException;
	
}
