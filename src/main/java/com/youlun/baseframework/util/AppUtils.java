package com.youlun.baseframework.util;

import java.io.File;
import java.text.SimpleDateFormat;

public class AppUtils {

	public static long getResLastTime(String relativepath)
	{ 
		if(!relativepath.startsWith("/") && !relativepath.startsWith("\\"))
		{
			relativepath = File.separator+relativepath;
		}
		String ulAppPath = System.getProperty("ul.app.path");
		ulAppPath = ulAppPath == null?"":ulAppPath;
		File file = new File(ulAppPath+relativepath);
		return file.lastModified();
	}
	
	public static String getResLastTimeStr(String relativepath)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss"); 
		return sdf.format(getResLastTime(relativepath));  
	}
	
	public static void main(String[] args)
	{
		System.out.println(getResLastTimeStr("C:\\Users\\Administrator\\Desktop\\info.txt"));
	}
}
