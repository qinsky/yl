package com.youlun.login.business;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.LoginInfo;
import com.youlun.baseframework.core.RequestContext;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.util.StringUtils;

@Component("login")
@Scope("prototype")
public class LoginHandler  extends AbstractBusinessHandler{
	
	public void login()
	{
		String code = params.getString("field1");
		String password = params.getString("field2");
		
		if(StringUtils.isEmpty(code)||StringUtils.isEmpty(password))
		{
			super.error("用户名或密码不能为空!");
			return;
		}
		
		code = code.trim();
		password = password.trim();
		 
		LoginInfo loginer = new LoginInfo();
		boolean flag = loginer.login(code, password);
		if(!flag)
		{
			error(RunContext.getInstance().getExeMsg().toString());
			return;
		}
		success("登录成功!");  
	}
	
	public void loginOut()
	{
		RequestContext.getSession().removeAttribute("loginInfo");
		success("注销成功!");
	}
}
