package com.youlun.user.util;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.StringUtils;

public class UserUtils {

	private static Logger log = Logger.getLogger(UserUtils.class);
	
	public static String getUserName(String userId)
	{
		if(StringUtils.isEmpty(userId))
		{
			return "";
		}
		JSONObject obj = DBOperator.queryColumnsByPrimaryKey("ul_bsf_user", userId, new String[]{"name"});
		if(obj==null)
		{
			log.warn("用户不存在,Id:"+userId);
			return "";
		}
		return obj.getString("name");
	}
}
