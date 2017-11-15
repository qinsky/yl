package com.youlun.baseframework.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectUtils {
  
	public static Object getValue(Object obj,String path) throws Exception
	{ 
		String curKey = null;
		String strTemp = null;
		int index = path.indexOf(".");
		if(index==-1)
		{
			curKey = path; 
		}else
		{ 
			curKey = path.substring(0,index); 
			strTemp = path.substring(index+1);
		}
		Object tempObj = null; 
		if(hasMethod(obj,"get",new Object[]{new String()}))
		{
			tempObj = invokeMethod(obj,"get",new String[]{curKey});
		}else if(hasField(obj,curKey))		 
		{
			tempObj = invokeField(obj,curKey);
		}else
		{
			return null;
		}
		
		if(index==-1||tempObj==null)
		{
			return tempObj;
		}else
		{
			return getValue(tempObj,strTemp); 
		} 
	}
	 
	public static void setField(Object bean,String attr,Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{ 
			Field field = bean.getClass().getDeclaredField(attr);
			if(field!=null)
			{
				LogUtils.debug(attr+" = "+value);
				field.setAccessible(true);
				field.set(bean, value);
			} 
	}
	
	public static Object invokeField(Object obj,String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(obj); 
	}
	
	public static boolean hasField(Object obj,String fieldName)
	{
		if(obj==null||StringUtils.isEmpty(fieldName))
		{
			return false;
		}
		try {
			obj.getClass().getDeclaredField(fieldName);  
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			return false;
		}  
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object invokeMethod(Object obj,String methodName,Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class[] paramTypes = null;
		if(!ArrayUtils.isEmpty(args))
		{
			paramTypes = new Class[args.length];
			for(int i=0;i<args.length;i++)
			{
				if(args[i]!=null)
				{
					paramTypes[i] = args[i].getClass();
				}
			}
		}
		Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
		method.setAccessible(true);
		return method.invoke(obj, args); 
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean hasMethod(Object obj,String methodName,Object[] args)
	{ 
		if(obj==null||StringUtils.isEmpty(methodName))
		{
			return false;
		}
		try
		{
			Class[] paramTypes = null;
			if(!ArrayUtils.isEmpty(args))
			{
				paramTypes = new Class[args.length];
				for(int i=0;i<args.length;i++)
				{
					if(args[i]!=null)
					{
						paramTypes[i] = args[i].getClass();
					}
				}
			}
			obj.getClass().getDeclaredMethod(methodName, paramTypes);
		}catch(NoSuchMethodException ex)
		{
			return false;
		}
		return true;
	}
	
	
}
