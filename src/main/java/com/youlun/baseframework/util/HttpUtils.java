package com.youlun.baseframework.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class HttpUtils {
	
	private static Logger log = Logger.getLogger(HttpUtils.class);
	
	public static void writeError(String msg,ServletResponse response)
	{ 
		JSONObject error  = new JSONObject();
		error.put("code", "error");
		error.put("msg", msg);
		write(error,response);
	}
	
	public static void write(Object obj,ServletResponse response)
	{
		if(obj==null)
		{
			return;
		}
		byte[] data = null;
		if(obj.getClass().getName().equals("[B"))
		{
			data = (byte[])obj;
		}else
		{
			data = StringUtils.getBytes(obj.toString(),"UTF-8");
		} 
		
		try {
			response.getOutputStream().write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExceptionUtils.throwException(e);
		}
	}
	
	public static boolean isHasParamter(String url)
	{
		boolean flag = false;
		Pattern pattern = Pattern.compile(".*\\?(.*)*$");
		Matcher match = pattern.matcher(url);
		if(match.matches())
		{
			flag = true;
		}
		
		return flag;
	}

	public static String jsonToQueryString(JSONObject param)
	{
		StringBuffer buf = new StringBuffer();
		Iterator<String> it = param.keys();
		String key = null;
		while(it.hasNext())
		{
			key = it.next();
			buf.append(key).append("=").append(param.get(key)).append("&");
		}
		if(buf.length()>0)
		{
			buf = buf.delete(buf.length()-1, buf.length());
		}
		return buf.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String mapToQueryString(Map map)
	{
		StringBuffer buf = new StringBuffer();
		Iterator<Object> it = map.keySet().iterator();
		Object key = null;
		while(it.hasNext())
		{
			key = it.next();
			buf.append(key).append("=").append(map.get(key)).append("&");
		}
		if(buf.length()>0)
		{
			buf = buf.delete(buf.length()-1, buf.length());
		}
		return buf.toString();
	}
	
	public static byte[] sendPostQuest(String server)
	{
		return sendPostRequest(server,new byte[0]);
	}
	
	public static byte[] sendPostRequest(String server,JSONObject params)
	{ 
		return sendPostRequest(server,params,null); 
	}
	
	public static byte[] sendPostRequest(String server,JSONObject params,String enc)
	{
		byte[] data = null;
		if(params!=null&&params.length()>0)
		{ 
			data = StringUtils.getBytes(params.toString(),enc);;
		}else
		{
			data = new byte[0];
		}
		return sendPostRequest(server,data); 
	}
	
	public static byte[] sendNormalPostRequest(String server,JSONObject params)
	{ 
		return sendNormalPostRequest(server,params,null); 
	}
	
	public static byte[] sendNormalPostRequest(String server,JSONObject params,String enc)
	{
		byte[] data = null;
		if(params!=null&&params.length()>0)
		{ 
			data = StringUtils.getBytes(jsonToQueryString(params),enc);;
		}else
		{
			data = new byte[0];
		}
		return sendPostRequest(server,data); 
	}
	
	public static byte[] sendPostRequest(String server,byte[] data)
	{

		if(StringUtils.isEmpty(server))
		{
			return null;
		}
		InputStream in = null;
		try
		{
			log.debug("url--->"+server);
			URL url = new URL(server);
			 
			HttpURLConnection c = (HttpURLConnection)url.openConnection(); 
			c.setRequestMethod("POST");  
			c.setDoInput(true);
			c.setDoOutput(true); 
			if(data!=null&&data.length>0)
			{
				c.getOutputStream().write(data);	
			} 
			in = c.getInputStream();
			
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return FileUtils.readData(in); 
 
	}
	
	public static byte[] sendGetRequest(String server)
	{ 
		return sendGetRequest(server,null);
	}
	public static byte[] sendGetRequest(String server,JSONObject params)
	{ 
	 
			if(StringUtils.isEmpty(server))
			{
				return null;
			}
			if(params!=null&&params.length()>0)
			{ 
				if(!isHasParamter(server))
				{
					server += "?"+jsonToQueryString(params);
				}else
				{
					if(server.endsWith("?"))
					{
						server += jsonToQueryString(params);
					}else
					{
						server += "&"+jsonToQueryString(params);
					} 
				} 
			}
			InputStream in = null;
			try
			{
				log.debug("url--->"+server);
				URL url = new URL(server);
				 
				HttpURLConnection c = (HttpURLConnection)url.openConnection(); 
				c.setRequestMethod("GET");
				c.setDoInput(true);
				c.setDoOutput(true);  
				in = c.getInputStream();
			}catch(Exception ex)
			{
				ExceptionUtils.throwException(ex);
			}
			
			return FileUtils.readData(in); 
		 
	}

	public static JSONObject getParams(HttpServletRequest request)
	{
		JSONObject params = null;  
		String contentType = request.getHeader("content-type"); 
		
		if("POST".equalsIgnoreCase(request.getMethod()))
		{    
			if(StringUtils.isNotEmpty(contentType)&&contentType.indexOf("application/json")>=0)
			{ 
				params = getFormDataJsonParams(request);
			}else if(StringUtils.isNotEmpty(contentType)&&contentType.indexOf("multipart/form-data")>=0)
			{ 
				params = getMultipartFormDataParams(request); 
			}
		}
		
		if(params == null)
		{
			params = getFormUrlencodedParams(request);
		} 
		
		request.setAttribute("requestParameter", params);
		return params; 
	}
	
	private static String getEncoding(HttpServletRequest request)
	{
		String enc = null;
		if(request!=null)
		{
			enc = getHeaderValue(request, "content-type", "charset"); 
		} 
		if(StringUtils.isEmpty(enc))
		{
			enc = (String)System.getProperties().get("youlun.urlencode");
			if(StringUtils.isEmpty(enc))
			{
				enc = "UTF-8";
			}
		} 
		return enc;
	}
	
	private static JSONObject getFormDataJsonParams(HttpServletRequest request)
	{
		byte[] data = null;
		JSONObject obj = null;
		try
		{
			data = FileUtils.readData(request.getInputStream());
			String strData = new String(data,getEncoding(request));
			if(StringUtils.isEmpty(strData))
			{
				obj = new JSONObject();
			}else
			{
				strData = strData.replaceAll("\n", "\\\\n");
				obj = new JSONObject(strData);
			}
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}  
		return obj;
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		InputStream in = new FileInputStream("C:\\Users\\qin\\Desktop\\data.json");
//		String boundary = "boundary=------WebKitFormBoundarydEQ8k1KEaoFEjyB2";
//		JSONObject obj = getMultipartFormDataParams(in,boundary);
		
//		System.out.println(obj);
//		String strTest = "multipart/form-data; boundary=----WebK;itFormBoundaryvEa0wvFcPVVYdNf2";
	   
//	}
	
	/**
	 * 
	 * @param request
	 * @param typeName
	 * @return
	 * desc:获取第一个参数
	 * eg:"multipart/form-data; boundary=----WebK;itFormBoundaryvEa0wvFcPVVYdNf2";  将返回:multipart/form-data
	 */ 
	@SuppressWarnings("unused")
	private static String getHeaderValue(HttpServletRequest request,String typeName)
	{
		return getHeaderValue(request,typeName,null);
	}
	
	private static String getHeaderValue(HttpServletRequest request,String typeName,String name)
	{
	 
		String strValue = request.getHeader(typeName);
		if(StringUtils.isEmpty(strValue))
		{
			return null;
		}
		
		String[] temp = strValue.split(";");
		if(StringUtils.isEmpty(name))
		{
			return temp[0].trim();
		}
		
		String[] tv = null;
		for(String str:temp)
		{
			tv = str.split("=");
			if(tv.length>1&&name.equals(tv[0].trim()))
			{
				return tv[1].trim();
			}
		}
		
		return null;
	}
	
	private static JSONObject getMultipartFormDataParams(HttpServletRequest request)  
	{
		ByteArrayOutputStream out = null;
		InputStream in = null;
		String strTemp = null;
		JSONObject obj = null;
		byte[] flags = new byte[]{13,10}; //回车换行 
		String boundary = getHeaderValue(request,"content-type","boundary");
		try
		{ 
			obj =  getFormUrlencodedParams(request,false);
			in = request.getInputStream();
			boundary = "--"+boundary;
			out = new ByteArrayOutputStream(); 
			out.write(flags);
			out.write(boundary.getBytes());
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		
		byte[] newEntryFlags = out.toByteArray();
		Map<String,String> entryMap = new HashMap<String,String>();
	 
		String enc   = (String)System.getProperties().get("youlun.urlencode");
		if(enc==null)
		{
			enc = "UTF-8";
		} 
		boolean isEnd=false;
		while(true)
		{ 
			entryMap.clear();
			
			//读取字段头信息
			isEnd = readHeadInfo(in,entryMap,boundary,flags,out);
			if(isEnd)
			{
				return obj;
			}
			
			//解析字段数据
			strTemp = entryMap.get("Content-Type");
			Map<String,String> map = parseEntry(strTemp);
			//流数据
			if("application/octet-stream".equals(map.get("Content-Type")))
			{
				getBinaryData(in,entryMap,obj,newEntryFlags,out,enc);
				
			}else //非流数据
			{
				strTemp = entryMap.get("Content-Disposition");
				map = parseEntry(strTemp);
				
				byte[] data = ByteUtils.readToByte(in, newEntryFlags,out);  
				strTemp =urlDecoder(map.get("name"),enc);
				
				try
				{
					obj.accumulate(strTemp, new String(data,enc)); 
				}catch(Exception ex)
				{
					ExceptionUtils.throwException(ex);
				}
				
			}   
		}  
		 
	} 
	
	private static boolean readHeadInfo(InputStream in,Map<String,String> entryMap,String boundary,byte[] flags,ByteArrayOutputStream out)
	{
		
		boolean isEnd = false;
		//读取字段头信息
		boolean entryFlag = false;
		byte[] data;
		String strTemp;
		while(true)
		{
			//如果流已经读取完 则返回
			data = ByteUtils.readToByte(in, flags,out);
			if(data.length==2&&data[0]==45&&data[1]==45)
			{
				isEnd = true;
				break;
			} 
			
			if(data.length>0)
			{
				//如果是刚开始解析会碰到分割符号，则直接跳过
				strTemp = new String(data);
				if(strTemp.equals(boundary))
				{
					continue;
				} 
				
				entryMap.put(strTemp.substring(0,strTemp.indexOf(":")),strTemp);
				entryFlag = true;
			}else
			{ 
				//加entryFlag 判断 是为了过滤 分割线后的换行
				if(entryFlag)
				{
					break; 
				}
			}
		}    
		return isEnd;
	}
	
	private static void getBinaryData(InputStream in,Map<String,String> entryMap,JSONObject obj,byte[] newEntryFlags,ByteArrayOutputStream out,String enc)
	{
		byte[] data = new byte[newEntryFlags.length];
		int td ;
		out.reset();
		int index = 0;
		boolean flag = false;
		File file = null;
		OutputStream fileOut = null;
		String strTemp;
		Map<String,String> map = null;
		try
		{
			//每次读取一个字节，效率很低，应该要判断是否读取完了
			while(true)
			{
				td = in.read();
				//表示填满了分割标识字节数组
				if(index==data.length)
				{
					 index = index%data.length;
					 flag = true;
				}
				//类似一个循环队列，如果已经填满，需要在覆盖前 保存数据
				if(flag)
				{
					if(fileOut!=null)
					{
						fileOut.write(data[index]);
					}else
					{
						out.write(data[index]);
						if(out.size()>1024*1024)
						{
							file = File.createTempFile("ulHttpTemp", ".data");
							fileOut = new FileOutputStream(file);
							fileOut.write(out.toByteArray());
						}
					}
				}
				data[index] = (byte) td;
				
				if(ByteUtils.equals(newEntryFlags, 0, data, index+1))
				{
					break;
				}
				index++;
			}
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		FileUtils.close(fileOut);
		
		strTemp = entryMap.get("Content-Disposition");
		map = parseEntry(strTemp);
		
		JSONObject jsEntry = new JSONObject();
		//获取文件名字，需要做转码处理
		if(map.containsKey("filename"))
		{
			strTemp = urlDecoder(map.get("filename"),enc);
			jsEntry.put("filename",strTemp);
		} 
		//设置参数流数据
		if(file!=null)
		{
			jsEntry.put("file", file);
		}else
		{
			jsEntry.put("data", out.toByteArray());
		}
		strTemp =urlDecoder(map.get("name"),enc);
		//保存变量
		obj.accumulate(strTemp, jsEntry);
	}
	
	private static String urlDecoder(String url,String enc)
	{
		try
		{
			return URLDecoder.decode(url,enc);
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}
 
	private static Map<String,String> parseEntry(String entry)
	{
		Map<String,String> map = new HashMap<String,String>();
		if(entry==null||"".equals(entry))
		{
			return map;
		}
		List<String> arrs = StringUtils.splitStr(entry,";","\"");
		String[] tArrs = arrs.get(0).split(": ");
		map.put(tArrs[0], tArrs[1]);
		List<String> lsTemp = null;
		if(arrs.size()>1)
		{
			String strTemp = null;
			for(int i=1;i<arrs.size();i++)
			{
				lsTemp = StringUtils.splitStr(arrs.get(i),"=","\"");
				strTemp = lsTemp.get(1);
				if(strTemp.startsWith("\"")&&strTemp.endsWith("\""))
				{
					strTemp = strTemp.substring(1,strTemp.length()-1);
				}
				map.put(lsTemp.get(0).trim(), strTemp);
			}
		}
		return map;
	}
	private static JSONObject getFormUrlencodedParams(HttpServletRequest request)
	{
		return getFormUrlencodedParams(request,true);
	}
	
	private static JSONObject getFormUrlencodedParams(HttpServletRequest request,boolean includePost)
	{
		
		String enc = getEncoding(request);
		
		JSONObject obj = new JSONObject();
		String queryStr = request.getQueryString(); 
		if(queryStr==null)
		{
			queryStr = "";
		}
		String conStr = "&";
		String str = "";
		if("POST".equalsIgnoreCase(request.getMethod())&&includePost)
		{
			try
			{
				byte[] data = FileUtils.readData(request.getInputStream());
				str = new String(data,enc); 
			}catch(Exception ex)
			{
				ExceptionUtils.throwException(ex);
			}
		}
		if(StringUtils.isEmpty(queryStr)||StringUtils.isEmpty(str))
		{
			conStr = "";
		}
		queryStr += conStr+str;
		 
		String[] params = queryStr.split("&");
		String[] tArr = null;
		String tValue = null;
		for(String tStr : params)
		{ 
			if(StringUtils.isEmpty(tStr))
			{
				continue;
			}
			
			tValue = "";
			tArr = tStr.split("=");
			if(tArr.length>1)
			{
				tValue = tArr[1];
			}
			 
			obj.accumulate(urlDecoder(tArr[0], enc).trim(), urlDecoder(tValue, enc).trim());
			 
		}
		return obj;
	}
	
}