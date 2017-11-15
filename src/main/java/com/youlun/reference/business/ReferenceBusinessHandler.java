package com.youlun.reference.business;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.reference.AbstractReference;

@Component("reference")
@Scope("prototype")
public class ReferenceBusinessHandler extends DefaultBusinessHandler{
	 
	
//	public void  handler()  
//	{ 
//		if(params.has("qq9214_at"))
//		{
//			super.handler();
//			return;
//		}
//		if(!this.params.has("busiData"))
//		{
//			log.error("参数不能为空!");
//			return;
//		} 
//		
//		String code = params.getJSONObject("busiData").getString("refType");
//		
//		JSONObject param = new JSONObject();
//		param.put("code", code);
//		JSONObject refBean = DBOperator.queryByWhereForSingle("ul_bsf_reference", param);
//		
//		AbstractReference ref = null;
//		try
//		{
//			if(refBean==null||StringUtils.isEmpty(refBean.getString("class_name")))
//			{ 
//				error("没有此引用:"+code);
//				return;
//			}
//			ref = (AbstractReference) Class.forName(refBean.getString("class_name")).newInstance();
//			
//			success(ref.invoke(params.getJSONObject("busiData")));
//		}catch(Exception ex)
//		{
//			ExceptionUtils.throwException(ex);
//		} 
//		
//	}
	
	
	
	public void qmeta()
	{
		JSONObject busi = params.getJSONObject("busiData");
		if(!busi.has("refType"))
		{
			this.error("缺失引用类型!");
			return;
		}
		JSONObject meta = getMeta(busi.getString("refType")); 
		JSONObject retObj = new JSONObject();
		retObj.put("meta", meta);
		success(retObj);
	}
	
	private JSONObject getMeta(String code)
	{
		JSONObject temp = getReferenceBean(code); 
		return new JSONObject(temp.get("meta").toString()); 
	}
	
	public void qdata()
	{
		JSONObject busi = params.getJSONObject("busiData");
		if(!busi.has("refType"))
		{
			this.error("缺失引用类型!");
			return;
		}
		 
		JSONArray data = getData(busi);
		success(data);
	}
	
	private JSONArray getData(JSONObject busi)
	{
		JSONObject temp = getReferenceBean(busi.getString("refType")); 
		String type = temp.getString("datasource_type");
		JSONArray data = null;
		if(type.equals("sql"))
		{
			if(!temp.has("datasource") || StringUtils.isEmpty(temp.getString("datasource")))
			{
				ExceptionUtils.throwBusinessException("引用("+temp.getString("code")+")的数据源信息为空!");
			}
			StringBuffer sql = new StringBuffer(temp.getString("datasource").trim());
			data = DBOperator.query(sql.toString()); 
		}
		return data;
	}
	
	public void qtext()
	{
		JSONObject busi = params.getJSONObject("busiData");
		if(!busi.has("refType"))
		{
			this.error("缺失引用类型!");
			return;
		}
		
		if(!busi.has("ids"))
		{
			this.error("缺失查询参数ids!");
			return;
		}
		JSONArray ret = getText(busi);
		success(ret);
	}
	
	/***
	 * 查询多个参照的引用文本
	 */
	public void qmttext()
	{
		JSONObject busiData = params.getJSONObject("busiData");
		if(!busiData.has("params"))
		{
			error("缺失查询参数params");
			return;
		}
		JSONArray queryParams = busiData.getJSONArray("params");
		JSONObject param = null;
		JSONArray ret = new JSONArray();
		JSONArray tArray = null; 
		for(int i=0;i<queryParams.length();i++)
		{
			param = queryParams.getJSONObject(i); 
			try
			{
				tArray = this.getText(param);
				param.remove("ids");
				param.put("data", tArray); 
			}catch(Exception ex)
			{
				ExceptionUtils.handler(ex);
				param.remove("ids");
				param.put("error", "查询引用("+param.getString("refType")+")显示名称,发生异常:"+ex.getMessage());
			}
			ret.put(param);
		} 
		success(ret);
	}
	
	private JSONArray getText(JSONObject busi)
	{ 
		JSONObject temp = getReferenceBean(busi.getString("refType"));
		if(temp==null)
		{
			ExceptionUtils.throwBusinessException("无法获取到引用定义信息,引用类型:"+busi.getString("refType"));
		}
		if(StringUtils.isEmpty(temp.getString("text_datasource").trim()))
		{
			ExceptionUtils.throwBusinessException("引用定义缺失text_datasource信息,引用类型:"+busi.getString("refType"));
		}
		
		JSONArray ids = busi.getJSONArray("ids");
		if(ids.length()==0)
		{
			return ids;
		}
		
		String type = temp.getString("datasource_type");
		JSONArray retObj = new JSONArray();
		String refField = getRefTextField(temp);
		if(refField==null)
		{
			refField = "id";
		}
		if(type.equals("sql"))
		{
			
			StringBuffer sql = new StringBuffer(temp.getString("text_datasource").trim());
		
			Object[] params = new Object[ids.length()];
			sql.append(" and "+refField+" in(");
			for(int i=0;i<ids.length();i++)
			{
				sql.append("?,");
				params[i] = ids.get(i);
			}
			sql.delete(sql.length()-1, sql.length());
			sql.append(")");
			
			//返回数据
			retObj = DBOperator.query(sql.toString(), params);
			 
		}else if(type.equals("json"))
		{
			
		}  
		
		return retObj;
	}
	
	public static String getRefTextField(JSONObject refBean)
	{
		JSONObject meta = new JSONObject(refBean.getString("meta"));
		JSONArray fields = meta.getJSONArray("fields");
		JSONObject field = null;
		for(int i=0;i<fields.length();i++)
		{
			field = fields.getJSONObject(i);
			if(field.has("refValue") && field.getBoolean("refValue"))
			{
				return field.getString("field");
			}
		}
		return null;
	}
	
	public static JSONObject getReferenceBean(String code)
	{
		JSONObject temp = new JSONObject();
		temp.put("code", code);
		temp = DBOperator.queryByWhereForSingle("ul_bsf_reference",temp);
		return temp;
	}
}
