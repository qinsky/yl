package com.youlun.baseframework.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	
	public static byte[] getBytes(String str)
	{
		 return getBytes(str,null);
	}
	
	public static byte[] getBytes(String str,String enc)
	{
		try
		{
			if(str==null)
			{
				return null;
			}
			if(enc==null)
			{
				return str.getBytes();
			}else
			{
				return str.getBytes(enc);
			}
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}
	 
	public static String listToString(List<String> lsValues)
	{
		return listToString(lsValues,",");
	}
	public static String listToString(List<String> lsValues,String split)
	{ 
		if(lsValues==null||lsValues.isEmpty())
		{
			return "";
		}
		return arrToStr((String[]) lsValues.toArray(new String[0]),split);
	}
 
	/**
	 * 
	 * @param str 待分割的字符串
	 * @param regex 分割标识
	 * @param keyFlag 关键标志:不识别关键标志里面的分割标识
	 * @return
	 * eg: str="ab=c=d'='cde",regex="=",keyFlag="'",结果是:"ab"、"c"、"d'='cde"
	 */
	public static List<String> splitStr(String str,String regex,String keyFlag)
	{
		List<String> lsRs = new ArrayList<String>();
		if(StringUtils.isEmpty(str))
		{
			return lsRs;
		} 
		
		int indexRegex = 0;
		int indexKey = 0;
		int beginIndex = 0;
		while(true)
		{
			indexRegex = str.indexOf(regex,indexRegex==0?beginIndex:indexRegex+1);
			if(indexRegex==-1)
			{
				lsRs.add(str.substring(beginIndex));
				break;
			}
			indexKey = str.indexOf(keyFlag,beginIndex);
			int countKey = 0;
			while(indexRegex>indexKey&&indexKey!=-1)
			{
				countKey++; 
				indexKey = str.indexOf(keyFlag, indexKey+1);
			}
			if(countKey%2==0||indexKey==-1)
			{  
				lsRs.add(str.substring(beginIndex,indexRegex)); 
				beginIndex = indexRegex+1; 
				indexRegex = 0;
			}  
			if(beginIndex>=str.length())
			{
				break;
			}
		} 
		return lsRs;
	}
	
	public static String arrToStr(String[] args)
	{
		return arrToStr(args,",");
	}
	public static String arrToStr(String[] args,String split)
	{ 
		if(ArrayUtils.isEmpty(args))
		{
			return "";
		}
		if(split==null)
		{
			split = ",";
		}
		StringBuffer buf = new StringBuffer();
		for(String str : args)
		{
			buf.append(str).append(split);
		}
		if(!split.equals(""))
		{
			int dex = buf.lastIndexOf(split);
			buf.delete(dex, buf.length());
		}   
		return buf.toString();
	}
	
	public static boolean isEmpty(String str)
	{
		if(str==null||str.trim().equals(""))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(String str)
	{
		return !isEmpty(str);
	}
	 
	public static String trim(String str)
	{
		if(str==null)
		{
			return null;
		}else
		{
			return str.trim();
		}
	}
}
