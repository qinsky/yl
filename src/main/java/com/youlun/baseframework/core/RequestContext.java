package com.youlun.baseframework.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.youlun.baseframework.util.ExceptionUtils;
  
public class RequestContext { 

	private static ThreadLocal<HttpServletRequest> local = new ThreadLocal<HttpServletRequest>();
	
	public static Object getAttribute(String name)
	{
		check();
		return local.get().getAttribute(name);
	}
	
	public static void setAttribute(String name,Object value)
	{
		check();
		local.get().setAttribute(name, value);
	}
	
	public static HttpSession getSession()
	{ 
		check();
		return  local.get().getSession();
	}
	
	public static JSONObject getHttpParams()
	{
		check();
		return (JSONObject) local.get().getAttribute("requestParameter");
	}
	
	public static HttpServletRequest getRequest()
	{
		check();
		return  local.get();
	}
	
	public static HttpServletResponse getResponse()
	{
		check();
		return (HttpServletResponse) local.get().getAttribute("response");
	}
	
	public static ServletContext getServletContext()
	{
		return  local.get().getServletContext();
	}  
	  
	protected static void initialContext(ServletRequest request, ServletResponse response)
	{
		local.remove(); 
		if(request!=null)
		{ 
			HttpServletRequest req = (HttpServletRequest)request;   
			req.setAttribute("response", response);
			req.setAttribute("ctxPath", req.getContextPath());
			local.set((HttpServletRequest) request);
			
			RunContext.getInstance();
		}
		
	}
	
	public static void destoryContext()
	{
		releaseSource(); 
		local.remove();
	}
	
	private static void check()
	{
		if(!isValid())
		{
			ExceptionUtils.throwException("RequestContext is not valid!");
		}
	}
	
	private static void releaseSource()
	{  
		RunContext.getInstance().destory();
	}
	
	public static boolean isValid()
	{
		return local.get()==null?false:true;
	}
	
}
