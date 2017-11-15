package com.youlun.baseframework.util;

import org.apache.log4j.Logger;

public class ExceptionUtils {
	private static Logger log = Logger.getLogger(ExceptionUtils.class);
	public static void handler(Throwable ex)
	{	
		try
		{
			log.error(ex.getMessage(),ex);
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public static void  throwException(Throwable ex)
	{
		log.error(ex.getMessage(),ex);
		throw new RuntimeException(ex);
	}
	
	public static void  throwException(String msg,Throwable ex)
	{
		throw new RuntimeException(msg,ex);
	}
	
	public static void  throwException(String msg)
	{
		throw new RuntimeException(msg);
	}
	
	public static void  throwBusinessException(String msg)
	{
		throw new RuntimeException(msg);
	}
	
	public static String getMessage(Throwable ex)
	{
		if(ex.getCause()!=null)
		{
			return getMessage(ex.getCause());
		}
		return ex.getMessage();
	}
}

