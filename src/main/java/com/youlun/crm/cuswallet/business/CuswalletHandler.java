package com.youlun.crm.cuswallet.business;

 
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.dao.DBOperator;

@Component("cuswallet")
@Scope("prototype")
public class CuswalletHandler extends DefaultBusinessHandler {

	@Override
	public boolean beforeSave(JSONObject bean)
	{
		String cusId = bean.getString("customerId");
	 
		JSONArray array = DBOperator.query("select id from ul_crm_cuswallet where df!='Y' and customer_id=?",new Object[]{cusId});
		if(array.length()>0)
		{
			error("同一客户已经存在记录");
			return false;
		}
		return true; 
	}
	
}
