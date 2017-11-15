package com.youlun.meta.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.meta.MetaBusinessUtils;

@Component("meta")
@Scope("prototype")
public class MetaBusinessHandler extends DefaultBusinessHandler {
	
	public void qUIMeta()
	{
		JSONObject billData = params.getJSONObject("busiData");
		if(!billData.has("busiCode"))
		{
			this.error("缺失参数:busiCode");
			return;
		}
		String busiCode = billData.getString("busiCode");
		JSONObject busi = BusinessConfig.getBusitype(busiCode);
		
		String metaID = busi.getString("meta_id");
		if(StringUtils.isEmpty(metaID))
		{
			//this.error("业务功能("+busi.getString("name")+")没有关联元数据!");
			success(new JSONObject());
			return;
		}
		
		//查询元数据
		billData = MetaBusinessUtils.getUIMeta(metaID);
		//返回元数据
		success(billData);
	}
	
	public void bt()
	{
		JSONObject billData = params.getJSONObject("busiData");
		if(!billData.has("id"))
		{
			this.error("缺失参数:id");
			return;
		}
		String metaId = billData.getString("id"); 
		 
		//查询元数据
		billData = MetaBusinessUtils.getDBMeta(metaId);
		 
		List<String> lsSql = generalSql(billData);
		for(int i=0;i<lsSql.size();i++)
		{
			log.debug(lsSql.get(i));
			DBOperator.executeSql(lsSql.get(i));
		}
		//返回元数据
		success(billData);
	}
	
	private List<String> generalSql(JSONObject meta)
	{
		List<String> lsSql = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("create table ");
		sql.append(meta.get("tableName"));
		sql.append("( ");
		
		JSONArray fields = meta.getJSONArray("columns");
		JSONObject field = null;
		for(int i=0;i<fields.length();i++)
		{
			field = fields.getJSONObject(i);
			if(!field.has("type"))
			{
				continue;
			}
			String type = field.getString("type");
			if("char".equals(type))
			{
				sql.append(field.get("column")).append(" ").append("varchar(");
				sql.append(field.get("length")).append(") ");
				if(field.has("isId")&& field.getBoolean("isId"))
				{
					sql.append(" primary key ");
				}
				if(field.has("isUnique") &&  field.getBoolean("isUnique"))
				{
					sql.append(" unique ");
				}
				if(field.has("isNull") &&  !field.getBoolean("isNull"))
				{
					sql.append(" not null ");
				}
				if(field.has("default"))
				{
					sql.append("default ").append("'").append(field.get("default")).append("'");
				}
				sql.append(",");
			}else if("number".equals(type))
			{
				sql.append(field.get("column")).append(" ").append("varchar(");
				sql.append(field.get("length")); 
				
				//数字类型设置精度
				sql.append(field.has("precision")?field.get("precision"):0);
				 
				sql.append(") ");
				if(field.has("isNull") &&  !field.getBoolean("isNull"))
				{
					sql.append(" not null ");
				}
				if(field.has("default"))
				{
					sql.append("default ").append(field.get("default"));
				}
				sql.append(",");
			}
		}
		sql.append(" df varchar(1) default 'N' ");
		sql.append(")");
		lsSql.add(sql.toString());
		
		if(meta.has("children"))
		{
			JSONArray children = meta.getJSONArray("children");
			for(int i=0;i<children.length();i++)
			{
				lsSql.addAll(generalSql(children.getJSONObject(i)));
			}
		}
		return lsSql;
	}
}
