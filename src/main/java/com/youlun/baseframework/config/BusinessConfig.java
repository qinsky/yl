package com.youlun.baseframework.config;

import org.json.JSONObject;

import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;

public class BusinessConfig {
	
	private static JSONObject busitype = new JSONObject();
	
	public static JSONObject getBusitype(String qq9214Bt)
	{
		String cacheKey = RunContext.getInstance().getDsName()+"."+qq9214Bt;
		if(busitype.has(cacheKey))
		{
			return busitype.getJSONObject(cacheKey);
		}
		
		synchronized(busitype)
		{
			if(busitype.has(cacheKey))
			{
				return busitype.getJSONObject(cacheKey);
			}
			
			JSONObject tObj = new JSONObject(); 
			tObj.put("qq9214_bt", qq9214Bt); 
			tObj = DBOperator.queryByWhereForSingle("ul_bsf_busitype",tObj);
			if(tObj!=null)
			{
				busitype.put(cacheKey, tObj);
				return tObj;
			}
		} 
		return null;
	}
	
	/**
	 * @desc:按模块划分业务类型 
	 * @param qq9214Bt  业务类型
	 * @param qq9214At  业务动作类型
	 * @return
	 */
	public static JSONObject getActionType(String qq9214Bt,String qq9214At)
	{
		String cacheKey = RunContext.getInstance().getDsName()+"."+qq9214Bt+"."+qq9214At;
		if(busitype.has(cacheKey))
		{
			return busitype.getJSONObject(cacheKey);
		}
		
		synchronized(busitype)
		{
			if(busitype.has(cacheKey))
			{
				return busitype.getJSONObject(cacheKey);
			}
			 
			JSONObject tObj = getBusitype(qq9214Bt);
			
			JSONObject param = new JSONObject();
			param.put("busitype_id", tObj.get("id"));
			param.put("qq9214_at", qq9214At); 
			
			tObj = DBOperator.queryByWhereForSingle("ul_bsf_busitype_action",param);
			if(tObj!=null)
			{
				busitype.put(cacheKey, tObj);
				return tObj;
			} 
		} 
		return null;
	}
}
