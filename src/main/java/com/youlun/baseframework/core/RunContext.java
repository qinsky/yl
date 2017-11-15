package com.youlun.baseframework.core;

import org.json.JSONObject;


public class RunContext {
	
	private static ThreadLocal<RunContext> local = new ThreadLocal<RunContext>();
	
	private LoginInfo loginInfo;
	private JSONObject tEvn = new JSONObject();
	 
	private RunContext()
	{
		
	}
	
	public boolean isLogined()
	{  
		 return loginInfo.isLogined(); 
	}
	
	public void setAttribute(String name,Object value)
	{
		tEvn.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return tEvn.get(name);
	}
	
	public Object getExeCode()
	{
		return tEvn.get("ul_exe_code");
	}
	
	public Object getExeMsg()
	{
		if(tEvn.has("ul_exe_msg"))
		{
			return tEvn.get("ul_exe_msg");
		}
		return "";
	}
	
	public void setExeInfo(String code,String msg)
	{
		tEvn.put("ul_exe_code",code);
		tEvn.put("ul_exe_msg",msg);
	}
	 
	
	protected void destory()
	{
		local.remove();
	}
	
	public void initialLoginInfo()
	{
		if(RequestContext.isValid())
		{
			this.loginInfo = (LoginInfo) RequestContext.getSession().getAttribute("loginInfo");
			if(this.loginInfo==null)
			{
				this.loginInfo = new LoginInfo();
				RequestContext.getSession().setAttribute("loginInfo",this.loginInfo);
			}
		}else
		{
			this.loginInfo = new LoginInfo();
		} 
	}
	
	public static RunContext getInstance()
	{
		if(local.get()==null)
		{
			RunContext context = new RunContext();
			context.initialLoginInfo();
			local.set(context);
		}
		return local.get();
	} 
	
	public String getDsName()
	{
		if(loginInfo.hasAttr("dsName"))
		{
			return loginInfo.getDsName();
		}else
		{
			return "default";
		}
	}
	
	public void setDsName(String value)
	{
		if(!loginInfo.hasAttr("dsName"))
		{
			loginInfo.setDsName(value);
		} 
	}
	
	public String getUserId()
	{
		return loginInfo.getUserId();
	}
	public JSONObject getUserInfo()
	{ 
		 return loginInfo.getUserInfo();
	}  
	
	public String getUserName()
	{
		return loginInfo.getUserName();
	}
 
}
