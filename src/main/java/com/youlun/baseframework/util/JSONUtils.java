package com.youlun.baseframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
 
public class JSONUtils {
	
	public static void copySrcToDes(JSONObject desc,JSONObject src)
	{
		if(src==null||desc==null)
		{
			return;
		}
		Iterator<String> keys = src.keys();
		String key = null;
		while(keys.hasNext())
		{
			key = keys.next();
			desc.put(key, src.get(key));
		}
	}
	
	public static boolean equals(JSONObject obj,String key,Object value)
	{
		 if(obj.has(key) && obj.get(key).equals(value))
		 {
			 return true;
		 }
		 return false;
	}
	 
	
	public static void writeJSONToStream(JSONObject obj,OutputStream out)
	{
		byte[] data = StringUtils.getBytes(obj.toString());
		try {
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExceptionUtils.throwException(e);
		}
	}
	
	public static JSONObject readFileToJSON(String path,String charSet)
	{
		byte[] data = FileUtils.readFile(path); 
		String str = FileUtils.bytesToStr(data,charSet);
		return new JSONObject(str);
	}
	 
	public static JSONObject readFileToJSON(String path)
	{ 
		return readFileToJSON(path,null); 
	}
	
	public static JSONObject readFileToJSON(InputStream in,String character)
	{
		byte[] data = FileUtils.readData(in);
		String str = FileUtils.bytesToStr(data,character);
		return new JSONObject(str); 
	}
	
	public static JSONObject readFileToJSON(InputStream in)
	{ 
		return readFileToJSON(in,null);
	}
	
	
	public static JSONObject clearJson(JSONObject json)
	{
		Iterator<String> it = json.keys();
		List<String> lsKey = new ArrayList<String>();
		while(it.hasNext())
		{
			lsKey.add(it.next());
		} 
		for(String key : lsKey)
		{
			json.remove(key);
		}
		return json;
	}
	
	public static JSONObject convertArrayToJson(String key,JSONArray jsonarray)
	{
		return convertArrayToJson(key,jsonarray,0);
	}
	
	public static JSONObject convertArrayToJsonWithLowerCase(String key,JSONArray jsonarray)
	{
		return convertArrayToJson(key,jsonarray,-1);
	}
	public static JSONObject convertArrayToJsonWithUpperCase(String key,JSONArray jsonarray)
	{
		return convertArrayToJson(key,jsonarray,1);
	}
	
	/**
	 * 将JSON数组的对象转换成JSON对象，JSON对象的key来自于数组每个对象key(参数key指定)的值 
	 * @param key
	 * @param jsonarray
	 * @param caseSize  <0:小写、=0:不变、>0:大写
	 * @return
	 */
	public static JSONObject convertArrayToJson(String key,JSONArray jsonarray,int caseSize)
	{
		JSONObject obj = new JSONObject();
		JSONObject tJson = null;
		 
		String kv = null;
		for(int i=0;i<jsonarray.length();i++)
		{
			tJson = jsonarray.getJSONObject(i);
			if(!tJson.has(key))
			{
				ExceptionUtils.throwException("The Object JSONObject of JSONArray has no attribute of "+key+"!");
			} 
			kv = tJson.getString(key);
			if(caseSize<0)
			{
				kv = kv.toLowerCase();
			}else if(caseSize>0)
			{
				kv = kv.toUpperCase();
			}
			obj.put(kv, tJson);
		}
		return obj;
	}
	
	public static boolean isEmpty(JSONArray array)
	{
		if(array==null||array.length()==0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 根据给定的path 获取json里面的对象
	 * @param json
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Object getValue(JSONObject json,String path)
	{   
		int index = path.indexOf(".");
		if(index==-1)
		{
			if(json.has(path)){
				return  json.get(path);
			}else
			{
				return null;
			}
		}else
		{ 
			String curKey = path.substring(0,index); 
			String strTemp = path.substring(index+1);
			
			if(!json.has(curKey))
			{
				return null;
			} 
			return getValue(json.getJSONObject(curKey),strTemp); 
		} 
	}
	
	 
	
	public static void setValue(JSONObject json,String path,Object value)
	{
		int index = path.indexOf(".");
  
		if(index==-1)
		{ 
			 json.put(path, value);
		}else
		{
			String curKey = path.substring(0,index);
			String strTemp  = path.substring(index+1);
			JSONObject jsonTemp = json.getJSONObject(curKey); 
		    setValue(jsonTemp,strTemp,value);
		}  
	}

	public static void generalJsonPath(JSONObject json,String path)
	{
		int index = path.indexOf(".");
  
		if(index==-1)
		{ 
			if(!json.has(path))
			{
				json.put(path, new JSONObject());
			}
		}else
		{
			String curKey = path.substring(0,index);
			String strTemp  = path.substring(index+1);
			JSONObject jsonTemp = null;
			if(json.has(curKey))
			{
				jsonTemp = json.getJSONObject(curKey);
			}else
			{
				jsonTemp = new JSONObject();
				json.put(curKey, jsonTemp);
			}
			generalJsonPath(jsonTemp,strTemp);
		}  
	}
	
	public static List<Object> jsonArrayToList(JSONArray array)
	{
		List<Object> list = new ArrayList<Object>();
		for(int i=0;i<array.length();i++)
		{
			list.add(array.get(i));
		}
		return list;
	}
}
