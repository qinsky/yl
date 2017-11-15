package com.youlun.crm.payreq.business;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.orgs.util.OrgUtils;

@Component("payreq")
@Scope("prototype")
public class PayreqHandler extends DefaultBusinessHandler {

	public Map<String,Object> getVariables()
	{
		Map<String,Object> variables = super.getVariables();
 
		//获取总经理
		List<String> user = OrgUtils.getPostStaff("ZJL", OrgUtils.StaffType.KEY_POST);
		String strUser = StringUtils.listToString(user);
		if(StringUtils.isNotEmpty(strUser))
		{
			variables.put("companyAdmin", strUser);
		} 
		return variables;
	}
	
}
