package com.youlun.baseframework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public static String getCurDateTime(String fm)
	{
		String strFm = "yyyy-MM-dd HH:mm:ss";
		if(StringUtils.isNotEmpty(fm))
		{
			strFm = fm;
		}
		SimpleDateFormat format = new SimpleDateFormat(strFm);
		return format.format(new Date());
	}
	
	public static String getCurDateTime()
	{
		return getCurDateTime(null);
	}
	
	public static String formatDate(Date date,String pattern)
	{
		if(date==null){
			return "";
		}
		if(StringUtils.isEmpty(pattern))
		{
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static String formatDate(Date date)
	{
		return formatDate(date,null);
	}

}
