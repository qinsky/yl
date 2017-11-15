package com.youlun.reference;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;

public class BusiReference extends AbstractReference{
	
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
 
	public JSONArray getFields() {
		// TODO Auto-generated method stub
		JSONArray fields = new JSONArray();
		JSONObject field  = null;
//		field = new JSONObject(); 
//		field.put("field", "code");
//		field.put("title", "编码");   
//		fields.put(field);
		
		field  = new JSONObject();
		field.put("field", "name");
		field.put("title", "业务名称"); 
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
		return DBOperator.query("select id,name,parent_id from ul_bsf_busitype where df!='Y'");
	}
 
	public String getRefTitle() {
		// TODO Auto-generated method stub
		return "业务引用";
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
		StringBuffer sql = new StringBuffer("select id ,name from ul_bsf_busitype where id in(");
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
