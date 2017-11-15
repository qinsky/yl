package com.youlun.meta;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.ExceptionUtils;

public class MetaBusinessUtils {
	
	/**
	 * 通过菜单ID获取关联菜单的功能的元数据
	 * @return
	 */
	public static JSONObject getDbMetaByMenuId(String menuId)
	{
		StringBuffer buf = new StringBuffer("select  a.id from ul_bsf_meta a,ul_bsf_menu b,ul_bsf_busitype c"); 
		buf.append(" where a.id=c.meta_id and b.busitype_id=c.id and a.df!='Y' and b.df!='Y' and c.df!='Y' and b.id=?");
		JSONObject bean = DBOperator.queryForSingle(buf.toString(), new String[]{menuId});
		if(bean==null)
		{
			return null;
		}
		
		return getDBMeta(bean.getString("id")); 
	}
	
	/**
	 * 通过元数据ID获取界面元数据
	 * @param metaID
	 * @return
	 */
	public static JSONObject getUIMeta(String metaID)
	{
		JSONObject meta = null;
		JSONObject bsfMeta = DBOperator.queryColumnsByPrimaryKey("ul_bsf_meta", metaID, new String[]{"meta_info"});
		if(bsfMeta==null)
		{
			ExceptionUtils.throwBusinessException("没有获取到元数据,metaID:"+metaID);
		}
		
		meta = new JSONObject(bsfMeta.getString("meta_info"));
		getUIMeta(meta);
		return meta;
	}
	
	/**
	 * 通过单据元数据信息,获取界面元数据
	 * @param meta
	 * @return
	 */
	public static JSONObject getUIMeta(JSONObject meta)
	{
		meta.remove("dbInfo");
		if(meta.has("fields"))
		{
			JSONArray fields = meta.getJSONArray("fields");
			for(int i=0;i<fields.length();i++)
			{
				fields.getJSONObject(i).remove("dbInfo");
			}
		}
		if(meta.has("children"))
		{
			JSONArray children = meta.getJSONArray("children");
			for(int i=0;i<children.length();i++)
			{
				getUIMeta(children.getJSONObject(i));
			}
		}
		return meta;
	}

	/**
	 * 通过元数据ID获取数据库元数据
	 * @param metaID
	 * @return
	 */
	public static JSONObject getDBMeta(String metaID)
	{
		JSONObject meta = null;
		JSONObject bsfMeta = DBOperator.queryColumnsByPrimaryKey("ul_bsf_meta", metaID, new String[]{"meta_info"});
		if(bsfMeta==null)
		{
			ExceptionUtils.throwBusinessException("没有获取到元数据,metaID:"+metaID);
		}
		
		//提取元数据中的映射数据库的表、字段描述信息
		String info = bsfMeta.getString("meta_info");
		JSONObject metaInfo = new JSONObject(info);
		meta =  getDBMeta(metaInfo);
		return meta;
	}
	
	/**
	 * 通过单据元数据获取数据库元数据
	 * @param metaInfo
	 * @return
	 */
	public static JSONObject getDBMeta(JSONObject metaInfo)
	{	
		if(!metaInfo.has("dbInfo"))
		{
			return null;
		}
		JSONObject meta = metaInfo.getJSONObject("dbInfo");
		if(metaInfo.has("fields"))
		{
			if(metaInfo.has("code"))
			{
				meta.put("uiCode", metaInfo.get("code"));
			}
			JSONArray array = metaInfo.getJSONArray("fields");
			JSONArray columns = new JSONArray();
			meta.put("columns", columns);
			JSONObject column = null;
			JSONObject field = null;
			for(int i=0;i<array.length();i++)
			{
				field = array.getJSONObject(i);
				if(field.has("dbInfo"))
				{
					column = field.getJSONObject("dbInfo");
					column.put("uiField", field.get("field"));
					columns.put(column);
				}
			}
			
			if(metaInfo.has("children"))
			{
				JSONArray children = new JSONArray();
				array = metaInfo.getJSONArray("children");
				JSONObject child = null;
				for(int i=0;i<array.length();i++)
				{
					child = array.getJSONObject(i);
					child = getDBMeta(child);
					if(child!=null)
					{ 
						children.put(child);
					}
				}
				
				if(children.length()>0)
				{
					meta.put("children", children);
				}
			} 
			return meta; 
		}
		return null;
	}
}
