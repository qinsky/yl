package com.youlun.baseframework.util;

import org.apache.log4j.Logger;

public class LogUtils {
	
	private static Logger log = Logger.getRootLogger();
	
	
	public static void info(Object msg)
	{
		log.info(msg);
	}
	
	public static void info(Object msg,Throwable ex)
	{
		log.info(msg,ex);
	}
	
	public static void error(Object msg)
	{
		log.error(msg);
	}
	
	public static void error(Object msg,Throwable ex)
	{
		log.error(msg,ex);
	}
	
	public static void warn(Object msg)
	{
		log.warn(msg);
	}
	
	public static void warn(Object msg,Throwable ex)
	{
		log.warn(msg,ex);
	}
	
	public static void debug(Object msg)
	{
		log.debug(msg);
	}
	
	public static void debug(Object msg,Throwable ex)
	{ 
		log.debug(msg,ex);
	}
	
	public static void trace(Object msg)
	{
		log.trace(msg);
	}
	
	public static void trace(Object msg,Throwable ex)
	{ 
		log.trace(msg,ex);
	}
	
	public static void fatal(Object msg)
	{
		log.fatal(msg);
	}
	
	public static void fatal(Object msg,Throwable ex)
	{ 
		log.fatal(msg,ex);
	}

}
