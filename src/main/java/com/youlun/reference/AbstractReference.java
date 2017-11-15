package com.youlun.reference;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractReference {
	 
	protected JSONObject param = null;
	
	public void initial(JSONObject param)
	{
		this.param = param;
	}
	
	public abstract JSONObject getMeta();
	
	public abstract JSONArray getData(); 
	
	public abstract JSONArray getRefText();
	
	public JSONObject invoke(JSONObject param)
	{
		JSONObject ret = new JSONObject();
		String action = param.getString("action");
		this.initial(param);
		if("meta".equals(action))
		{   
			ret.put("meta", this.getMeta()); 
		}else if("text".equals(action))
		{  
			ret.put("meta", this.getMeta()); 
			ret.put("data", this.getRefText()); 
		}else if("data".equals(action))
		{
			ret.put("data", this.getData()); 
		}
		return ret;
	}
	
	

}
