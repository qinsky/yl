package com.youlun.baseframework.core;

import org.json.JSONObject;

public class AppContext {

	private JSONObject context = new JSONObject();
	private static AppContext instance = null;
	private static Object locked = new Object();
	 
	
	private AppContext()
	{
		
	}
	
	public void setAttribute(String key,Object value)
	{
		context.put(key, value);
	}
	
	public Object getAttribute(String key)
	{
		if(context.has(key))
		{
			return context.get(key);
		}
		return null;
	}
	
	public static AppContext getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		synchronized(locked)
		{
			if(instance != null)
			{
				return instance;
			}
			
			instance = new AppContext();
		}
		
		return instance;
	}
	
}
