package com.youlun.baseframework.core;

import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.MD5;
import com.youlun.baseframework.util.StringUtils;

public class LoginInfo {
	
	private JSONObject loginInfo = new JSONObject();
	
	public LoginInfo()
	{
		loginInfo.put("isLogined", false);
	}
	
	public boolean hasAttr(String attName)
	{
		return loginInfo.has(attName);
	}
	
	public boolean isLogined()
	{
		return loginInfo.getBoolean("isLogined");
	}
	
	public boolean login(String code,String password)
	{ 
		if(loginInfo.getBoolean("isLogined"))
		{
			RunContext.getInstance().setExeInfo("000001", "The user has the login!"); 
			return false;
		}  
		
		if(StringUtils.isEmpty(code)||StringUtils.isEmpty(password))
		{
			 
			return false;
		} 
		
		/**校验用户密码*/
		JSONObject qpara = new JSONObject();
		qpara.put("code", code); 
		JSONObject user = DBOperator.queryByWhereForSingle("ul_bsf_user", qpara);
		if(user==null)
		{
			RunContext.getInstance().setExeInfo("000002", "用户不存在!"); 
			return false;
		}
		
		if(user.has("locked") && "Y".equalsIgnoreCase(user.getString("locked")))
		{
			RunContext.getInstance().setExeInfo("000003", "账户已被锁定，请联系管理员解锁!");  
			return false;
		} 
		
		password = MD5.encrypt(password);
		if(!password.equals(user.get("password")))
		{
			/**如果有error_num字段，则记录密码错误次数，默认超过三次锁定账户*/
			if(user.has("error_num"))
			{
				int errNum  =user.getInt("error_num");
				errNum++;
				qpara.put("error_num", errNum);
				if(errNum>=3)
				{
					qpara.put("locked", "Y");
				} 
				qpara.remove("code");
				qpara.put("id", user.get("id"));
				DBOperator.updateByPk("ul_bsf_user", qpara);
			}
			RunContext.getInstance().setExeInfo("000004", "密码错误!");  
			return false;
		}  
		 
		loginInfo.put("isLogined", true);
		loginInfo.put("userInfo", user);
		
		RequestContext.getSession().setAttribute("loginInfo", this);
		
		return true;
	}
	
	public String getDsName()
	{
		if(loginInfo.has("dsName"))
		{
			return loginInfo.getString("dsName");
		}else
		{
			return null;
		}
	}
	
	public void setDsName(String value)
	{
		if(!loginInfo.has("dsName"))
		{
			loginInfo.put("dsName",value);
		} 
	}
	
	public String getUserId()
	{
		if(loginInfo.has("userInfo"))
		{
			return loginInfo.getJSONObject("userInfo").getString("id");
		}else
		{
			return null;
		}
	}
	
//	public void setUserId(String value)
//	{
//		if(!loginInfo.has("userInfo"))
//		{
//			loginInfo.getJSONObject("userInfo").put("id", value);
//		} 
//	} 
	
	public String getUserName()
	{
		if(loginInfo.has("userInfo"))
		{
			return loginInfo.getJSONObject("userInfo").getString("name");
		}else
		{
			return null;
		}
	}
	
	public JSONObject getUserInfo()
	{
		if(loginInfo.has("userInfo"))
		{
			return loginInfo.getJSONObject("userInfo");
		}else
		{
			return null;
		}
	}
	
//	public void setUserName(String value)
//	{
//		if(!loginInfo.has("userInfo"))
//		{
//			loginInfo.getJSONObject("userInfo").put("name", value);
//		} 
//	} 

	 
}
