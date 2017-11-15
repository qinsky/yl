package com.youlun.baseframework.util;

public class ArrayUtils {
	
	public static boolean isEmpty(Object[] obj)
	{
		if(obj==null||obj.length==0)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(Object[] obj)
	{
		if(obj!=null&&obj.length>=0)
		{
			return true;
		}
		return false;
	}

}
