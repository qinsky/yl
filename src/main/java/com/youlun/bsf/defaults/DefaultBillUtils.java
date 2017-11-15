package com.youlun.bsf.defaults;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.meta.MetaBusinessUtils;

public class DefaultBillUtils {
	
	public static JSONObject getBillByMetaId(String metaId,Object id,boolean isUiData)
	{ 
		JSONObject meta = MetaBusinessUtils.getDBMeta(metaId);
		return getBillByMeta(meta,id,isUiData);
	}

	public static JSONObject getBillByMeta(JSONObject meta,Object id,boolean isUiData)
	{ 
		if(meta==null)
		{
			return null;
		}
		JSONObject bean = DBOperator.queryByPrimaryKey(meta.getString("tableName"), id);
		//转换成UI单据数据
		if(isUiData)
		{
			dbToUi(meta,bean);
		}
		return bean;
	}
	
	public static JSONObject getBillByMenuId(String menuId,Object id,boolean isUiData)
	{
		JSONObject meta = MetaBusinessUtils.getDbMetaByMenuId(menuId);
		if(meta!=null)
		{
			return getBillByMeta(meta,id,isUiData);
		}
		return null;
		
	}
	
	public static JSONObject getUiBillByMenuId(String menuId,Object id,boolean isUiData)
	{
		JSONObject meta = MetaBusinessUtils.getDbMetaByMenuId(menuId);
		if(meta!=null)
		{
			return getBillByMeta(meta,id,isUiData);
		}
		return null;
		
	}
	
	public static void dbToUi(JSONObject meta,JSONObject bean)
	{
		JSONArray beans = new JSONArray();
		beans.put(bean);
	    dbToUi(meta,beans);
	}
	public static void dbToUi(JSONObject meta,JSONArray beans)
	{
		JSONArray columns = meta.getJSONArray("columns");
		JSONObject bean;
		JSONObject column;
		
		for(int i=0;i<beans.length();i++)
		{
			bean = beans.getJSONObject(i);  
			for(int j=0;j<columns.length();j++)
			{
				column = columns.getJSONObject(j);
				if(column.get("uiField").equals(column.get("column")))
				{
					continue;
				}
				if(bean.has(column.getString("column")))
				{ 
					bean.put(column.getString("uiField"), bean.remove(column.getString("column")));
				} 
			}
		} 
	} 
	
	public static JSONArray uiToDb(JSONObject meta,JSONArray beans)
	{
		for(int i=0;i<beans.length();i++)
		{
			uiToDb(meta,beans.getJSONObject(i));
		}
		return beans;
	}
	public static JSONObject uiToDb(JSONObject meta,JSONObject bean)
	{
		//获取定义的表头字段
		JSONArray fields = meta.getJSONArray("columns");
		JSONObject field = null;
		JSONObject saveData = new JSONObject();
		String tCode = null;
		String tDbCode = null;
		for(int i=0;i<fields.length();i++)
		{
			field = fields.getJSONObject(i);
			//界面显示的字段编码
			tCode = field.getString("uiField");
			//映射的数据库字段编码
			tDbCode = field.getString("column");
			if(bean.has(tCode))
			{
				saveData.put(tDbCode, bean.get(tCode));
			}
		}
		return saveData;
	}
}
