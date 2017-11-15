package com.youlun.baseframework.core;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContext {
	
	private static WebApplicationContext context = null; 
	
	protected static void initialContext(ServletContext sc)
	{
		context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}
	
	public static WebApplicationContext getContext()
	{
		return context;
	}
	 
	public static <T> T getBean(Class<T> requiredType)
	{
		
		return context.getBean(requiredType);
	}
	
	public static boolean containsBean(String name)
	{  
		return context.containsBean(name);
	}
	
	public static boolean containsBeanDefinition(String beanName)
	{ 
		return context.containsBeanDefinition(beanName);
	}
	
	public static Object getBean(String name)
	{
		return context.getBean(name);
	}
	
	public static <T>  T getBean(String name,Class<T> requiredType)
	{ 
		return context.getBean(name,requiredType);
	}

}
