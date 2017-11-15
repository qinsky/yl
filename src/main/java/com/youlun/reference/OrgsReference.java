package com.youlun.reference;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;

public class OrgsReference extends AbstractReference{

	@Override
	public JSONObject getMeta()
	{
		JSONObject meta = new JSONObject();
		meta.put("title", this.getRefTitle());
		meta.put("type", "tree");
		meta.put("fields", getFields()); 
		meta.put("autoLoad", true);
		return meta;
	}
	 
	public String getRefTitle()
	{
		return "组织引用";
	} 
	
	public JSONArray getFields() {
		// TODO Auto-generated method stub
		JSONArray fields = new JSONArray();
		
		JSONObject field  = new JSONObject(); 
		field.put("field", "code");
		field.put("title", "编码");   
		fields.put(field);
		
		field  = new JSONObject();
		field.put("field", "name");
		field.put("title", "实体名称"); 
		field.put("refText", true); 
		fields.put(field); 
		
		field  = new JSONObject();
		field.put("field", "id");
		field.put("title", "主键"); 
		field.put("refValue", true); 
		field.put("hidden", true);
		
		fields.put(field); 
		
		return fields;
	}
 
	@Override
	public JSONArray getData() {
		// TODO Auto-generated method stub
		/**
		 * type:  1、集团;2、公司;3、部门;5、岗位
		 * */ 
		return DBOperator.query("select name,code,id from ul_bsf_org where df='N' and type in ('1','2','3','6')");
	}
	
	@Override
	public JSONArray getRefText() {
		// TODO Auto-generated method stub
		JSONArray ret = new JSONArray();
		JSONArray ids  = null;
		if(!param.has("ids") || (ids=param.getJSONArray("ids")).length()==0)
		{
			return ret;
		}
		 
		Object[] params = new Object[ids.length()];
		StringBuffer sql = new StringBuffer("select id ,name  from ul_bsf_org where id in(");
		for(int i=0;i<ids.length();i++)
		{
			sql.append("?,");
			params[i] = ids.get(i);
		}
		sql.delete(sql.length()-1, sql.length());
		sql.append(")");
		
		return DBOperator.query(sql.toString(), params); 
	}

}
